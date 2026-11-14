package crediya.consola;

import crediya.modelo.Pago;
import crediya.servicio.ServicioPagos;
import crediya.util.Formato;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;

public class AccionesPagos {

    private final EntradaConsola entrada;
    private final PrintStream salida;
    private final ServicioPagos pagos;

    public AccionesPagos(EntradaConsola entrada, PrintStream salida, ServicioPagos pagos) {
        this.entrada = entrada;
        this.salida = salida;
        this.pagos = pagos;
    }

    public void registrarAbono() {
        int prestamoId = entrada.leerEntero("ID del prestamo: ");
        BigDecimal abono = entrada.leerDecimal("Monto del abono: ");
        BigDecimal saldo = pagos.registrarAbono(prestamoId, abono);
        salida.println("Abono registrado. Saldo pendiente: " + Formato.dinero(saldo));
        if (saldo.signum() == 0) {
            salida.println("El prestamo quedo PAGADO.");
        }
    }

    public void listar() {
        int prestamoId = entrada.leerEntero("ID del prestamo (0 para ver todos): ");
        List<Pago> lista = pagos.listar(prestamoId);
        salida.println("--- HISTORICO DE PAGOS ---");
        if (lista.isEmpty()) {
            salida.println("(sin pagos)");
        }
        lista.forEach(p -> salida.println(p.id() + " | prestamo " + p.prestamoId()
                + " | " + p.fecha() + " | " + Formato.dinero(p.monto())));
    }
}
