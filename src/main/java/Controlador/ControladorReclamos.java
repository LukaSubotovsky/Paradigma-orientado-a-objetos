package Controlador;

import Modelo.*;
import java.time.*;
import java.util.*;
import java.math.BigDecimal;
public class ControladorReclamos extends ControladorBase {
    private final GestionReclamos gestor;
    public ControladorReclamos(Barrio barrio, Sesion sesion, Clock reloj) { super(barrio, sesion, reloj); gestor = new GestionReclamos(barrio); }
    public Reclamo registrarReclamo(int lote, TipoReclamo tipo, String descripcion) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.VECINO);
        return gestor.registrar(barrio().lote(lote), sesion().persona(), tipo, descripcion, ahora());
    }
    public void asignarReclamo(int numero, int legajo) throws ReglaNegocioException {
        admin(); gestor.asignar(barrio().reclamo(numero), barrio().empleado(legajo), ahora());
    }
    public void avanzarEstado(int numero, String observacion) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.ADMINISTRADOR, RolUsuario.GUARDIA, RolUsuario.MANTENIMIENTO);
        Reclamo r = consultar(numero); gestor.avanzarEstado(r, observacion, ahora());
    }
    public void reabrirReclamo(int numero, String motivo) throws ReglaNegocioException {
        sesion().exigirRol(RolUsuario.VECINO); gestor.reabrir(consultar(numero), sesion().persona(), motivo, ahora());
    }
    public void agregarComentario(int numero, String texto) throws ReglaNegocioException {
        consultar(numero).agregarComentario(texto, sesion().getUsuario().getNombre(), ahora());
    }
    public Reclamo consultar(int numero) throws ReglaNegocioException {
        Reclamo r = barrio().reclamo(numero); sesion().consultarReclamo(r, hoy()); return r;
    }
    public List<Reclamo> listar() throws ReglaNegocioException {
        List<Reclamo> resultado = new ArrayList<>();
        for (Reclamo r : barrio().obtenerReclamos()) {
            RolUsuario rol = sesion().getUsuario().getRol();
            if (rol == RolUsuario.ADMINISTRADOR
                    || (rol == RolUsuario.VECINO && r.getLote().puedePresentarReclamo(sesion().persona(), hoy()))
                    || (rol != RolUsuario.ADMINISTRADOR && rol != RolUsuario.VECINO && r.getResponsable() == sesion().personal())) resultado.add(r);
        }
        return resultado;
    }
    public List<CambioEstado> consultarHistorial(int numero) throws ReglaNegocioException { return consultar(numero).getHistorial(); }
}
