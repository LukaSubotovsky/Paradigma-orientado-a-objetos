package Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
public class Reclamo implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numero;
    private final LocalDateTime fecha;
    private final TipoReclamo tipo;
    private final String descripcion;
    private final Lote lote;
    private final Persona solicitante;
    private EstadoReclamo estado = EstadoReclamo.ABIERTO;
    private Personal responsable;
    private boolean escalado;
    private boolean atendido;
    private final List<CambioEstado> historial = new ArrayList<>();
    private final List<Comentario> comentarios = new ArrayList<>();
    Reclamo(int numero, Lote lote, Persona persona, TipoReclamo tipo, String descripcion, LocalDateTime fecha)
            throws ReglaNegocioException {
        Validacion.exigir(tipo != null && fecha != null, "Reclamo incompleto");
        this.numero = numero; this.lote = lote; solicitante = persona; this.tipo = tipo;
        this.descripcion = Validacion.texto(descripcion, "Descripción"); this.fecha = fecha;
        historial.add(new CambioEstado(fecha, estado, "Alta del reclamo"));
    }
    void registrarCambio(EstadoReclamo nuevo, String observacion, LocalDateTime fecha) throws ReglaNegocioException {
        Validacion.exigir(!fecha.isBefore(getUltimoCambio()), "La fecha no puede retroceder");
        String texto = Validacion.texto(observacion, "Observación");
        estado = nuevo;
        if (nuevo == EstadoReclamo.EN_CURSO) atendido = true;
        historial.add(new CambioEstado(fecha, nuevo, texto));
    }
    void asignar(Personal personal) { responsable = personal; }
    void marcarEscalado() { escalado = true; }
    public void agregarComentario(String texto, String autor, LocalDateTime fecha) throws ReglaNegocioException {
        Validacion.exigir(!fecha.isBefore(this.fecha), "Comentario anterior al reclamo");
        comentarios.add(new Comentario(fecha, Validacion.texto(texto, "Comentario"), Validacion.texto(autor, "Autor")));
    }
    public boolean estaPendiente() { return estado != EstadoReclamo.RESUELTO && estado != EstadoReclamo.CERRADO; }
    public LocalDateTime getUltimoCambio() { return historial.get(historial.size() - 1).getFecha(); }
    public int getNumero() { return numero; }
    public LocalDateTime getFecha() { return fecha; }
    public TipoReclamo getTipo() { return tipo; }
    public PrioridadReclamo getPrioridad() { return tipo.getPrioridad(); }
    public String getDescripcion() { return descripcion; }
    public Lote getLote() { return lote; }
    public Persona getSolicitante() { return solicitante; }
    public EstadoReclamo getEstado() { return estado; }
    public Personal getResponsable() { return responsable; }
    public boolean isEscalado() { return escalado; }
    public boolean isAtendido() { return atendido; }
    public List<CambioEstado> getHistorial() { return List.copyOf(historial); }
    public List<Comentario> getComentarios() { return List.copyOf(comentarios); }
    @Override public String toString() { return numero + " | lote " + lote.getNumero() + " | " + tipo + " | " + estado + " | " + descripcion; }
}
