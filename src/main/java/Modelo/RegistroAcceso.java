package Modelo;

import java.io.Serializable;
import java.time.LocalDateTime;
public class RegistroAcceso implements Serializable {
    private static final long serialVersionUID = 1L;
    private final LocalDateTime horaIngreso;
    private LocalDateTime horaEgreso;
    private final GuardiaDeSeguridad guardiaIngreso;
    private GuardiaDeSeguridad guardiaEgreso;
    private String observacionEgreso;
    public RegistroAcceso(LocalDateTime hora, GuardiaDeSeguridad guardia) throws ReglaNegocioException {
        Validacion.exigir(hora != null && guardia != null, "Ingreso incompleto");
        horaIngreso = hora; guardiaIngreso = guardia;
    }
    public void registrarEgreso(LocalDateTime hora, GuardiaDeSeguridad guardia, String observacion) throws ReglaNegocioException {
        Validacion.exigir(estaAdentro(), "El egreso ya fue registrado");
        Validacion.exigir(hora != null && !hora.isBefore(horaIngreso) && guardia != null, "Egreso inválido");
        observacionEgreso = Validacion.texto(observacion, "Observación de egreso");
        horaEgreso = hora; guardiaEgreso = guardia;
    }
    public boolean estaAdentro() { return horaEgreso == null; }
    public LocalDateTime getHoraIngreso() { return horaIngreso; }
    public LocalDateTime getHoraEgreso() { return horaEgreso; }
    public GuardiaDeSeguridad getGuardiaIngreso() { return guardiaIngreso; }
    public GuardiaDeSeguridad getGuardiaEgreso() { return guardiaEgreso; }
    public String getObservacionEgreso() { return observacionEgreso; }
    @Override public String toString() { return "Ingreso " + horaIngreso + " | egreso " + horaEgreso + " | " + observacionEgreso; }
}
