package Modelo;
import java.math.BigDecimal;
import java.time.*;
public final class DatosPrueba {
    public static final LocalDateTime AHORA = LocalDateTime.of(2026, 11, 11, 12, 0);
    public static Barrio barrio() throws ReglaNegocioException {
        Barrio b = new Barrio("Los Aromos", new Administrador("Ana", "Prueba", "900", "111", "a@example.test"));
        Persona titular = new Persona("Luis", "Prueba", "100", "111", "l@example.test"); b.agregarPersona(titular);
        for (int i = 1; i <= 150; i++) b.crearLote(i, new BigDecimal("100.00"), titular, LocalDate.of(2026,1,1));
        b.agregarPersonal(new GuardiaDeSeguridad(1, "Guardia", LocalDate.of(2026,1,1), "Día", PuestoGuardia.PORTERIA));
        b.agregarPersonal(new EmpleadoDeMantenimiento(2, "Técnico", LocalDate.of(2026,1,1), Especialidad.ELECTRICIDAD));
        b.agregarUsuario(new Usuario("admin", "admin123", RolUsuario.ADMINISTRADOR, "900", null));
        b.agregarUsuario(new Usuario("vecino", "vecino123", RolUsuario.VECINO, "100", null));
        b.agregarUsuario(new Usuario("guardia", "guardia123", RolUsuario.GUARDIA, null, 1));
        b.agregarUsuario(new Usuario("tecnico", "tecnico123", RolUsuario.MANTENIMIENTO, null, 2));
        return b;
    }
    public static BigDecimal monto(String s) { return new BigDecimal(s); }
    public static Clock reloj() { return Clock.fixed(AHORA.toInstant(ZoneOffset.UTC), ZoneOffset.UTC); }
}
