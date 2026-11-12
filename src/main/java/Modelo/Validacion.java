package Modelo;

import java.math.BigDecimal;
import java.math.RoundingMode;
public final class Validacion {
    private Validacion() {}
    public static void exigir(boolean condicion, String mensaje) throws ReglaNegocioException {
        if (!condicion) throw new ReglaNegocioException(mensaje);
    }
    public static String texto(String valor, String campo) throws ReglaNegocioException {
        exigir(valor != null && !valor.isBlank(), campo + " es obligatorio");
        return valor.trim();
    }
    public static BigDecimal dinero(BigDecimal valor) throws ReglaNegocioException {
        exigir(valor != null && valor.signum() > 0, "El importe debe ser positivo");
        exigir(valor.scale() <= 2, "Use como máximo dos decimales");
        return valor.setScale(2, RoundingMode.HALF_UP);
    }
}
