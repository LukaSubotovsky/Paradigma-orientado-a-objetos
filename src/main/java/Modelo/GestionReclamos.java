package Modelo;

import java.time.LocalDateTime;
public class GestionReclamos {
    private final Barrio barrio;
    private final Configuracion configuracion = new Configuracion();
    public GestionReclamos(Barrio barrio) { this.barrio = barrio; }
    public Reclamo registrar(Lote lote, Persona persona, TipoReclamo tipo, String descripcion, LocalDateTime fecha)
            throws ReglaNegocioException {
        Validacion.exigir(barrio.obtenerLotes().contains(lote) && lote.puedePresentarReclamo(persona, fecha.toLocalDate()),
                "Solo el titular o su autorizado puede reclamar");
        Reclamo reclamo = new Reclamo(barrio.obtenerReclamos().size() + 1, lote, persona, tipo, descripcion, fecha);
        barrio.agregarReclamo(reclamo); return reclamo;
    }
    public void asignar(Reclamo reclamo, Personal personal, LocalDateTime fecha) throws ReglaNegocioException {
        Validacion.exigir(reclamo.getEstado() == EstadoReclamo.ABIERTO, "Solo se asigna un reclamo abierto");
        Validacion.exigir(barrio.getPersonal().contains(personal), "Personal no registrado");
        reclamo.registrarCambio(EstadoReclamo.ASIGNADO, "Asignado a " + personal.getNombre(), fecha);
        reclamo.asignar(personal);
    }
    public void avanzarEstado(Reclamo reclamo, String observacion, LocalDateTime fecha) throws ReglaNegocioException {
        EstadoReclamo actual = reclamo.getEstado();
        Validacion.exigir(actual != EstadoReclamo.ABIERTO && actual != EstadoReclamo.CERRADO, "Estado incompatible: " + actual);
        if (actual == EstadoReclamo.ASIGNADO) {
            long cantidad = barrio.obtenerReclamos().stream().filter(r -> r.getResponsable() == reclamo.getResponsable()
                    && r.getEstado() == EstadoReclamo.EN_CURSO).count();
            Validacion.exigir(cantidad < configuracion.getMaxEnCurso(), "El empleado ya tiene cinco reclamos en curso");
        }
        reclamo.registrarCambio(EstadoReclamo.values()[actual.ordinal() + 1], observacion, fecha);
    }
    public void reabrir(Reclamo reclamo, Persona persona, String motivo, LocalDateTime fecha) throws ReglaNegocioException {
        Validacion.exigir(reclamo.getLote().puedePresentarReclamo(persona, fecha.toLocalDate()), "Vecino no habilitado");
        Validacion.exigir(reclamo.getEstado() == EstadoReclamo.RESUELTO, "Solo se reabre un reclamo resuelto");
        Validacion.exigir(!fecha.isAfter(reclamo.getUltimoCambio().plusDays(configuracion.getPlazoReapertura())), "Pasaron cinco días");
        reclamo.registrarCambio(EstadoReclamo.ASIGNADO, "Reapertura: " + Validacion.texto(motivo, "Motivo"), fecha);
    }
    public void revisarPlazos(LocalDateTime fecha) throws ReglaNegocioException {
        for (Reclamo r : barrio.obtenerReclamos()) {
            if (r.getEstado() == EstadoReclamo.RESUELTO && fecha.isAfter(r.getUltimoCambio().plusDays(configuracion.getPlazoReapertura())))
                r.registrarCambio(EstadoReclamo.CERRADO, "Cierre automático por plazo", fecha);
            if (r.getPrioridad() == PrioridadReclamo.ALTA && !r.isAtendido() && !r.isEscalado()
                    && !fecha.isBefore(r.getFecha().plusHours(configuracion.getHorasEscalamiento()))) {
                barrio.getAdministrador().recibirAviso(fecha + " Reclamo urgente sin atender: " + r.getNumero());
                r.marcarEscalado();
            }
        }
    }
}
