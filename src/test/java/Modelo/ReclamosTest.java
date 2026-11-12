package Modelo;
import static org.junit.jupiter.api.Assertions.*;
import static Modelo.DatosPrueba.*;
import java.time.*;
import org.junit.jupiter.api.*;
class ReclamosTest {
    Barrio b; GestionReclamos g;
    @BeforeEach void preparar() throws Exception { b = barrio(); g = new GestionReclamos(b); }
    Reclamo nuevo(TipoReclamo tipo) throws Exception { return g.registrar(b.lote(1), b.persona("100"), tipo, "Prueba", AHORA); }
    Reclamo resuelto() throws Exception {
        Reclamo r = nuevo(TipoReclamo.MANTENIMIENTO); g.asignar(r, b.empleado(2), AHORA);
        g.avanzarEstado(r, "Inicio", AHORA); g.avanzarEstado(r, "Reparado", AHORA); return r;
    }
    @Test void prioridadesSegunTipo() throws Exception {
        assertEquals(PrioridadReclamo.ALTA, nuevo(TipoReclamo.SEGURIDAD).getPrioridad());
        assertEquals(PrioridadReclamo.MEDIA, nuevo(TipoReclamo.MANTENIMIENTO).getPrioridad());
        assertEquals(PrioridadReclamo.BAJA, nuevo(TipoReclamo.CONVIVENCIA).getPrioridad());
        assertEquals(PrioridadReclamo.BAJA, nuevo(TipoReclamo.ADMINISTRATIVO).getPrioridad());
    }
    @Test void rechazaVecinoAjenoSinCrearReclamo() throws Exception {
        assertThrows(ReglaNegocioException.class, () -> g.registrar(b.lote(1), b.getAdministrador(), TipoReclamo.SEGURIDAD, "Falla", AHORA));
        assertTrue(b.obtenerReclamos().isEmpty());
    }
    @Test void noSaltaAsignacion() throws Exception {
        Reclamo r = nuevo(TipoReclamo.SEGURIDAD);
        assertThrows(ReglaNegocioException.class, () -> g.avanzarEstado(r, "Salto", AHORA));
        assertEquals(EstadoReclamo.ABIERTO, r.getEstado());
    }
    @Test void limiteDeCincoEnCursoYLiberacionAlResolver() throws Exception {
        for(int i=0;i<5;i++) { Reclamo r=nuevo(TipoReclamo.MANTENIMIENTO); g.asignar(r,b.empleado(2),AHORA); g.avanzarEstado(r,"Inicio",AHORA); }
        Reclamo sexto=nuevo(TipoReclamo.MANTENIMIENTO); g.asignar(sexto,b.empleado(2),AHORA);
        assertThrows(ReglaNegocioException.class, () -> g.avanzarEstado(sexto,"Inicio",AHORA));
        g.avanzarEstado(b.reclamo(1),"Resuelto",AHORA); g.avanzarEstado(sexto,"Inicio",AHORA);
        assertEquals(EstadoReclamo.EN_CURSO,sexto.getEstado());
    }
    @Test void reaperturaExactamenteCincoDiasConservaResponsable() throws Exception {
        Reclamo r=resuelto(); g.reabrir(r,b.persona("100"),"Sigue fallando",AHORA.plusDays(5));
        assertEquals(EstadoReclamo.ASIGNADO,r.getEstado()); assertSame(b.empleado(2),r.getResponsable()); assertEquals(5,r.getHistorial().size());
    }
    @Test void cierraSoloDespuesDelPlazoYNoPermiteReabrir() throws Exception {
        Reclamo r=resuelto(); g.revisarPlazos(AHORA.plusDays(5)); assertEquals(EstadoReclamo.RESUELTO,r.getEstado());
        assertThrows(ReglaNegocioException.class, () -> g.reabrir(r,b.persona("100"),"Tarde",AHORA.plusDays(5).plusSeconds(1)));
        g.revisarPlazos(AHORA.plusDays(5).plusSeconds(1)); assertEquals(EstadoReclamo.CERRADO,r.getEstado());
    }
    @Test void escalaEn24HorasUnaSolaVez() throws Exception {
        Reclamo r=nuevo(TipoReclamo.SEGURIDAD); g.asignar(r,b.empleado(1),AHORA);
        g.revisarPlazos(AHORA.plusHours(24).minusSeconds(1)); assertFalse(r.isEscalado());
        g.revisarPlazos(AHORA.plusHours(24)); g.revisarPlazos(AHORA.plusHours(25)); assertEquals(1,b.getAdministrador().getAvisos().size());
    }
    @Test void atendidoNoSeEscala() throws Exception {
        Reclamo r=nuevo(TipoReclamo.SEGURIDAD); g.asignar(r,b.empleado(1),AHORA); g.avanzarEstado(r,"Inicio",AHORA);
        g.revisarPlazos(AHORA.plusHours(25)); assertFalse(r.isEscalado());
    }
    @Test void comentarioVacioYFechaRetroactivaNoAlteranHistoria() throws Exception {
        Reclamo r=nuevo(TipoReclamo.SEGURIDAD);
        assertThrows(ReglaNegocioException.class, () -> r.agregarComentario(" ","Luis",AHORA));
        assertThrows(ReglaNegocioException.class, () -> g.asignar(r,b.empleado(1),AHORA.minusSeconds(1)));
        assertNull(r.getResponsable()); assertEquals(1,r.getHistorial().size()); assertTrue(r.getComentarios().isEmpty());
    }
}
