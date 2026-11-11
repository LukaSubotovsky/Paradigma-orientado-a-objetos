package Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
public class FallaEquipo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDateTime fecha;
    private final String descripcion, reportadoPor;
    public FallaEquipo(LocalDateTime fecha, String descripcion, String reportadoPor) throws ReglaNegocioException {
        Validacion.exigir(fecha != null, "Falta fecha"); this.fecha = fecha;
        this.descripcion = Validacion.texto(descripcion, "Descripción");
        this.reportadoPor = Validacion.texto(reportadoPor, "Reportante");
    }
    public LocalDateTime getFecha() { return fecha; }
    public String getDescripcion() { return descripcion; }
    public String getReportadoPor() { return reportadoPor; }
    @Override public String toString() { return fecha + " | " + descripcion + " | " + reportadoPor; }
}
