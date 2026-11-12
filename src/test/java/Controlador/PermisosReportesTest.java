package Controlador;
import Modelo.*;
import static Modelo.DatosPrueba.*;
import static org.junit.jupiter.api.Assertions.*;
import java.time.*;
import org.junit.jupiter.api.*;
class PermisosReportesTest {
    Barrio b; Sesion admin, vecino, guardia;
    @BeforeEach void preparar() throws Exception {
        b=barrio(); admin=Sesion.ingresar(b,"admin","admin123"); vecino=Sesion.ingresar(b,"vecino","vecino123"); guardia=Sesion.ingresar(b,"guardia","guardia123");
    }
    @Test void loginIncorrectoNoCreaSesion() {
        assertThrows(ReglaNegocioException.class,()->Sesion.ingresar(b,"admin","otra"));
        assertThrows(ReglaNegocioException.class,()->Sesion.ingresar(b,"inexistente","admin123"));
    }
    @Test void vecinoNoPuedeEmitirAsignarNiVerReportesPrivados() throws Exception {
        assertThrows(ReglaNegocioException.class,()->new ControladorExpensas(b,vecino,reloj()).emitirExpensas(YearMonth.of(2026,11),monto("1000")));
        assertThrows(ReglaNegocioException.class,()->new ControladorReclamos(b,vecino,reloj()).asignarReclamo(1,1));
        assertThrows(ReglaNegocioException.class,()->new ControladorReportes(b,vecino,reloj()).morosidadPorLote(AHORA.toLocalDate()));
        assertTrue(b.lote(1).getExpensas().isEmpty());
    }
    @Test void responsableAjenoNoPuedeVerNiAvanzarReclamo() throws Exception {
        Reclamo r=new ControladorReclamos(b,vecino,reloj()).registrarReclamo(1,TipoReclamo.MANTENIMIENTO,"Vereda rota");
        new ControladorReclamos(b,admin,reloj()).asignarReclamo(r.getNumero(),2);
        assertThrows(ReglaNegocioException.class,()->new ControladorReclamos(b,guardia,reloj()).consultar(r.getNumero()));
        assertThrows(ReglaNegocioException.class,()->new ControladorReclamos(b,guardia,reloj()).avanzarEstado(r.getNumero(),"Inicio"));
    }
    @Test void reporteFiltraTipoFechaYOrdenaPorPrioridad() throws Exception {
        ControladorReclamos c=new ControladorReclamos(b,vecino,reloj());
        c.registrarReclamo(1,TipoReclamo.CONVIVENCIA,"Ruidos"); c.registrarReclamo(1,TipoReclamo.SEGURIDAD,"Portón");
        ControladorReportes r=new ControladorReportes(b,admin,reloj());
        assertEquals(TipoReclamo.SEGURIDAD,r.reclamosPendientes(AHORA.toLocalDate(),AHORA.toLocalDate(),null).get(0).getTipo());
        assertEquals(1,r.reclamosPendientes(AHORA.toLocalDate(),AHORA.toLocalDate(),TipoReclamo.CONVIVENCIA).size());
        assertTrue(r.reclamosPendientes(AHORA.toLocalDate().minusDays(1),AHORA.toLocalDate().minusDays(1),null).isEmpty());
    }
    @Test void reporteEquipoFueraDeServicioRespetaEstadoYSector() throws Exception {
        ControladorEquipos e=new ControladorEquipos(b,admin,reloj());
        e.registrarEquipo(new Camara("C",Sector.ACCESOS,AHORA.toLocalDate(),1080,30)); e.registrarFalla("C","Sin señal");
        ControladorReportes r=new ControladorReportes(b,admin,reloj()); assertTrue(r.equiposFueraDeServicio(null).isEmpty());
        e.retirarEquipo("C"); assertEquals(1,r.equiposFueraDeServicio(Sector.ACCESOS).size()); assertTrue(r.equiposFueraDeServicio(Sector.CALLES_INTERNAS).isEmpty());
    }
    @Test void rolesDeResidenteYAutorizadoNoSonIntercambiables() throws Exception {
        Persona p=new Persona("Eva","Prueba","200","111","e@example.test"); b.agregarPersona(p);
        b.lote(1).agregarVinculo(p,RolLote.FAMILIAR,AHORA.toLocalDate(),null);
        assertTrue(b.lote(1).puedeAutorizarVisita(p,AHORA.toLocalDate())); assertFalse(b.lote(1).puedePresentarReclamo(p,AHORA.toLocalDate()));
        b.lote(1).agregarVinculo(p,RolLote.AUTORIZADO,AHORA.toLocalDate(),null); assertTrue(b.lote(1).puedePresentarReclamo(p,AHORA.toLocalDate()));
    }
    @Test void notificacionesNoCambianAlConsultarReporte() throws Exception {
        GestionExpensas g=new GestionExpensas(b);
        for(int mes=7;mes<=10;mes++)g.emitirExpensas(YearMonth.of(2026,mes),monto("150000"),AHORA.toLocalDate());
        int antes=b.lote(1).getNotificaciones().size();
        assertEquals(150,new ControladorReportes(b,admin,reloj()).morosidadPorLote(AHORA.toLocalDate()).size());
        assertEquals(antes,b.lote(1).getNotificaciones().size());
    }
}
