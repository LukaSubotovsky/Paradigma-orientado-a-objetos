package Modelo;

import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;
public class Comentario implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDateTime fecha;
    private final String texto;
    private final String autor;
    public Comentario(LocalDateTime fecha, String texto, String autor) {
        this.fecha = fecha;
        this.texto = texto;
        this.autor = autor;
    }
    public LocalDateTime getFecha() { return fecha; }
    public String getTexto() { return texto; }
    public String getAutor() { return autor; }
    @Override public String toString() { return fecha + " | " + texto + " | " + autor + ""; }
}
