package Modelo;

import java.time.LocalDate;
public class EmpleadoDeMantenimiento extends Personal {
    private static final long serialVersionUID = 1L;
    private final Especialidad especialidad;
    public EmpleadoDeMantenimiento(int legajo, String nombre, LocalDate fecha, Especialidad especialidad)
            throws ReglaNegocioException {
        super(legajo, nombre, fecha);
        Validacion.exigir(especialidad != null, "Falta especialidad"); this.especialidad = especialidad;
    }
    public Especialidad getEspecialidad() { return especialidad; }
}
