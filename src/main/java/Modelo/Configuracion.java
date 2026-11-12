package Modelo;

import java.io.Serializable;
import java.math.BigDecimal;
public class Configuracion implements Serializable {
    private static final long serialVersionUID = 1L;
    public BigDecimal getTasaMora() { return new BigDecimal("0.03"); }
    public int getDiaVencimiento() { return 10; }
    public int getPlazoReapertura() { return 5; }
    public int getHorasEscalamiento() { return 24; }
    public int getMaxEnCurso() { return 5; }
}
