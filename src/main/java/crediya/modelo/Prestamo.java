package crediya.modelo;

import crediya.util.Formato;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Objects;

public record Prestamo(
        int id,
        int clienteId,
        BigDecimal monto,
        BigDecimal interes,
        int cuotas,
        LocalDate fechaPrestamo,
        LocalDate fechaVencimiento,
        EstadoPrestamo estado,
        BigDecimal pagado) {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);
    private static final int DIAS_PARA_MORA = 30;

    public Prestamo {
        Objects.requireNonNull(monto, "monto");
        Objects.requireNonNull(interes, "interes");
        Objects.requireNonNull(fechaPrestamo, "fechaPrestamo");
        Objects.requireNonNull(estado, "estado");
        Objects.requireNonNull(pagado, "pagado");
    }

    public BigDecimal montoTotal() {
        BigDecimal intereses = monto.multiply(interes).divide(CIEN, 2, RoundingMode.HALF_UP);
        return monto.add(intereses);
    }

    public BigDecimal saldo() {
        return montoTotal().subtract(pagado).max(BigDecimal.ZERO);
    }

    public BigDecimal valorCuota() {
        return montoTotal().divide(BigDecimal.valueOf(cuotas), 2, RoundingMode.HALF_UP);
    }

    public boolean estaActivo() {
        return estado == EstadoPrestamo.ACTIVO;
    }

    public boolean estaVencido(LocalDate hoy) {
        return fechaVencimiento != null && fechaVencimiento.isBefore(hoy);
    }

    public boolean estaEnMora(LocalDate hoy) {
        return estaActivo() && fechaPrestamo.isBefore(hoy.minusDays(DIAS_PARA_MORA));
    }

    public Prestamo conId(int nuevoId) {
        return new Prestamo(nuevoId, clienteId, monto, interes, cuotas,
                fechaPrestamo, fechaVencimiento, estado, pagado);
    }

    @Override
    public String toString() {
        return "Prestamo #" + id + " | cliente " + clienteId
                + " | monto " + Formato.dinero(monto)
                + " | interes " + interes + "%"
                + " | prestado " + fechaPrestamo
                + " | vence " + fechaVencimiento
                + " | " + estado
                + " | saldo " + Formato.dinero(saldo());
    }
}
