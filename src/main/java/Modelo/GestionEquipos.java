package Modelo;

import java.time.LocalDateTime;
public class GestionEquipos {
    private final Barrio barrio;
    public GestionEquipos(Barrio barrio) { this.barrio = barrio; }
    public void registrarEquipo(Equipo equipo) throws ReglaNegocioException {
        Validacion.exigir(barrio.obtenerEquipos().stream().noneMatch(e -> e.getCodigo().equals(equipo.getCodigo())), "Código duplicado");
        barrio.agregarEquipo(equipo);
    }
    public OrdenMantenimiento registrarFalla(Equipo equipo, String descripcion, String autor, LocalDateTime fecha)
            throws ReglaNegocioException {
        Validacion.exigir(barrio.obtenerEquipos().contains(equipo), "Equipo no registrado");
        equipo.registrarFalla(descripcion, autor, fecha);
        int cantidad = contarLuminariasFalladas(equipo.getSector());
        OrdenMantenimiento orden = new OrdenMantenimiento(equipo, fecha, descripcion, equipo.prioridadOrden(cantidad));
        equipo.agregarOrden(orden);
        // Cada equipo decide su prioridad. Una cuarta luminaria eleva también las órdenes anteriores.
        for (Equipo e : barrio.obtenerEquipos()) {
            if (e.getSector() == equipo.getSector() && e.prioridadOrden(cantidad) == PrioridadOrden.ALTA)
                for (OrdenMantenimiento o : e.getOrdenes()) if (o.estaAbierta()) o.elevarPrioridad();
        }
        return orden;
    }
    private int contarLuminariasFalladas(Sector sector) {
        return (int) barrio.obtenerEquipos().stream().filter(e -> e instanceof Luminaria && e.getSector() == sector
                && (e.getEstado() == EstadoEquipo.CON_FALLA || e.getEstado() == EstadoEquipo.EN_REPARACION)).count();
    }
    public OrdenMantenimiento ordenAbierta(Equipo equipo) throws ReglaNegocioException {
        return equipo.getOrdenes().stream().filter(OrdenMantenimiento::estaAbierta).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("El equipo no tiene una orden abierta"));
    }
    public void iniciarReparacion(Equipo equipo, EmpleadoDeMantenimiento tecnico) throws ReglaNegocioException {
        Validacion.exigir(barrio.getPersonal().contains(tecnico), "Técnico no registrado");
        ordenAbierta(equipo).asignar(tecnico); equipo.setEstado(EstadoEquipo.EN_REPARACION);
    }
    public void cerrarOrden(OrdenMantenimiento orden, LocalDateTime fecha, EmpleadoDeMantenimiento tecnico, String descripcion)
            throws ReglaNegocioException {
        Validacion.exigir(barrio.getPersonal().contains(tecnico), "Técnico no registrado");
        Validacion.exigir(orden.getTecnico() == tecnico, "Debe cerrar el técnico asignado");
        orden.cerrar(fecha, tecnico, descripcion);
    }
    public void retirarEquipo(Equipo equipo) throws ReglaNegocioException {
        ordenAbierta(equipo); equipo.setEstado(EstadoEquipo.FUERA_DE_SERVICIO);
    }
    public void verificarCamaraSinFuncionar(LocalDateTime fecha) {
        for (Equipo e : barrio.obtenerEquipos()) {
            if (e instanceof Camara && e.tieneFallaActiva() && !e.isAvisado() && !e.getFallas().isEmpty()
                    && fecha.isAfter(e.getFallas().get(e.getFallas().size()-1).getFecha().plusHours(48))) {
                barrio.getAdministrador().recibirAviso(fecha + " Cámara sin funcionar por más de 48 horas: " + e.getCodigo());
                e.marcarAvisado();
            }
        }
    }
}
