package Modelo;

import java.time.LocalDate;
import java.util.Arrays;
public class Sesion {
    private final Barrio barrio;
    private final Usuario usuario;
    private Sesion(Barrio barrio, Usuario usuario) { this.barrio = barrio; this.usuario = usuario; }
    public static Sesion ingresar(Barrio barrio, String nombre, String clave) throws ReglaNegocioException {
        Usuario usuario = barrio.getUsuarios().stream().filter(u -> u.getNombre().equals(nombre) && u.validarClave(clave))
                .findFirst().orElseThrow(() -> new ReglaNegocioException("Usuario o clave incorrectos"));
        return new Sesion(barrio, usuario);
    }
    public void exigirRol(RolUsuario... roles) throws ReglaNegocioException {
        Validacion.exigir(Arrays.asList(roles).contains(usuario.getRol()), "Su rol no puede realizar esta operación");
    }
    public Persona persona() throws ReglaNegocioException { return barrio.persona(usuario.getDocumento()); }
    public Personal personal() throws ReglaNegocioException {
        Validacion.exigir(usuario.getLegajo() != null, "El usuario no es personal"); return barrio.empleado(usuario.getLegajo());
    }
    public void consultarReclamo(Reclamo reclamo, LocalDate hoy) throws ReglaNegocioException {
        if (usuario.getRol() == RolUsuario.ADMINISTRADOR) return;
        if (usuario.getRol() == RolUsuario.VECINO) {
            Validacion.exigir(reclamo.getLote().puedePresentarReclamo(persona(), hoy), "Reclamo ajeno al vecino"); return;
        }
        Validacion.exigir(reclamo.getResponsable() == personal(), "Reclamo asignado a otro empleado");
    }
    public Usuario getUsuario() { return usuario; }
}
