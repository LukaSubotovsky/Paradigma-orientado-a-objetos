package Controlador;

import Modelo.*;
import java.time.*;
import java.util.*;
import java.math.BigDecimal;
public class ControladorDatos extends ControladorBase {
    public ControladorDatos(Barrio barrio, Sesion sesion, Clock reloj) { super(barrio, sesion, reloj); }
    public List<Lote> lotes() throws ReglaNegocioException { admin(); return barrio().obtenerLotes(); }
    public List<Persona> personas() throws ReglaNegocioException { admin(); return barrio().getPersonas(); }
    public List<Personal> personal() throws ReglaNegocioException { admin(); return barrio().getPersonal(); }
    public List<Equipo> equipos() throws ReglaNegocioException { admin(); return barrio().obtenerEquipos(); }
    public void registrarPersona(String nombre, String apellido, String dni, String telefono, String correo) throws ReglaNegocioException {
        admin(); barrio().agregarPersona(new Persona(nombre, apellido, dni, telefono, correo));
    }
    public Lote registrarLote(int numero, BigDecimal superficie, String dni) throws ReglaNegocioException {
        admin(); return barrio().crearLote(numero, superficie, barrio().persona(dni), hoy());
    }
    public void agregarVinculo(int numero, String dni, RolLote rol, LocalDate hasta) throws ReglaNegocioException {
        admin(); barrio().lote(numero).agregarVinculo(barrio().persona(dni), rol, hoy(), hasta);
    }
    public void registrarPersonal(Personal personal) throws ReglaNegocioException {
        admin(); Validacion.exigir(!personal.getFechaIngreso().isAfter(hoy()), "Ingreso laboral futuro"); barrio().agregarPersonal(personal);
    }
    public void registrarUsuario(String nombre, String clave, RolUsuario rol, String dni, Integer legajo) throws ReglaNegocioException {
        admin();
        if (rol == RolUsuario.VECINO) barrio().persona(dni);
        else if (rol == RolUsuario.ADMINISTRADOR) Validacion.exigir(barrio().getAdministrador().getDocumento().equals(dni), "Administrador incorrecto");
        else {
            Validacion.exigir(legajo != null, "Falta legajo"); Personal p = barrio().empleado(legajo);
            Validacion.exigir(rol == RolUsuario.GUARDIA ? p instanceof GuardiaDeSeguridad : p instanceof EmpleadoDeMantenimiento, "Rol incompatible con personal");
        }
        barrio().agregarUsuario(new Usuario(nombre, clave, rol, dni, legajo));
    }
}
