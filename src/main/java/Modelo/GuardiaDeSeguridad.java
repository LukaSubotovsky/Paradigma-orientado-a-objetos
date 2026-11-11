package Modelo;

import java.time.LocalDate;
public class GuardiaDeSeguridad extends Personal {
    private static final long serialVersionUID = 1L;
    private final String turno;
    private final PuestoGuardia puesto;
    public GuardiaDeSeguridad(int legajo, String nombre, LocalDate fecha, String turno, PuestoGuardia puesto)
            throws ReglaNegocioException {
        super(legajo, nombre, fecha);
        this.turno = Validacion.texto(turno, "Turno");
        Validacion.exigir(puesto != null, "Falta puesto"); this.puesto = puesto;
    }
    public String getTurno() { return turno; }
    public PuestoGuardia getPuesto() { return puesto; }
}
