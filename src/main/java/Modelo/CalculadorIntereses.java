package Modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
public class CalculadorIntereses {
    private final Configuracion configuracion = new Configuracion();
    public BigDecimal calcular(Expensa expensa, LocalDate fecha) {
        long dias = Math.max(0, ChronoUnit.DAYS.between(expensa.getFechaCalculo(), fecha));
        BigDecimal interes = expensa.calcularSaldo().multiply(configuracion.getTasaMora())
                .multiply(BigDecimal.valueOf(dias)).divide(BigDecimal.valueOf(30), 2, RoundingMode.HALF_UP);
        return expensa.getInteresPendiente().add(interes);
    }
}
