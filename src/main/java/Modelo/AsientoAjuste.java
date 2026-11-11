package Modelo;

import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;
public class AsientoAjuste implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDate fecha;
    private final BigDecimal importe;
    private final String motivo;
    public AsientoAjuste(LocalDate fecha, BigDecimal importe, String motivo) {
        this.fecha = fecha;
        this.importe = importe;
        this.motivo = motivo;
    }
    public LocalDate getFecha() { return fecha; }
    public BigDecimal getImporte() { return importe; }
    public String getMotivo() { return motivo; }
    @Override public String toString() { return fecha + " | " + importe + " | " + motivo + ""; }
}
