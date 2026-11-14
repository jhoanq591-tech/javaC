package crediya.consola;

import crediya.excepcion.ErrorDeNegocio;
import crediya.modelo.Cliente;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import crediya.servicio.ServicioClientes;
import crediya.servicio.ServicioPrestamos;
import crediya.util.Formato;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class AccionesPrestamos {

    private final EntradaConsola entrada;
    private final PrintStream salida;
    private final ServicioPrestamos prestamos;
    private final ServicioClientes clientes;

    public AccionesPrestamos(EntradaConsola entrada, PrintStream salida,
                             ServicioPrestamos prestamos, ServicioClientes clientes) {
        this.entrada = entrada;
        this.salida = salida;
        this.prestamos = prestamos;
        this.clientes = clientes;
    }

    public void crearParaCliente(int empleadoId) {
        int clienteId = entrada.leerEntero("ID del cliente: ");
        BigDecimal monto = entrada.leerDecimal("Monto del prestamo: ");
        int cuotas = entrada.leerEntero("Numero de cuotas: ");
        mostrarResultado(prestamos.crearParaCliente(clienteId, empleadoId, monto, cuotas));
    }

    public void solicitar(int clienteId) {
        BigDecimal monto = entrada.leerDecimal("Monto del prestamo: ");
        int cuotas = entrada.leerEntero("Numero de cuotas: ");
        mostrarResultado(prestamos.solicitar(clienteId, monto, cuotas));
    }

    private void mostrarResultado(Prestamo prestamo) {
        salida.println("Prestamo registrado. Queda PENDIENTE hasta que lo revise el administrador.");
        salida.println("Total a pagar: " + Formato.dinero(prestamo.montoTotal()));
        salida.println("Valor de cada cuota: " + Formato.dinero(prestamo.valorCuota()));
    }

    public void decidir(int empleadoId) {
        int id = entrada.leerEntero("ID del prestamo: ");
        Prestamo prestamo = prestamos.buscar(id);
        salida.println("Estado actual: " + prestamo.estado());
        salida.println("1. Aprobar\n2. Cancelar\n3. Dejar pendiente");
        EstadoPrestamo nuevo = switch (entrada.leerEntero("Opcion: ")) {
            case 1 -> EstadoPrestamo.ACTIVO;
            case 2 -> EstadoPrestamo.CANCELADO;
            case 3 -> EstadoPrestamo.PENDIENTE;
            default -> throw new ErrorDeNegocio("Opcion invalida.");
        };
        prestamos.decidir(id, nuevo, empleadoId);
        salida.println("El prestamo " + id + " quedo " + nuevo + ".");
    }

    public void verTodosPorCliente() {
        Map<Integer, List<Prestamo>> agrupados = prestamos.agrupadosPorCliente();
        for (Cliente cliente : clientes.listar()) {
            mostrar(cliente, agrupados.getOrDefault(cliente.id(), List.of()));
        }
    }

    public void verDeUnCliente() {
        mostrarDe(entrada.leerEntero("ID del cliente: "));
    }

    public void mostrarDe(int clienteId) {
        mostrar(clientes.buscar(clienteId), prestamos.prestamosDe(clienteId));
    }

    private void mostrar(Cliente cliente, List<Prestamo> lista) {
        salida.println("\n=== " + cliente.nombre() + " | documento " + cliente.documento()
                + " | ingresos " + Formato.dinero(cliente.ingresos()) + " ===");
        if (lista.isEmpty()) {
            salida.println("(sin prestamos)");
        }
        lista.forEach(p -> salida.println("Prestamo " + p.id() + " | " + p.estado()
                + " | " + Formato.dinero(p.monto()) + " | " + p.cuotas() + " cuotas | desde " + p.fechaPrestamo()
                + " | total " + Formato.dinero(p.montoTotal())
                + " | pagado " + Formato.dinero(p.pagado())
                + " | saldo " + Formato.dinero(p.saldo())));
    }
}
