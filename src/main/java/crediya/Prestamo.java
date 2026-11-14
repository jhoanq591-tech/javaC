package crediya;

import java.time.LocalDate;

/**
 * Representa una fila de la tabla prestamos.
 */
public class Prestamo {

    private int id;
    private int clienteId;
    private double monto;
    private double interes;
    private LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private String estado;
    private double pagado;

    public Prestamo(int id, int clienteId, double monto, double interes,
                    LocalDate fechaPrestamo, LocalDate fechaVencimiento, String estado, double pagado) {
        this.id = id;
        this.clienteId = clienteId;
        this.monto = monto;
        this.interes = interes;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
        this.pagado = pagado;
    }

    public int getId() {
        return id;
    }

    public int getClienteId() {
        return clienteId;
    }

    public double getMonto() {
        return monto;
    }

    public double getInteres() {
        return interes;
    }

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public double getPagado() {
        return pagado;
    }

    /** Lo que falta por pagar: total con interes menos los abonos (nunca negativo). */
    public double getSaldo() {
        return Math.max(getMontoTotal() - pagado, 0);
    }

    /** Monto mas el interes (%) = lo que el cliente debe pagar en total. */
    public double getMontoTotal() {
        return monto + (monto * interes / 100);
    }

    @Override
    public String toString() {
        return "Prestamo #" + id + " | cliente " + clienteId + " | monto $" + monto
                + " | interes " + interes + "% | prestado " + fechaPrestamo
                + " | vence " + fechaVencimiento + " | " + estado + " | saldo $" + getSaldo();
    }
}
