package Modelo;

import java.io.Serializable;
import java.time.*;
import java.math.BigDecimal;
public class FilaMorosidad implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Lote lote;
    private final int periodosImpagos;
    private final BigDecimal deudaCapital;
    private final BigDecimal deudaIntereses;
    public FilaMorosidad(Lote lote, int periodosImpagos, BigDecimal deudaCapital, BigDecimal deudaIntereses) {
        this.lote = lote;
        this.periodosImpagos = periodosImpagos;
        this.deudaCapital = deudaCapital;
        this.deudaIntereses = deudaIntereses;
    }
    public Lote getLote() { return lote; }
    public int getPeriodosImpagos() { return periodosImpagos; }
    public BigDecimal getDeudaCapital() { return deudaCapital; }
    public BigDecimal getDeudaIntereses() { return deudaIntereses; }
    @Override public String toString() { return lote + " | " + periodosImpagos + " | " + deudaCapital + " | " + deudaIntereses + ""; }
}
