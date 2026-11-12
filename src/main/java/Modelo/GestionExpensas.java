package Modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
public class GestionExpensas {
    private final Barrio barrio;
    public GestionExpensas(Barrio barrio) { this.barrio = barrio; }
    public void emitirExpensas(YearMonth periodo, BigDecimal presupuesto, LocalDate hoy) throws ReglaNegocioException {
        BigDecimal importe = Validacion.dinero(presupuesto);
        Validacion.exigir(periodo != null && !periodo.isAfter(YearMonth.from(hoy)), "Período futuro o inválido");
        Validacion.exigir(barrio.obtenerLotes().size() == 150, "Primero deben registrarse los 150 lotes");
        for (Lote lote : barrio.obtenerLotes()) {
            for (Expensa e : lote.getExpensas()) Validacion.exigir(e.getPeriodo().isBefore(periodo), "Emita períodos nuevos en orden");
            for (Pago p : lote.getPagos()) Validacion.exigir(!p.getFecha().isAfter(periodo.atDay(1)), "El período es anterior a un pago ya cargado");
        }
        barrio.calcularCoeficientes();
        List<Lote> lotes = new ArrayList<>(barrio.obtenerLotes()); lotes.sort(Comparator.naturalOrder());
        // Truncamos a centavos y repartimos el resto por número de lote. La suma coincide con el presupuesto.
        List<BigDecimal> importes = new ArrayList<>(); BigDecimal suma = BigDecimal.ZERO;
        BigDecimal superficieTotal = lotes.stream().map(Lote::getSuperficie).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (Lote lote : lotes) {
            BigDecimal valor = importe.multiply(lote.getSuperficie()).divide(superficieTotal, 2, RoundingMode.DOWN);
            importes.add(valor); suma = suma.add(valor);
        }
        int centavos = importe.subtract(suma).movePointRight(2).intValueExact();
        for (int i = 0; i < lotes.size(); i++) {
            BigDecimal valor = importes.get(i).add(i < centavos ? new BigDecimal("0.01") : BigDecimal.ZERO);
            lotes.get(i).agregarExpensa(new Expensa(periodo, valor));
        }
        verificarMorosidad(hoy);
    }
    public Pago registrarPago(Lote lote, BigDecimal importe, MedioDePago medio, LocalDate fecha, LocalDate hoy)
            throws ReglaNegocioException {
        validarFecha(lote, fecha, hoy);
        Pago pago = new Pago(lote.getPagos().size() + 1, importe, medio, fecha);
        List<Expensa> simuladas = copiarExpensas(lote);
        reconstruir(lote, simuladas, null, BigDecimal.ZERO);
        List<AplicacionPago> aplicaciones = imputar(simuladas, importe, fecha);
        imputar(lote.getExpensas(), importe, fecha);
        pago.guardarAplicaciones(aplicaciones, true); lote.agregarPago(pago); lote.verificarMorosidad(hoy);
        return pago;
    }
    public void registrarAjuste(Lote lote, Pago pago, BigDecimal importe, String motivo, LocalDate fecha, LocalDate hoy)
            throws ReglaNegocioException {
        Validacion.exigir(lote.getPagos().contains(pago), "Pago ajeno al lote");
        validarFecha(lote, fecha, hoy);
        String detalle = Validacion.texto(motivo, "Motivo");
        Validacion.exigir(importe != null && importe.signum() != 0 && importe.scale() <= 2, "Ajuste inválido");
        Validacion.exigir(pago.importeCorregido().add(importe).signum() >= 0, "El pago corregido no puede ser negativo");
        // Validar todo el historial antes de modificar el original: un ajuste puede afectar pagos posteriores.
        List<Expensa> simuladas = copiarExpensas(lote);
        reconstruir(lote, simuladas, pago, importe);
        pago.agregarAjuste(importe, detalle, fecha);
        Map<Pago, List<AplicacionPago>> aplicaciones = reconstruir(lote, lote.getExpensas(), null, BigDecimal.ZERO);
        aplicaciones.forEach((p, lista) -> p.guardarAplicaciones(lista, false));
        lote.verificarMorosidad(hoy);
    }
    private List<Expensa> copiarExpensas(Lote lote) throws ReglaNegocioException {
        List<Expensa> copia = new ArrayList<>();
        for (Expensa e : lote.getExpensas()) copia.add(new Expensa(e.getPeriodo(), e.getImporte()));
        return copia;
    }
    private Map<Pago, List<AplicacionPago>> reconstruir(Lote lote, List<Expensa> expensas, Pago modificado, BigDecimal ajuste)
            throws ReglaNegocioException {
        expensas.forEach(Expensa::reiniciar);
        Map<Pago, List<AplicacionPago>> resultado = new LinkedHashMap<>();
        for (Pago p : lote.getPagos()) {
            BigDecimal total = p.importeCorregido().add(p == modificado ? ajuste : BigDecimal.ZERO);
            resultado.put(p, imputar(expensas, total, p.getFecha()));
        }
        return resultado;
    }
    private List<AplicacionPago> imputar(List<Expensa> expensas, BigDecimal importe, LocalDate fecha)
            throws ReglaNegocioException {
        List<Expensa> ordenadas = new ArrayList<>(expensas); ordenadas.sort(Comparator.naturalOrder());
        BigDecimal deuda = ordenadas.stream().filter(e -> !e.getPeriodo().atDay(1).isAfter(fecha))
                .map(e -> e.deuda(fecha)).reduce(BigDecimal.ZERO, BigDecimal::add);
        Validacion.exigir(importe.compareTo(deuda) <= 0, "El pago supera la deuda; no se admite saldo a favor");
        List<AplicacionPago> aplicaciones = new ArrayList<>(); BigDecimal restante = importe;
        for (Expensa e : ordenadas) {
            if (restante.signum() == 0) break;
            if (e.getPeriodo().atDay(1).isAfter(fecha)) continue;
            BigDecimal interes = new CalculadorIntereses().calcular(e, fecha).min(restante);
            restante = restante.subtract(interes);
            BigDecimal capital = e.calcularSaldo().min(restante); restante = restante.subtract(capital);
            if (interes.add(capital).signum() > 0) {
                e.aplicar(interes, capital, fecha);
                aplicaciones.add(new AplicacionPago(e.getPeriodo(), interes, capital));
            }
        }
        return aplicaciones;
    }
    public void validarConsulta(Lote lote, LocalDate fecha) throws ReglaNegocioException {
        Validacion.exigir(fecha != null, "Falta fecha");
        for (Pago p : lote.getPagos()) {
            Validacion.exigir(!fecha.isBefore(p.getFecha()), "La consulta es anterior al último movimiento");
            for (AsientoAjuste a : p.getAjustes()) Validacion.exigir(!fecha.isBefore(a.getFecha()), "La consulta es anterior a un ajuste");
        }
        for (Expensa e : lote.getExpensas()) Validacion.exigir(!fecha.isBefore(e.getPeriodo().atDay(1)), "La consulta es anterior a una emisión");
    }
    private void validarFecha(Lote lote, LocalDate fecha, LocalDate hoy) throws ReglaNegocioException {
        Validacion.exigir(barrio.obtenerLotes().contains(lote), "Lote no registrado");
        Validacion.exigir(fecha != null && !fecha.isAfter(hoy), "Fecha futura o inválida");
        validarConsulta(lote, fecha);
    }
    public void verificarMorosidad(LocalDate fecha) { barrio.obtenerLotes().forEach(l -> l.verificarMorosidad(fecha)); }
}
