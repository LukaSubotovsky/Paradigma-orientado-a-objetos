package Modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
public class Pago implements Serializable {
    private static final long serialVersionUID = 1L;
    private final int numero;
    private final BigDecimal importe;
    private final LocalDate fecha;
    private final MedioDePago medioPago;
    private final List<AsientoAjuste> ajustes = new ArrayList<>();
    private List<AplicacionPago> aplicaciones = new ArrayList<>();
    private List<AplicacionPago> aplicacionesOriginales = new ArrayList<>();
    Pago(int numero, BigDecimal importe, MedioDePago medioPago, LocalDate fecha) throws ReglaNegocioException {
        this.importe = Validacion.dinero(importe);
        Validacion.exigir(medioPago != null && fecha != null, "Pago incompleto");
        this.numero = numero; this.medioPago = medioPago; this.fecha = fecha;
    }
    public BigDecimal importeCorregido() {
        BigDecimal total = importe;
        for (AsientoAjuste ajuste : ajustes) total = total.add(ajuste.getImporte());
        return total;
    }
    void agregarAjuste(BigDecimal importe, String motivo, LocalDate fecha) { ajustes.add(new AsientoAjuste(fecha, importe, motivo)); }
    void guardarAplicaciones(List<AplicacionPago> nuevas, boolean original) {
        aplicaciones = new ArrayList<>(nuevas);
        if (original) aplicacionesOriginales = new ArrayList<>(nuevas);
    }
    public int getNumero() { return numero; }
    public BigDecimal getImporte() { return importe; }
    public LocalDate getFecha() { return fecha; }
    public MedioDePago getMedioPago() { return medioPago; }
    public List<AsientoAjuste> getAjustes() { return List.copyOf(ajustes); }
    public List<AplicacionPago> getAplicaciones() { return List.copyOf(aplicaciones); }
    public List<AplicacionPago> getAplicacionesOriginales() { return List.copyOf(aplicacionesOriginales); }
    @Override public String toString() { return numero + " | " + fecha + " | original " + importe + " | corregido " + importeCorregido() + " | " + medioPago; }
}
