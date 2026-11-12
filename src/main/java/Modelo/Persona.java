package Modelo;

import java.io.Serializable;
public class Persona implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nombre, apellido, documento, telefono, correo;
    public Persona(String nombre, String apellido, String documento, String telefono, String correo)
            throws ReglaNegocioException {
        this.nombre = Validacion.texto(nombre, "Nombre");
        this.apellido = Validacion.texto(apellido, "Apellido");
        this.documento = Validacion.texto(documento, "Documento");
        this.telefono = Validacion.texto(telefono, "Teléfono");
        this.correo = Validacion.texto(correo, "Correo");
    }
    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public String getCorreo() { return correo; }
    @Override public String toString() { return documento + " - " + nombre + " " + apellido; }
}
