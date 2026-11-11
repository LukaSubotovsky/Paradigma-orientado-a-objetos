package Controlador;

import Modelo.*;
import java.time.*;
import java.util.*;
import java.math.BigDecimal;
public class ControladorExpensas extends ControladorBase {
    private final GestionExpensas gestor;
    public ControladorExpensas(Barrio barrio, Sesion sesion, Clock reloj) { super(barrio, sesion, reloj); gestor = new GestionExpensas(barrio); }
    public void emitirExpensas(YearMonth periodo, BigDecimal presupuesto) throws ReglaNegocioException {
        admin(); gestor.emitirExpensas(periodo, presupuesto, hoy());
    }
    public Lote consultarEstadoCuenta(int numero, LocalDate fecha) throws ReglaNegocioException {
        admin(); Lote lote = barrio().lote(numero); gestor.validarConsulta(lote, fecha); return lote;
    }
    public Pago registrarPago(int numero, BigDecimal importe, MedioDePago medio, LocalDate fecha) throws ReglaNegocioException {
        admin(); return gestor.registrarPago(barrio().lote(numero), importe, medio, fecha, hoy());
    }
    public void registrarAjuste(int numero, int numeroPago, BigDecimal importe, String motivo) throws ReglaNegocioException {
        admin(); Lote lote = barrio().lote(numero);
        Pago pago = lote.getPagos().stream().filter(p -> p.getNumero() == numeroPago).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("Pago inexistente"));
        gestor.registrarAjuste(lote, pago, importe, motivo, hoy(), hoy());
    }
    public List<NotificacionMora> consultarNotificaciones(int numero) throws ReglaNegocioException {
        admin(); return barrio().lote(numero).getNotificaciones();
    }
}
