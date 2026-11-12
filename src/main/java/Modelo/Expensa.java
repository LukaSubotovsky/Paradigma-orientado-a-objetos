package Modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
public class Expensa implements Serializable, Comparable<Expensa> {
    private static final long serialVersionUID = 1L;
    private final YearMonth periodo;
    private final BigDecimal importe;
    private final LocalDate fechaDeVencimiento;
    private BigDecimal saldoCapital;
    private BigDecimal interesPendiente = BigDecimal.ZERO;
    private LocalDate fechaCalculo;
    public Expensa(YearMonth periodo, BigDecimal importe) throws ReglaNegocioException {
        Validacion.exigir(periodo != null && importe != null && importe.signum() >= 0, "Expensa inválida");
        this.periodo = periodo; this.importe = importe;
        fechaDeVencimiento = periodo.atDay(new Configuracion().getDiaVencimiento());
        reiniciar();
    }
    void reiniciar() { saldoCapital = importe; interesPendiente = BigDecimal.ZERO; fechaCalculo = fechaDeVencimiento; }
    void aplicar(BigDecimal interes, BigDecimal capital, LocalDate fecha) {
        interesPendiente = new CalculadorIntereses().calcular(this, fecha).subtract(interes);
        saldoCapital = saldoCapital.subtract(capital);
        if (fecha.isAfter(fechaCalculo)) fechaCalculo = fecha;
    }
    public BigDecimal calcularSaldo() { return saldoCapital; }
    public boolean estaVencida(LocalDate fecha) {
        return fecha.isAfter(fechaDeVencimiento) && deuda(fecha).signum() > 0;
    }
    public BigDecimal deuda(LocalDate fecha) { return saldoCapital.add(new CalculadorIntereses().calcular(this, fecha)); }
    public EstadoExpensa getEstado(LocalDate fecha) {
        if (deuda(fecha).signum() == 0) return EstadoExpensa.PAGADA;
        if (estaVencida(fecha)) return EstadoExpensa.VENCIDA;
        if (saldoCapital.compareTo(importe) < 0) return EstadoExpensa.PARCIALMENTE_PAGADA;
        return EstadoExpensa.PENDIENTE;
    }
    public YearMonth getPeriodo() { return periodo; }
    public BigDecimal getImporte() { return importe; }
    public LocalDate getFechaDeVencimiento() { return fechaDeVencimiento; }
    public BigDecimal getInteresPendiente() { return interesPendiente; }
    public LocalDate getFechaCalculo() { return fechaCalculo; }
    @Override public int compareTo(Expensa otra) { return periodo.compareTo(otra.periodo); }
    @Override public String toString() { return periodo + " | importe " + importe + " | capital pendiente " + saldoCapital; }
}
