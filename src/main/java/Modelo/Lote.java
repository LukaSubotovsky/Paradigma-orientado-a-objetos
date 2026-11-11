package Modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class Lote implements Serializable, Comparable<Lote> {
    private static final long serialVersionUID = 1L;
    private final int numero;
    private final BigDecimal superficie;
    private BigDecimal coeficiente = BigDecimal.ZERO;
    private boolean moroso;
    private final List<VinculoLote> vinculos = new ArrayList<>();
    private final List<Expensa> expensas = new ArrayList<>();
    private final List<Pago> pagos = new ArrayList<>();
    private final List<NotificacionMora> notificaciones = new ArrayList<>();
    public Lote(int numero, BigDecimal superficie, Persona titular, LocalDate desde) throws ReglaNegocioException {
        Validacion.exigir(numero > 0, "Número de lote inválido");
        this.numero = numero; this.superficie = Validacion.dinero(superficie);
        vinculos.add(new VinculoLote(titular, RolLote.TITULAR, desde, null));
    }
    public void agregarVinculo(Persona persona, RolLote rol, LocalDate desde, LocalDate hasta)
            throws ReglaNegocioException {
        Validacion.exigir(rol != RolLote.TITULAR, "El titular no se cambia (S23)");
        for (VinculoLote v : vinculos) {
            Validacion.exigir(!(v.getPersona().getDocumento().equals(persona.getDocumento()) && v.getRol() == rol),
                    "La persona ya tiene un vínculo en este lote");
        }
        vinculos.add(new VinculoLote(persona, rol, desde, hasta));
    }
    public Persona obtenerTitular() { return vinculos.get(0).getPersona(); }
    public boolean puedePresentarReclamo(Persona persona, LocalDate fecha) {
        if (persona == null) return false;
        return vinculos.stream().anyMatch(v -> v.getPersona().getDocumento().equals(persona.getDocumento())
                && v.estaVigente(fecha) && (v.getRol() == RolLote.TITULAR || v.getRol() == RolLote.AUTORIZADO));
    }
    public boolean puedeAutorizarVisita(Persona persona, LocalDate fecha) {
        if (persona == null) return false;
        return vinculos.stream().anyMatch(v -> v.getPersona().getDocumento().equals(persona.getDocumento())
                && v.estaVigente(fecha) && v.getRol() != RolLote.AUTORIZADO);
    }
    public BigDecimal calcularDeudaCapital() {
        return expensas.stream().map(Expensa::calcularSaldo).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public BigDecimal calcularDeudaIntereses(LocalDate hasta) {
        CalculadorIntereses calculador = new CalculadorIntereses();
        return expensas.stream().map(e -> calculador.calcular(e, hasta)).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    public int cantidadPeriodosImpagos(LocalDate fecha) {
        return (int) expensas.stream().filter(e -> e.estaVencida(fecha)).count();
    }
    public void verificarMorosidad(LocalDate fecha) {
        int cantidad = cantidadPeriodosImpagos(fecha);
        boolean nueva = cantidad > 3;
        if (nueva && !moroso) notificaciones.add(new NotificacionMora(fecha, cantidad, obtenerTitular()));
        moroso = nueva;
    }
    void agregarExpensa(Expensa expensa) { expensas.add(expensa); }
    void agregarPago(Pago pago) { pagos.add(pago); }
    void setCoeficiente(BigDecimal valor) { coeficiente = valor; }
    public int getNumero() { return numero; }
    public BigDecimal getSuperficie() { return superficie; }
    public BigDecimal getCoeficiente() { return coeficiente; }
    public boolean isMoroso() { return moroso; }
    public List<VinculoLote> getVinculos() { return List.copyOf(vinculos); }
    public List<Expensa> getExpensas() { return List.copyOf(expensas); }
    public List<Pago> getPagos() { return List.copyOf(pagos); }
    public List<NotificacionMora> getNotificaciones() { return List.copyOf(notificaciones); }
    @Override public int compareTo(Lote otro) { return Integer.compare(numero, otro.numero); }
    @Override public String toString() { return "Lote " + numero + " | " + superficie + " m2 | " + obtenerTitular(); }
}
