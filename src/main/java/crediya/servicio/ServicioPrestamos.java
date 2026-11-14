package crediya.servicio;

import crediya.excepcion.ErrorDeNegocio;
import crediya.modelo.Cliente;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioClientes;
import crediya.repositorio.RepositorioPrestamos;
import crediya.util.Formato;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServicioPrestamos {

    private static final BigDecimal TOPE_SOBRE_INGRESOS = BigDecimal.valueOf(5);
    private static final BigDecimal INTERES = BigDecimal.valueOf(5).setScale(2);
    private static final int MAXIMO_CUOTAS = 120;

    private final RepositorioPrestamos prestamos;
    private final RepositorioClientes clientes;
    private final Clock reloj;

    public ServicioPrestamos(RepositorioPrestamos prestamos, RepositorioClientes clientes, Clock reloj) {
        this.prestamos = prestamos;
        this.clientes = clientes;
        this.reloj = reloj;
    }

    public Prestamo solicitar(int clienteId, BigDecimal monto, int cuotas) {
        Validaciones.montoPositivo(monto, "El monto");
        if (cuotas < 1 || cuotas > MAXIMO_CUOTAS) {
            throw new ErrorDeNegocio("Las cuotas deben estar entre 1 y " + MAXIMO_CUOTAS + ".");
        }
        Cliente cliente = clientes.buscarPorId(clienteId)
                .orElseThrow(() -> new ErrorDeNegocio("No existe un cliente con ese ID."));
        BigDecimal maximo = cliente.ingresos().multiply(TOPE_SOBRE_INGRESOS);
        if (monto.compareTo(maximo) > 0) {
            throw new ErrorDeNegocio("El monto supera lo permitido segun sus ingresos (maximo "
                    + Formato.dinero(maximo) + ").");
        }
        LocalDate hoy = LocalDate.now(reloj);
        Prestamo nuevo = new Prestamo(0, clienteId, monto.setScale(2, RoundingMode.HALF_UP), INTERES, cuotas,
                hoy, hoy.plusMonths(cuotas), EstadoPrestamo.PENDIENTE, BigDecimal.ZERO);
        return nuevo.conId(prestamos.crear(nuevo));
    }

    public void decidir(int prestamoId, EstadoPrestamo nuevoEstado, int empleadoId) {
        if (nuevoEstado == EstadoPrestamo.PAGADO) {
            throw new ErrorDeNegocio("Un prestamo solo pasa a PAGADO al registrar abonos.");
        }
        Prestamo prestamo = buscar(prestamoId);
        if (prestamo.estado() == EstadoPrestamo.PAGADO) {
            throw new ErrorDeNegocio("Ese prestamo ya esta pagado, no se puede cambiar.");
        }
        if (nuevoEstado == EstadoPrestamo.ACTIVO) {
            LocalDate hoy = LocalDate.now(reloj);
            prestamos.aprobar(prestamoId, empleadoId, hoy, hoy.plusMonths(prestamo.cuotas()));
        } else {
            prestamos.cambiarEstado(prestamoId, nuevoEstado, empleadoId);
        }
    }

    public Prestamo buscar(int prestamoId) {
        return prestamos.buscarPorId(prestamoId)
                .orElseThrow(() -> new ErrorDeNegocio("No existe un prestamo con ese ID."));
    }

    public List<Prestamo> prestamosDe(int clienteId) {
        return prestamos.listarPorCliente(clienteId);
    }

    public Map<Integer, List<Prestamo>> agrupadosPorCliente() {
        return prestamos.listarTodos().stream()
                .collect(Collectors.groupingBy(Prestamo::clienteId));
    }
}
