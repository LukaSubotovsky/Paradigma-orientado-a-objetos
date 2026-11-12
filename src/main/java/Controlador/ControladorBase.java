package Controlador;

import Modelo.*;
import java.time.*;
import java.util.*;
import java.math.BigDecimal;
public abstract class ControladorBase {
    private final Barrio barrio;
    private final Sesion sesion;
    private final Clock reloj;
    protected ControladorBase(Barrio barrio, Sesion sesion, Clock reloj) { this.barrio = barrio; this.sesion = sesion; this.reloj = reloj; }
    protected Barrio barrio() { return barrio; }
    protected Sesion sesion() { return sesion; }
    protected LocalDateTime ahora() { return LocalDateTime.now(reloj); }
    protected LocalDate hoy() { return LocalDate.now(reloj); }
    protected void admin() throws ReglaNegocioException { sesion.exigirRol(RolUsuario.ADMINISTRADOR); }
}
