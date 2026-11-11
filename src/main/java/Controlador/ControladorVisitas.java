package Controlador;

import Modelo.*;
import java.time.*;
import java.util.*;
import java.math.BigDecimal;
public class ControladorVisitas extends ControladorBase {
    private final GestionVisitas gestor;
    public ControladorVisitas(Barrio barrio, Sesion sesion, Clock reloj) { super(barrio, sesion, reloj); gestor = new GestionVisitas(barrio); }
    public Autorizacion registrarAutorizacion(int lote, String visitante, String documento, LocalDate desde, LocalDate hasta,
            LocalTime inicio, LocalTime fin, boolean recurrente) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.VECINO);
        return gestor.registrarAutorizacion(barrio().lote(lote), sesion().persona(), visitante, documento, desde, hasta, inicio, fin, recurrente, ahora());
    }
    public Autorizacion consultarAutorizacion(String documento) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.GUARDIA); return gestor.consultarAutorizacion(documento, ahora());
    }
    public RegistroAcceso registrarIngreso(String documento) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.GUARDIA); return gestor.registrarIngreso(documento, (GuardiaDeSeguridad) sesion().personal(), ahora());
    }
    public void registrarEgreso(String documento) throws ReglaNegocioException { registrarEgresoManual(documento, "Egreso normal"); }
    public void registrarEgresoManual(String documento, String observacion) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.GUARDIA);
        gestor.registrarEgreso(documento, (GuardiaDeSeguridad) sesion().personal(), ahora(), observacion);
    }
    public List<Autorizacion> consultarHistorial() throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.GUARDIA, RolUsuario.VECINO);
        if (sesion().getUsuario().getRol() != RolUsuario.VECINO) return barrio().getAutorizaciones();
        Persona persona = sesion().persona();
        return barrio().getAutorizaciones().stream().filter(a -> a.getAutoriza() == persona).toList();
    }
}
