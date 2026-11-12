package Persistencia;
import Modelo.*;
import static Modelo.DatosPrueba.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.*;
import java.io.IOException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
class ArchivoBarrioTest {
    @TempDir Path carpeta;
    @Test void conservaRelacionesPagosAjustesYAccesosEntreLecturas() throws Exception {
        Barrio b=barrio(); new GestionReclamos(b).registrar(b.lote(1),b.persona("100"),TipoReclamo.SEGURIDAD,"Portón",AHORA);
        GestionExpensas exp=new GestionExpensas(b); exp.emitirExpensas(java.time.YearMonth.of(2026,11),monto("150000"),AHORA.toLocalDate());
        Pago p=exp.registrarPago(b.lote(1),monto("500"),MedioDePago.EFECTIVO,AHORA.toLocalDate(),AHORA.toLocalDate());
        exp.registrarAjuste(b.lote(1),p,monto("-10"),"Error",AHORA.toLocalDate(),AHORA.toLocalDate());
        GestionVisitas visitas=new GestionVisitas(b); visitas.registrarAutorizacion(b.lote(1),b.persona("100"),"Visita","500",AHORA.toLocalDate(),AHORA.toLocalDate(),java.time.LocalTime.MIN,java.time.LocalTime.MAX,false,AHORA);
        visitas.registrarIngreso("500",(GuardiaDeSeguridad)b.empleado(1),AHORA);
        ArchivoBarrio archivo=new ArchivoBarrio(carpeta.resolve("barrio.dat")); archivo.guardar(b); Barrio cargado=archivo.cargar();
        assertEquals(150,cargado.obtenerLotes().size()); assertSame(cargado.lote(1),cargado.reclamo(1).getLote());
        assertEquals(1,cargado.lote(1).getPagos().get(0).getAjustes().size()); assertTrue(cargado.getAutorizaciones().get(0).getAccesos().get(0).estaAdentro());
        assertNotNull(Sesion.ingresar(cargado,"admin","admin123"));
        assertThrows(ReglaNegocioException.class,()->new GestionVisitas(cargado).registrarIngreso("500",(GuardiaDeSeguridad)cargado.empleado(1),AHORA));
        archivo.guardar(cargado); assertTrue(Files.exists(carpeta.resolve("barrio.dat.bak")));
    }
    @Test void archivoDanadoSeInformaYNoSeReemplaza() throws Exception {
        Path ruta=carpeta.resolve("mal.dat"); Files.writeString(ruta,"datos rotos");
        assertThrows(IOException.class,()->new ArchivoBarrio(ruta).cargar()); assertEquals("datos rotos",Files.readString(ruta));
    }
    @Test void falloDeEscrituraNoDestruyeArchivoAnterior() throws Exception {
        Path ruta=carpeta.resolve("barrio.dat"); ArchivoBarrio archivo=new ArchivoBarrio(ruta); archivo.guardar(barrio());
        byte[] previo=Files.readAllBytes(ruta); Files.createDirectory(carpeta.resolve("barrio.dat.bak")); Files.writeString(carpeta.resolve("barrio.dat.bak/bloqueo"),"x");
        assertThrows(IOException.class,()->archivo.guardar(barrio())); assertArrayEquals(previo,Files.readAllBytes(ruta));
        try(var archivos=Files.list(carpeta)) { assertTrue(archivos.noneMatch(p->p.toString().endsWith(".tmp"))); }
    }
}
