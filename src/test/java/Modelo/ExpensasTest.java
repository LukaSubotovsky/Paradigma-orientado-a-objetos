package Modelo;
import static org.junit.jupiter.api.Assertions.*;
import static Modelo.DatosPrueba.*;
import java.math.BigDecimal;
import java.time.*;
import org.junit.jupiter.api.*;
class ExpensasTest {
    Barrio b; GestionExpensas g; Lote l;
    LocalDate hoy=LocalDate.of(2026,11,11);
    @BeforeEach void preparar() throws Exception { b=barrio(); g=new GestionExpensas(b); l=b.lote(1); }
    void emitir(int mes) throws Exception { g.emitirExpensas(YearMonth.of(2026,mes),monto("150000.00"),hoy); }
    void igual(String esperado, BigDecimal real) { assertEquals(0,monto(esperado).compareTo(real),real.toPlainString()); }
    @Test void emisionSumaPresupuestoSinPerderCentavos() throws Exception {
        g.emitirExpensas(YearMonth.of(2026,11),monto("100.00"),hoy);
        BigDecimal total=b.obtenerLotes().stream().map(x->x.getExpensas().get(0).getImporte()).reduce(BigDecimal.ZERO,BigDecimal::add);
        igual("100",total); igual("0.67",l.getExpensas().get(0).getImporte()); igual("0.66",b.lote(150).getExpensas().get(0).getImporte());
    }
    @Test void noDuplicaPeriodoNiAceptaPresupuestoInvalido() throws Exception {
        emitir(10); assertThrows(ReglaNegocioException.class,()->emitir(10));
        assertThrows(ReglaNegocioException.class,()->g.emitirExpensas(YearMonth.of(2026,11),monto("-1"),hoy));
        assertEquals(1,l.getExpensas().size());
    }
    @Test void diezSinInteresOnceConUnDia() throws Exception {
        emitir(10); Expensa e=l.getExpensas().get(0); CalculadorIntereses c=new CalculadorIntereses();
        igual("0",c.calcular(e,LocalDate.of(2026,10,10))); igual("1",c.calcular(e,LocalDate.of(2026,10,11)));
    }
    @Test void pagoParcialReduceBaseDeInteresDesdeSuFecha() throws Exception {
        emitir(10); Pago p=g.registrarPago(l,monto("510"),MedioDePago.EFECTIVO,LocalDate.of(2026,10,20),hoy);
        igual("10",p.getAplicaciones().get(0).getImporteInteres()); igual("500",p.getAplicaciones().get(0).getImporteCapital());
        igual("500",l.calcularDeudaCapital()); igual("5",l.calcularDeudaIntereses(LocalDate.of(2026,10,30)));
    }
    @Test void pagoQueSoloCubreParteDelInteresNoCapitaliza() throws Exception {
        emitir(10); g.registrarPago(l,monto("5"),MedioDePago.EFECTIVO,LocalDate.of(2026,10,20),hoy);
        igual("1000",l.calcularDeudaCapital()); igual("15",l.calcularDeudaIntereses(LocalDate.of(2026,10,30)));
    }
    @Test void imputaPrimeroPeriodoMasAntiguo() throws Exception {
        emitir(9); emitir(10); Pago p=g.registrarPago(l,monto("1041"),MedioDePago.TRANSFERENCIA,LocalDate.of(2026,10,20),hoy);
        assertEquals(YearMonth.of(2026,9),p.getAplicaciones().get(0).getPeriodo());
        igual("0",l.getExpensas().get(0).calcularSaldo()); igual("1000",l.getExpensas().get(1).calcularSaldo());
        igual("9",new CalculadorIntereses().calcular(l.getExpensas().get(1),LocalDate.of(2026,10,20)));
    }
    @Test void sobrepagoYRetroactividadNoModificanSaldos() throws Exception {
        emitir(10); assertThrows(ReglaNegocioException.class,()->g.registrarPago(l,monto("9999"),MedioDePago.EFECTIVO,hoy,hoy));
        assertTrue(l.getPagos().isEmpty()); igual("1000",l.calcularDeudaCapital());
        g.registrarPago(l,monto("100"),MedioDePago.EFECTIVO,hoy,hoy);
        assertThrows(ReglaNegocioException.class,()->g.registrarPago(l,monto("10"),MedioDePago.EFECTIVO,hoy.minusDays(1),hoy));
    }
    @Test void cuartoPeriodoNotificaUnaVezYRegularizarQuitaMarca() throws Exception {
        emitir(7); emitir(8); emitir(9); assertFalse(l.isMoroso()); emitir(10); assertTrue(l.isMoroso());
        g.verificarMorosidad(hoy); assertEquals(1,l.getNotificaciones().size());
        BigDecimal deuda=l.calcularDeudaCapital().add(l.calcularDeudaIntereses(hoy));
        g.registrarPago(l,deuda,MedioDePago.EFECTIVO,hoy,hoy); assertFalse(l.isMoroso()); igual("0",l.calcularDeudaCapital());
    }
    @Test void ajusteRecalculaSinBorrarPagoNiAplicacionOriginal() throws Exception {
        emitir(10); Pago p=g.registrarPago(l,monto("510"),MedioDePago.EFECTIVO,LocalDate.of(2026,10,20),hoy);
        g.registrarAjuste(l,p,monto("-100"),"Se cargó cien de más",hoy,hoy);
        igual("510",p.getImporte()); igual("410",p.importeCorregido()); igual("600",l.calcularDeudaCapital());
        igual("500",p.getAplicacionesOriginales().get(0).getImporteCapital()); igual("400",p.getAplicaciones().get(0).getImporteCapital());
        igual("6",l.calcularDeudaIntereses(LocalDate.of(2026,10,30))); assertEquals(1,p.getAjustes().size());
    }
    @Test void ajusteIncompatibleConPagoPosteriorEsAtomico() throws Exception {
        emitir(10); Pago p=g.registrarPago(l,monto("500"),MedioDePago.EFECTIVO,LocalDate.of(2026,10,10),hoy);
        g.registrarPago(l,monto("500"),MedioDePago.EFECTIVO,LocalDate.of(2026,10,10),hoy);
        assertThrows(ReglaNegocioException.class,()->g.registrarAjuste(l,p,monto("100"),"Error",hoy,hoy));
        assertTrue(p.getAjustes().isEmpty()); igual("0",l.calcularDeudaCapital());
    }
    @Test void noEmiteConMenosDe150Lotes() throws Exception {
        Barrio vacio=new Barrio("Otro",b.getAdministrador());
        assertThrows(ReglaNegocioException.class,()->new GestionExpensas(vacio).emitirExpensas(YearMonth.of(2026,11),monto("10"),hoy));
    }
}
