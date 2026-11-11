package Modelo;

import java.time.LocalDate;
public class Camara extends Equipo {
    private static final long serialVersionUID = 1L;
    private final int resolucion, diasGrabados;
    public Camara(String codigo, Sector sector, LocalDate fecha, int resolucion, int diasGrabados) throws ReglaNegocioException {
        super(codigo, sector, fecha);
        Validacion.exigir(resolucion > 0 && diasGrabados > 0, "Resolución y días deben ser positivos");
        this.resolucion = resolucion; this.diasGrabados = diasGrabados;
    }
    @Override public PrioridadOrden prioridadOrden(int fallasEnSector) { return PrioridadOrden.ALTA; }
    public int getResolucion() { return resolucion; }
    public int getDiasGrabados() { return diasGrabados; }
}
