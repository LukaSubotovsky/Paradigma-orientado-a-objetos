package Modelo;
import static org.junit.jupiter.api.Assertions.*;
import static Modelo.DatosPrueba.*;
import java.time.*;
import org.junit.jupiter.api.*;
class VisitasTest {
    Barrio b; GestionVisitas g; GuardiaDeSeguridad guardia;
    @BeforeEach void preparar() throws Exception { b=barrio(); g=new GestionVisitas(b); guardia=(GuardiaDeSeguridad)b.empleado(1); }
    Autorizacion autorizar(boolean recurrente,int dias) throws Exception {
        return g.registrarAutorizacion(b.lote(1),b.persona("100"),"Visita","500",AHORA.toLocalDate(),AHORA.toLocalDate().plusDays(dias-1),LocalTime.of(8,0),LocalTime.of(18,0),recurrente,AHORA.minusHours(1));
    }
    @Test void sinAutorizacionNoEntra() { assertThrows(ReglaNegocioException.class,()->g.registrarIngreso("500",guardia,AHORA)); }
    @Test void validaFranjaYAutorizacionPrevia() throws Exception {
        autorizar(false,1);
        assertThrows(ReglaNegocioException.class,()->g.registrarIngreso("500",guardia,AHORA.minusHours(2)));
        assertThrows(ReglaNegocioException.class,()->g.registrarIngreso("500",guardia,AHORA.withHour(19)));
        assertNotNull(g.registrarIngreso("500",guardia,AHORA));
    }
    @Test void bloqueaDobleIngresoInclusoConDosAutorizaciones() throws Exception {
        autorizar(false,1); autorizar(true,2); RegistroAcceso r=g.registrarIngreso("500",guardia,AHORA);
        assertThrows(ReglaNegocioException.class,()->g.registrarIngreso("500",guardia,AHORA.plusMinutes(1)));
        assertTrue(r.estaAdentro()); g.registrarEgreso("500",guardia,AHORA.plusHours(1),"Salida manual: olvidó marcar"); assertFalse(r.estaAdentro());
    }
    @Test void puntualSoloUnUsoYRecurrenteUnoPorDia() throws Exception {
        autorizar(false,1); g.registrarIngreso("500",guardia,AHORA); g.registrarEgreso("500",guardia,AHORA.plusMinutes(1),"Salida");
        assertThrows(ReglaNegocioException.class,()->g.registrarIngreso("500",guardia,AHORA.plusMinutes(2)));
        autorizar(true,30); g.registrarIngreso("500",guardia,AHORA.plusDays(1)); g.registrarEgreso("500",guardia,AHORA.plusDays(1).plusMinutes(1),"Salida");
        assertThrows(ReglaNegocioException.class,()->g.registrarIngreso("500",guardia,AHORA.plusDays(1).plusMinutes(2)));
        assertNotNull(g.registrarIngreso("500",guardia,AHORA.plusDays(2)));
    }
    @Test void treintaDiasInclusiveYTreintaYUnoRechazado() throws Exception {
        assertNotNull(autorizar(true,30)); assertThrows(ReglaNegocioException.class,()->autorizar(true,31));
        assertThrows(ReglaNegocioException.class,()->autorizar(false,2));
    }
    @Test void salidaAnteriorORepetidaNoSeAcepta() throws Exception {
        autorizar(false,1); RegistroAcceso r=g.registrarIngreso("500",guardia,AHORA);
        assertThrows(ReglaNegocioException.class,()->g.registrarEgreso("500",guardia,AHORA.minusSeconds(1),"Salida")); assertTrue(r.estaAdentro());
        g.registrarEgreso("500",guardia,AHORA,"Salida"); assertThrows(ReglaNegocioException.class,()->g.registrarEgreso("500",guardia,AHORA,"Otra"));
    }
}
