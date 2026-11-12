package Controlador;

import Modelo.*;
import java.time.*;
import java.util.*;
import java.math.BigDecimal;
public class ControladorEquipos extends ControladorBase {
    private final GestionEquipos gestor;
    public ControladorEquipos(Barrio barrio, Sesion sesion, Clock reloj) { super(barrio, sesion, reloj); gestor = new GestionEquipos(barrio); }
    public void registrarEquipo(Equipo equipo) throws ReglaNegocioException {
        admin(); Validacion.exigir(!equipo.getFechaInstalacion().isAfter(hoy()), "Instalación futura"); gestor.registrarEquipo(equipo);
    }
    public OrdenMantenimiento registrarFalla(String codigo, String descripcion) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.VECINO, RolUsuario.GUARDIA, RolUsuario.ADMINISTRADOR);
        return gestor.registrarFalla(barrio().equipo(codigo), descripcion, sesion().getUsuario().getNombre(), ahora());
    }
    public void iniciarReparacion(String codigo, int legajo) throws ReglaNegocioException {
        admin(); Personal personal = barrio().empleado(legajo);
        Validacion.exigir(personal instanceof EmpleadoDeMantenimiento, "Seleccione un técnico de mantenimiento");
        gestor.iniciarReparacion(barrio().equipo(codigo), (EmpleadoDeMantenimiento) personal);
    }
    public void cerrarOrden(String codigo, String reparacion) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.MANTENIMIENTO);
        gestor.cerrarOrden(gestor.ordenAbierta(barrio().equipo(codigo)), ahora(), (EmpleadoDeMantenimiento) sesion().personal(), reparacion);
    }
    public void retirarEquipo(String codigo) throws ReglaNegocioException { admin(); gestor.retirarEquipo(barrio().equipo(codigo)); }
    public Equipo consultarEstado(String codigo) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.GUARDIA, RolUsuario.MANTENIMIENTO);
        return barrio().equipo(codigo);
    }
}
