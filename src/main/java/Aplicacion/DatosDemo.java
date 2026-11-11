package Aplicacion;

import Modelo.*;
import java.math.BigDecimal;
import java.time.LocalDate;
public final class DatosDemo {
    private DatosDemo() {}
    public static Barrio crear() throws ReglaNegocioException {
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        Administrador administrador = new Administrador("Ana", "Demo", "900", "1100000000", "admin@example.test");
        Barrio barrio = new Barrio("Los Aromos", administrador);
        Persona titular = new Persona("Luis", "Demo", "100", "1100000001", "vecino@example.test");
        Persona residente = new Persona("Eva", "Demo", "200", "1100000002", "residente@example.test");
        Persona autorizado = new Persona("Leo", "Demo", "300", "1100000003", "autorizado@example.test");
        barrio.agregarPersona(titular); barrio.agregarPersona(residente); barrio.agregarPersona(autorizado);
        // Datos ficticios: un titular puede tener varios lotes. El menú permite cargar otros datos.
        for (int i = 1; i <= 150; i++) barrio.crearLote(i, new BigDecimal("100.00"), titular, inicio);
        barrio.lote(1).agregarVinculo(residente, RolLote.FAMILIAR, inicio, null);
        barrio.lote(1).agregarVinculo(autorizado, RolLote.AUTORIZADO, inicio, null);
        barrio.agregarPersonal(new GuardiaDeSeguridad(1, "Guardia Demo", inicio, "Diurno", PuestoGuardia.PORTERIA));
        barrio.agregarPersonal(new EmpleadoDeMantenimiento(2, "Técnico Demo", inicio, Especialidad.ELECTRICIDAD));
        barrio.agregarUsuario(new Usuario("admin", "admin123", RolUsuario.ADMINISTRADOR, "900", null));
        barrio.agregarUsuario(new Usuario("vecino", "vecino123", RolUsuario.VECINO, "100", null));
        barrio.agregarUsuario(new Usuario("residente", "residente123", RolUsuario.VECINO, "200", null));
        barrio.agregarUsuario(new Usuario("autorizado", "autorizado123", RolUsuario.VECINO, "300", null));
        barrio.agregarUsuario(new Usuario("guardia", "guardia123", RolUsuario.GUARDIA, null, 1));
        barrio.agregarUsuario(new Usuario("tecnico", "tecnico123", RolUsuario.MANTENIMIENTO, null, 2));
        GestionEquipos equipos = new GestionEquipos(barrio);
        equipos.registrarEquipo(new Camara("C1", Sector.ACCESOS, inicio, 1080, 30));
        for (int i = 1; i <= 4; i++) equipos.registrarEquipo(new Luminaria("L" + i, Sector.CALLES_INTERNAS, inicio, "LED", 50));
        return barrio;
    }
}
