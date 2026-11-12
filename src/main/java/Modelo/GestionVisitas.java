package Modelo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
public class GestionVisitas {
    private final Barrio barrio;
    public GestionVisitas(Barrio barrio) { this.barrio = barrio; }
    public Autorizacion registrarAutorizacion(Lote lote, Persona persona, String nombre, String documento,
            LocalDate desde, LocalDate hasta, LocalTime inicio, LocalTime fin, boolean recurrente, LocalDateTime ahora)
            throws ReglaNegocioException {
        Validacion.exigir(barrio.obtenerLotes().contains(lote), "Lote no registrado");
        Autorizacion autorizacion = new Autorizacion(barrio.getAutorizaciones().size()+1, lote, persona, nombre,
                documento, desde, hasta, inicio, fin, recurrente, ahora);
        barrio.agregarAutorizacion(autorizacion); return autorizacion;
    }
    public Autorizacion consultarAutorizacion(String documento, LocalDateTime fecha) throws ReglaNegocioException {
        return barrio.getAutorizaciones().stream().filter(a -> a.getDocumentoVisitante().equals(documento) && a.permiteIngreso(fecha))
                .findFirst().orElseThrow(() -> new ReglaNegocioException("No hay autorización vigente disponible"));
    }
    public RegistroAcceso registrarIngreso(String documento, GuardiaDeSeguridad guardia, LocalDateTime fecha)
            throws ReglaNegocioException {
        Validacion.exigir(barrio.getPersonal().contains(guardia), "Guardia no registrado");
        for (Autorizacion a : barrio.getAutorizaciones()) if (a.getDocumentoVisitante().equals(documento)) {
            for (RegistroAcceso r : a.getAccesos()) {
                Validacion.exigir(!r.estaAdentro(), "La persona todavía está dentro del barrio");
                Validacion.exigir(!fecha.isBefore(r.getHoraEgreso()), "Ingreso anterior al último egreso");
            }
        }
        return consultarAutorizacion(documento, fecha).registrarIngreso(fecha, guardia);
    }
    public void registrarEgreso(String documento, GuardiaDeSeguridad guardia, LocalDateTime fecha, String observacion)
            throws ReglaNegocioException {
        Validacion.exigir(barrio.getPersonal().contains(guardia), "Guardia no registrado");
        for (Autorizacion a : barrio.getAutorizaciones()) if (a.getDocumentoVisitante().equals(documento)) {
            for (RegistroAcceso r : a.getAccesos()) if (r.estaAdentro()) {
                r.registrarEgreso(fecha, guardia, observacion); return;
            }
        }
        throw new ReglaNegocioException("No existe ingreso abierto para ese documento");
    }
}
