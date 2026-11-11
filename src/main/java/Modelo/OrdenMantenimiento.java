package Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
public class OrdenMantenimiento implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Equipo equipo;
    private final LocalDateTime fechaDeApertura;
    private final String descripcion;
    private PrioridadOrden prioridad;
    private LocalDateTime fechaDeCierre;
    private EmpleadoDeMantenimiento tecnico;
    private String descripcionReparacion;
    OrdenMantenimiento(Equipo equipo, LocalDateTime fecha, String descripcion, PrioridadOrden prioridad) {
        this.equipo = equipo; fechaDeApertura = fecha; this.descripcion = descripcion; this.prioridad = prioridad;
    }
    void asignar(EmpleadoDeMantenimiento tecnico) { this.tecnico = tecnico; }
    void elevarPrioridad() { prioridad = PrioridadOrden.ALTA; }
    void cerrar(LocalDateTime fecha, EmpleadoDeMantenimiento tecnico, String reparacion) throws ReglaNegocioException {
        Validacion.exigir(fechaDeCierre == null, "La orden ya está cerrada");
        Validacion.exigir(fecha != null && !fecha.isBefore(fechaDeApertura) && tecnico != null, "Fecha o técnico inválidos");
        String detalle = Validacion.texto(reparacion, "Reparación");
        fechaDeCierre = fecha; this.tecnico = tecnico; descripcionReparacion = detalle;
        equipo.setEstado(EstadoEquipo.OPERATIVO);
    }
    public Equipo getEquipo() { return equipo; }
    public LocalDateTime getFechaDeApertura() { return fechaDeApertura; }
    public LocalDateTime getFechaDeCierre() { return fechaDeCierre; }
    public PrioridadOrden getPrioridad() { return prioridad; }
    public EmpleadoDeMantenimiento getTecnico() { return tecnico; }
    public String getDescripcion() { return descripcion; }
    public String getDescripcionReparacion() { return descripcionReparacion; }
    public boolean estaAbierta() { return fechaDeCierre == null; }
    @Override public String toString() { return equipo.getCodigo() + " | " + fechaDeApertura + " | " + prioridad
            + " | técnico " + tecnico + " | cierre " + fechaDeCierre + " | " + descripcionReparacion; }
}
