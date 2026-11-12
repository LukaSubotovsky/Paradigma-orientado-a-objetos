package Modelo;

import java.io.Serializable;
import java.time.LocalDate;
public abstract class Personal implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int legajo;
    private final String nombre;
    private final LocalDate fechaIngreso;
    protected Personal(int legajo, String nombre, LocalDate fechaIngreso) throws ReglaNegocioException {
        Validacion.exigir(legajo > 0 && fechaIngreso != null, "Legajo y fecha inválidos");
        this.legajo = legajo;
        this.nombre = Validacion.texto(nombre, "Nombre");
        this.fechaIngreso = fechaIngreso;
    }
    public int getLegajo() { return legajo; }
    public String getNombre() { return nombre; }
    public LocalDate getFechaIngreso() { return fechaIngreso; }
    @Override public String toString() { return legajo + " - " + nombre; }
}
