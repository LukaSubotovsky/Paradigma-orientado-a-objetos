package Controlador;

import Modelo.*;
import java.time.*;
import java.util.*;
import java.math.BigDecimal;
public class ControladorReportes extends ControladorBase {
    public ControladorReportes(Barrio barrio, Sesion sesion, Clock reloj) { super(barrio, sesion, reloj); }
    public List<Reclamo> reclamosPendientes(LocalDate desde, LocalDate hasta, TipoReclamo tipo) throws ReglaNegocioException {
        admin(); Validacion.exigir(desde != null && hasta != null && !hasta.isBefore(desde), "Fechas inválidas");
        return barrio().obtenerReclamos().stream().filter(Reclamo::estaPendiente)
                .filter(r -> !r.getFecha().toLocalDate().isBefore(desde) && !r.getFecha().toLocalDate().isAfter(hasta))
                .filter(r -> tipo == null || r.getTipo() == tipo)
                .sorted(Comparator.comparing(Reclamo::getPrioridad).thenComparing(Reclamo::getFecha)).toList();
    }
    public List<FilaMorosidad> morosidadPorLote(LocalDate hasta) throws ReglaNegocioException {
        admin(); List<FilaMorosidad> filas = new ArrayList<>();
        List<Lote> ordenados = new ArrayList<>(barrio().obtenerLotes()); Collections.sort(ordenados);
        GestionExpensas gestor = new GestionExpensas(barrio());
        for (Lote lote : ordenados) {
            gestor.validarConsulta(lote, hasta);
            int cantidad = lote.cantidadPeriodosImpagos(hasta);
            if (cantidad > 3) filas.add(new FilaMorosidad(lote, cantidad, lote.calcularDeudaCapital(), lote.calcularDeudaIntereses(hasta)));
        }
        return filas;
    }
    public List<Equipo> equiposFueraDeServicio(Sector sector) throws ReglaNegocioException {
        admin(); return barrio().obtenerEquipos().stream().filter(Equipo::estaFueraDeServicio)
                .filter(e -> sector == null || e.getSector() == sector).sorted(Comparator.comparing(Equipo::getCodigo)).toList();
    }
    public List<String> avisosAdministrador() throws ReglaNegocioException { admin(); return barrio().getAdministrador().getAvisos(); }
}
