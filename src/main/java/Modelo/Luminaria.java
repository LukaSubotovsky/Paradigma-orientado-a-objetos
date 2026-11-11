package Modelo;

import java.time.LocalDate;
public class Luminaria extends Equipo {
    private static final long serialVersionUID = 1L;
    private final String tipoLampara;
    private final int potencia;
    public Luminaria(String codigo, Sector sector, LocalDate fecha, String tipoLampara, int potencia) throws ReglaNegocioException {
        super(codigo, sector, fecha);
        this.tipoLampara = Validacion.texto(tipoLampara, "Tipo de lámpara");
        Validacion.exigir(potencia > 0, "Potencia inválida"); this.potencia = potencia;
    }
    @Override public PrioridadOrden prioridadOrden(int fallasEnSector) {
        return fallasEnSector > 3 ? PrioridadOrden.ALTA : PrioridadOrden.NORMAL;
    }
    public String getTipoLampara() { return tipoLampara; }
    public int getPotencia() { return potencia; }
}
