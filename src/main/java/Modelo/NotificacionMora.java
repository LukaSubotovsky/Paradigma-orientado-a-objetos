package Modelo;

import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;
public class NotificacionMora implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDate fecha;
    private final int periodosImpagos;
    private final Persona titular;
    public NotificacionMora(LocalDate fecha, int periodosImpagos, Persona titular) {
        this.fecha = fecha;
        this.periodosImpagos = periodosImpagos;
        this.titular = titular;
    }
    public LocalDate getFecha() { return fecha; }
    public int getPeriodosImpagos() { return periodosImpagos; }
    public Persona getTitular() { return titular; }
    @Override public String toString() { return fecha + " | " + periodosImpagos + " | " + titular + ""; }
}
