package Modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
public class Autorizacion implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numero;
    private final Lote lote;
    private final Persona autoriza;
    private final String nombreVisitante, documentoVisitante;
    private final LocalDate fechaDesde, fechaHasta;
    private final LocalTime horaDesde, horaHasta;
    private final LocalDateTime creada;
    private final boolean recurrente;
    private final List<RegistroAcceso> accesos = new ArrayList<>();
    public Autorizacion(int numero, Lote lote, Persona autoriza, String nombre, String documento, LocalDate desde,
            LocalDate hasta, LocalTime horaDesde, LocalTime horaHasta, boolean recurrente, LocalDateTime creada)
            throws ReglaNegocioException {
        Validacion.exigir(desde != null && hasta != null && horaDesde != null && horaHasta != null && creada != null,
                "Complete fechas y horas");
        Validacion.exigir(!hasta.isBefore(desde) && horaDesde.isBefore(horaHasta), "Franja o fechas inválidas");
        long dias = ChronoUnit.DAYS.between(desde, hasta) + 1;
        Validacion.exigir(recurrente ? dias <= 30 : dias == 1, "Puntual: un día; recurrente: hasta 30 días inclusive");
        Validacion.exigir(!hasta.atTime(horaHasta).isBefore(creada), "La autorización ya venció");
        Validacion.exigir(lote.puedeAutorizarVisita(autoriza, creada.toLocalDate()), "Solo el titular o residente puede autorizar");
        this.numero = numero; this.lote = lote; this.autoriza = autoriza;
        nombreVisitante = Validacion.texto(nombre, "Visitante"); documentoVisitante = Validacion.texto(documento, "Documento");
        fechaDesde = desde; fechaHasta = hasta; this.horaDesde = horaDesde; this.horaHasta = horaHasta;
        this.recurrente = recurrente; this.creada = creada;
    }
    public boolean estaVigente(LocalDateTime fecha) {
        return !fecha.isBefore(creada) && !fecha.toLocalDate().isBefore(fechaDesde) && !fecha.toLocalDate().isAfter(fechaHasta)
                && !fecha.toLocalTime().isBefore(horaDesde) && !fecha.toLocalTime().isAfter(horaHasta);
    }
    public boolean permiteIngreso(LocalDateTime fecha) {
        if (!estaVigente(fecha)) return false;
        if (!recurrente) return accesos.isEmpty();
        return accesos.stream().noneMatch(a -> a.getHoraIngreso().toLocalDate().equals(fecha.toLocalDate()));
    }
    public RegistroAcceso registrarIngreso(LocalDateTime fecha, GuardiaDeSeguridad guardia) throws ReglaNegocioException {
        Validacion.exigir(permiteIngreso(fecha), "Autorización vencida, fuera de horario o ya utilizada");
        RegistroAcceso registro = new RegistroAcceso(fecha, guardia); accesos.add(registro); return registro;
    }
    public int getNumero() { return numero; }
    public Lote getLote() { return lote; }
    public Persona getAutoriza() { return autoriza; }
    public String getDocumentoVisitante() { return documentoVisitante; }
    public String getNombreVisitante() { return nombreVisitante; }
    public boolean isRecurrente() { return recurrente; }
    public List<RegistroAcceso> getAccesos() { return List.copyOf(accesos); }
    @Override public String toString() { return numero + " | lote " + lote.getNumero() + " | " + documentoVisitante + " "
            + nombreVisitante + " | " + fechaDesde + " a " + fechaHasta + " " + horaDesde + "-" + horaHasta; }
}
