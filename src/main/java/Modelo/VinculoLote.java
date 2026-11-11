package Modelo;

import java.io.Serializable;
import java.time.LocalDate;
public class VinculoLote implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Persona persona;
    private final RolLote rol;
    private final LocalDate fechaDesde, fechaHasta;
    public VinculoLote(Persona persona, RolLote rol, LocalDate desde, LocalDate hasta)
            throws ReglaNegocioException {
        Validacion.exigir(persona != null && rol != null && desde != null, "Vínculo incompleto");
        Validacion.exigir(hasta == null || !hasta.isBefore(desde), "Fechas inválidas");
        this.persona = persona; this.rol = rol; fechaDesde = desde; fechaHasta = hasta;
    }
    public boolean estaVigente(LocalDate fecha) {
        return !fecha.isBefore(fechaDesde) && (fechaHasta == null || !fecha.isAfter(fechaHasta));
    }
    public Persona getPersona() { return persona; }
    public RolLote getRol() { return rol; }
}
