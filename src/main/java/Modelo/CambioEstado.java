package Modelo;

import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;
public class CambioEstado implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDateTime fecha;
    private final EstadoReclamo nuevoEstado;
    private final String observaciones;
    public CambioEstado(LocalDateTime fecha, EstadoReclamo nuevoEstado, String observaciones) {
        this.fecha = fecha;
        this.nuevoEstado = nuevoEstado;
        this.observaciones = observaciones;
    }
    public LocalDateTime getFecha() { return fecha; }
    public EstadoReclamo getNuevoEstado() { return nuevoEstado; }
    public String getObservaciones() { return observaciones; }
    @Override public String toString() { return fecha + " | " + nuevoEstado + " | " + observaciones + ""; }
}
