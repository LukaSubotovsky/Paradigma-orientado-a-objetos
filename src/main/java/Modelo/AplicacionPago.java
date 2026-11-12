package Modelo;

import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;
public class AplicacionPago implements Serializable {
    private static final long serialVersionUID = 1L;
    private final YearMonth periodo;
    private final BigDecimal importeInteres;
    private final BigDecimal importeCapital;
    public AplicacionPago(YearMonth periodo, BigDecimal importeInteres, BigDecimal importeCapital) {
        this.periodo = periodo;
        this.importeInteres = importeInteres;
        this.importeCapital = importeCapital;
    }
    public YearMonth getPeriodo() { return periodo; }
    public BigDecimal getImporteInteres() { return importeInteres; }
    public BigDecimal getImporteCapital() { return importeCapital; }
    @Override public String toString() { return periodo + " | " + importeInteres + " | " + importeCapital + ""; }
}
