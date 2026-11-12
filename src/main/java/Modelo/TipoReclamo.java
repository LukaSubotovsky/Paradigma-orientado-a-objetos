package Modelo;

public enum TipoReclamo {
    SEGURIDAD(PrioridadReclamo.ALTA), MANTENIMIENTO(PrioridadReclamo.MEDIA),
    CONVIVENCIA(PrioridadReclamo.BAJA), ADMINISTRATIVO(PrioridadReclamo.BAJA);
    private final PrioridadReclamo prioridad;
    TipoReclamo(PrioridadReclamo prioridad) { this.prioridad = prioridad; }
    public PrioridadReclamo getPrioridad() { return prioridad; }
}
