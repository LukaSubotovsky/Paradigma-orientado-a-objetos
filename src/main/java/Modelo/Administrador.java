package Modelo;

import java.util.ArrayList;
import java.util.List;
public class Administrador extends Persona {
    private static final long serialVersionUID = 1L;
    private final List<String> avisos = new ArrayList<>();
    public Administrador(String nombre, String apellido, String documento, String telefono, String correo)
            throws ReglaNegocioException { super(nombre, apellido, documento, telefono, correo); }
    public void recibirAviso(String mensaje) { avisos.add(mensaje); }
    public List<String> getAvisos() { return List.copyOf(avisos); }
}
