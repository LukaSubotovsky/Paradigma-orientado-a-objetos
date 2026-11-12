package Vista;
import Modelo.*;
import Persistencia.*;
import Aplicacion.DatosDemo;
import static Modelo.DatosPrueba.*;
import static org.junit.jupiter.api.Assertions.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;
class MenuConsolaTest {
    @TempDir Path carpeta;
    String ejecutar(Barrio barrio,String comandos) {
        ByteArrayOutputStream texto=new ByteArrayOutputStream();
        new MenuConsola(barrio,new ArchivoBarrio(carpeta.resolve("datos.dat")),reloj(),new ByteArrayInputStream(comandos.getBytes(StandardCharsets.UTF_8)),new PrintStream(texto,true,StandardCharsets.UTF_8)).ejecutar();
        return texto.toString(StandardCharsets.UTF_8);
    }
    @Test void flujoReclamoConCambiosDeUsuarioYReinicio() throws Exception {
        Barrio b=DatosDemo.crear();
        String salida=ejecutar(b,"vecino\nvecino123\n10\n1\n1\nPortón roto\n90\nadmin\nadmin123\n11\n1\n1\n90\nguardia\nguardia123\n12\n1\nInicio de revisión\n14\n1\nSe revisa el motor\n15\n1\n0\n");
        assertFalse(salida.contains("No se pudo"),salida); assertEquals(EstadoReclamo.EN_CURSO,b.reclamo(1).getEstado());
        Barrio leido=new ArchivoBarrio(carpeta.resolve("datos.dat")).cargar(); assertEquals(1,leido.reclamo(1).getComentarios().size());
    }
    @Test void entradasInvalidasNoCierranMenuNiCambianReglas() throws Exception {
        Barrio b=DatosDemo.crear();
        String salida=ejecutar(b,"admin\nmal\nadmin\nadmin123\ntexto\n20\nmes-invalido\n0\n");
        assertTrue(salida.contains("Usuario o clave incorrectos")); assertTrue(salida.contains("Formato inválido")); assertTrue(salida.contains("Fin de la sesión"));
        assertTrue(b.lote(1).getExpensas().isEmpty());
    }
    @Test void flujoExpensaPagoAjusteYReportePorConsola() throws Exception {
        Barrio b=DatosDemo.crear();
        String salida=ejecutar(b,"admin\nadmin123\n20\n2026-11\n150000\n22\n1\n500\n1\n2026-11-11\n23\n1\n1\n-10\nError de carga\n21\n1\n2026-11-11\n51\n2026-11-11\n0\n");
        assertFalse(salida.contains("No se pudo"),salida); assertEquals(1,b.lote(1).getPagos().get(0).getAjustes().size()); assertTrue(salida.contains("Capital: 511.00"),salida);
    }
    @Test void flujoFallaOrdenReparacionConDosRoles() throws Exception {
        Barrio b=DatosDemo.crear();
        String salida=ejecutar(b,"guardia\nguardia123\n31\nC1\nSin imagen\n90\nadmin\nadmin123\n32\nC1\n2\n90\ntecnico\ntecnico123\n33\nC1\nCambio de cable\n35\nC1\n0\n");
        assertFalse(salida.contains("No se pudo"),salida); assertEquals(EstadoEquipo.OPERATIVO,b.equipo("C1").getEstado());
    }
    @Test void flujoVisitaRechazaDobleIngresoYGuardaEgreso() throws Exception {
        Barrio b=DatosDemo.crear();
        String salida=ejecutar(b,"vecino\nvecino123\n40\n1\nVisita Demo\n500\n2026-11-11\n2026-11-11\n08:00\n18:00\nn\n90\nguardia\nguardia123\n41\n500\n42\n500\n42\n500\n43\n500\n\n44\n0\n");
        assertTrue(salida.contains("todavía está dentro"),salida); assertEquals(1,b.getAutorizaciones().get(0).getAccesos().size()); assertFalse(b.getAutorizaciones().get(0).getAccesos().get(0).estaAdentro());
    }
}
