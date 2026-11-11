package Modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public abstract class Equipo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String codigo;
    private final Sector sector;
    private final LocalDate fechaInstalacion;
    private EstadoEquipo estado = EstadoEquipo.OPERATIVO;
    private boolean avisado;
    private final List<FallaEquipo> fallas = new ArrayList<>();
    private final List<OrdenMantenimiento> ordenes = new ArrayList<>();
    protected Equipo(String codigo, Sector sector, LocalDate fecha) throws ReglaNegocioException {
        this.codigo = Validacion.texto(codigo, "Código");
        Validacion.exigir(sector != null && fecha != null, "Equipo incompleto");
        this.sector = sector; fechaInstalacion = fecha;
    }
    public abstract PrioridadOrden prioridadOrden(int fallasEnSector);
    void registrarFalla(String descripcion, String autor, LocalDateTime fecha) throws ReglaNegocioException {
        Validacion.exigir(estado == EstadoEquipo.OPERATIVO, "El equipo ya tiene una falla abierta");
        Validacion.exigir(!fecha.toLocalDate().isBefore(fechaInstalacion), "Falla anterior a la instalación");
        if (!ordenes.isEmpty()) Validacion.exigir(!fecha.isBefore(ordenes.get(ordenes.size()-1).getFechaDeCierre()), "Fecha anterior al último cierre");
        fallas.add(new FallaEquipo(fecha, descripcion, autor)); estado = EstadoEquipo.CON_FALLA; avisado = false;
    }
    void agregarOrden(OrdenMantenimiento orden) { ordenes.add(orden); }
    void setEstado(EstadoEquipo estado) { this.estado = estado; }
    void marcarAvisado() { avisado = true; }
    public boolean estaFueraDeServicio() { return estado == EstadoEquipo.FUERA_DE_SERVICIO; }
    public boolean tieneFallaActiva() { return estado != EstadoEquipo.OPERATIVO; }
    public String getCodigo() { return codigo; }
    public Sector getSector() { return sector; }
    public LocalDate getFechaInstalacion() { return fechaInstalacion; }
    public EstadoEquipo getEstado() { return estado; }
    public boolean isAvisado() { return avisado; }
    public List<FallaEquipo> getFallas() { return List.copyOf(fallas); }
    public List<OrdenMantenimiento> getOrdenes() { return List.copyOf(ordenes); }
    @Override public String toString() { return codigo + " | " + getClass().getSimpleName() + " | " + sector + " | " + estado; }
}
