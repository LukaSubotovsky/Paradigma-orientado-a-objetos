package Modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class Barrio implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String nombreBarrio;
    private final Administrador administrador;
    private final List<Lote> lotes = new ArrayList<>();
    private final List<Persona> personas = new ArrayList<>();
    private final List<Personal> personal = new ArrayList<>();
    private final List<Reclamo> reclamos = new ArrayList<>();
    private final List<Equipo> equipos = new ArrayList<>();
    private final List<Autorizacion> autorizaciones = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    public Barrio(String nombre, Administrador administrador) throws ReglaNegocioException {
        nombreBarrio = Validacion.texto(nombre, "Barrio");
        Validacion.exigir(administrador != null, "Falta administrador");
        this.administrador = administrador; personas.add(administrador);
    }
    public void agregarPersona(Persona persona) throws ReglaNegocioException {
        Validacion.exigir(personas.stream().noneMatch(p -> p.getDocumento().equals(persona.getDocumento())), "DNI duplicado");
        personas.add(persona);
    }
    public Lote crearLote(int numero, BigDecimal superficie, Persona titular, LocalDate desde) throws ReglaNegocioException {
        Validacion.exigir(lotes.size() < 150, "El barrio tiene como máximo 150 lotes");
        Validacion.exigir(lotes.stream().noneMatch(l -> l.getNumero() == numero), "Lote duplicado");
        Validacion.exigir(personas.contains(titular), "Titular no registrado");
        Lote lote = new Lote(numero, superficie, titular, desde); lotes.add(lote); return lote;
    }
    public void agregarPersonal(Personal empleado) throws ReglaNegocioException {
        Validacion.exigir(personal.stream().noneMatch(p -> p.getLegajo() == empleado.getLegajo()), "Legajo duplicado");
        personal.add(empleado);
    }
    public void calcularCoeficientes() throws ReglaNegocioException {
        Validacion.exigir(!lotes.isEmpty(), "No hay lotes");
        BigDecimal total = lotes.stream().map(Lote::getSuperficie).reduce(BigDecimal.ZERO, BigDecimal::add);
        for (Lote lote : lotes) lote.setCoeficiente(lote.getSuperficie().divide(total, 12, RoundingMode.HALF_UP));
    }
    public Lote lote(int numero) throws ReglaNegocioException {
        return lotes.stream().filter(l -> l.getNumero() == numero).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("Lote inexistente"));
    }
    public Persona persona(String documento) throws ReglaNegocioException {
        return personas.stream().filter(p -> p.getDocumento().equals(documento)).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("Persona inexistente"));
    }
    public Personal empleado(int legajo) throws ReglaNegocioException {
        return personal.stream().filter(p -> p.getLegajo() == legajo).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("Legajo inexistente"));
    }
    public Reclamo reclamo(int numero) throws ReglaNegocioException {
        return reclamos.stream().filter(r -> r.getNumero() == numero).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("Reclamo inexistente"));
    }
    public Equipo equipo(String codigo) throws ReglaNegocioException {
        return equipos.stream().filter(e -> e.getCodigo().equals(codigo)).findFirst()
                .orElseThrow(() -> new ReglaNegocioException("Equipo inexistente"));
    }
    public void agregarUsuario(Usuario usuario) throws ReglaNegocioException {
        Validacion.exigir(usuarios.stream().noneMatch(u -> u.getNombre().equals(usuario.getNombre())), "Usuario duplicado");
        usuarios.add(usuario);
    }
    void agregarReclamo(Reclamo reclamo) { reclamos.add(reclamo); }
    void agregarEquipo(Equipo equipo) { equipos.add(equipo); }
    void agregarAutorizacion(Autorizacion autorizacion) { autorizaciones.add(autorizacion); }
    public String getNombreBarrio() { return nombreBarrio; }
    public Administrador getAdministrador() { return administrador; }
    public List<Lote> obtenerLotes() { return List.copyOf(lotes); }
    public List<Persona> getPersonas() { return List.copyOf(personas); }
    public List<Personal> getPersonal() { return List.copyOf(personal); }
    public List<Reclamo> obtenerReclamos() { return List.copyOf(reclamos); }
    public List<Equipo> obtenerEquipos() { return List.copyOf(equipos); }
    public List<Autorizacion> getAutorizaciones() { return List.copyOf(autorizaciones); }
    public List<Usuario> getUsuarios() { return List.copyOf(usuarios); }
}
