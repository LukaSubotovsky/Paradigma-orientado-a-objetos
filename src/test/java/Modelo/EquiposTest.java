package Modelo;
import static org.junit.jupiter.api.Assertions.*;
import static Modelo.DatosPrueba.*;
import org.junit.jupiter.api.*;
class EquiposTest {
    Barrio b; GestionEquipos g;
    @BeforeEach void preparar() throws Exception { b=barrio(); g=new GestionEquipos(b); }
    Camara camara() throws Exception { Camara c=new Camara("C1",Sector.ACCESOS,AHORA.toLocalDate(),1080,30); g.registrarEquipo(c); return c; }
    @Test void prioridadPolimorficaDeCamaraYLuminaria() throws Exception {
        Equipo c=camara(), l=new Luminaria("L1",Sector.ACCESOS,AHORA.toLocalDate(),"LED",30);
        assertEquals(PrioridadOrden.ALTA,c.prioridadOrden(1)); assertEquals(PrioridadOrden.NORMAL,l.prioridadOrden(3)); assertEquals(PrioridadOrden.ALTA,l.prioridadOrden(4));
    }
    @Test void cuartaLuminariaElevaTodasLasOrdenesDelSector() throws Exception {
        for(int i=1;i<=4;i++) { Luminaria l=new Luminaria("L"+i,Sector.ACCESOS,AHORA.toLocalDate(),"LED",30); g.registrarEquipo(l); g.registrarFalla(l,"No enciende","Vecino",AHORA); }
        for(Equipo e:b.obtenerEquipos()) assertEquals(PrioridadOrden.ALTA,e.getOrdenes().get(0).getPrioridad());
    }
    @Test void fallaDuplicadaNoCreaOtraOrden() throws Exception {
        Camara c=camara(); g.registrarFalla(c,"Sin imagen","Guardia",AHORA);
        assertThrows(ReglaNegocioException.class,()->g.registrarFalla(c,"Otra","Guardia",AHORA)); assertEquals(1,c.getOrdenes().size());
    }
    @Test void avisoDespuesDe48HorasUnaVezYSeReiniciaTrasReparar() throws Exception {
        Camara c=camara(); OrdenMantenimiento o=g.registrarFalla(c,"Sin imagen","Guardia",AHORA);
        g.verificarCamaraSinFuncionar(AHORA.plusHours(48)); assertTrue(b.getAdministrador().getAvisos().isEmpty());
        g.verificarCamaraSinFuncionar(AHORA.plusHours(49)); g.verificarCamaraSinFuncionar(AHORA.plusHours(50)); assertEquals(1,b.getAdministrador().getAvisos().size());
        EmpleadoDeMantenimiento tecnico=(EmpleadoDeMantenimiento)b.empleado(2); g.iniciarReparacion(c,tecnico); g.cerrarOrden(o,AHORA.plusHours(51),tecnico,"Cable reemplazado");
        g.registrarFalla(c,"Nueva falla","Guardia",AHORA.plusHours(52)); g.verificarCamaraSinFuncionar(AHORA.plusHours(101)); assertEquals(2,b.getAdministrador().getAvisos().size());
    }
    @Test void cierreExigeTecnicoFechaYReparacion() throws Exception {
        Camara c=camara(); OrdenMantenimiento o=g.registrarFalla(c,"Sin imagen","Guardia",AHORA);
        EmpleadoDeMantenimiento t=(EmpleadoDeMantenimiento)b.empleado(2); g.iniciarReparacion(c,t);
        assertThrows(ReglaNegocioException.class,()->g.cerrarOrden(o,null,t,"Cable"));
        assertThrows(ReglaNegocioException.class,()->g.cerrarOrden(o,AHORA,t," "));
        assertTrue(o.estaAbierta()); g.cerrarOrden(o,AHORA,t,"Cambio de cable"); assertEquals(EstadoEquipo.OPERATIVO,c.getEstado());
    }
}
