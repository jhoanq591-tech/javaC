package crediya.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import crediya.excepcion.ErrorDeNegocio;
import crediya.modelo.Cliente;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Pago;
import crediya.modelo.Prestamo;
import crediya.pruebas.Datos;
import crediya.pruebas.PrestamosEnMemoria;
import crediya.repositorio.RepositorioClientes;
import crediya.repositorio.RepositorioPagos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServicioPrestamosPagosTest {

    private PrestamosEnMemoria prestamos;
    private ServicioPrestamos servicioPrestamos;
    private ServicioPagos servicioPagos;
    private final List<Pago> pagosGuardados = new ArrayList<>();
    private boolean prestamoSaldado;

    @BeforeEach
    void preparar() {
        prestamos = new PrestamosEnMemoria(
                Datos.prestamo(1, EstadoPrestamo.ACTIVO, Datos.HOY, Datos.HOY.plusMonths(10), "1000000.00", "0.00"),
                Datos.prestamo(2, EstadoPrestamo.PAGADO, Datos.HOY, Datos.HOY.plusMonths(10), "1000000.00", "1050000.00"),
                Datos.prestamo(3, EstadoPrestamo.PENDIENTE, Datos.HOY, Datos.HOY.plusMonths(10), "1000000.00", "0.00"));
        RepositorioClientes clientes = new RepositorioClientes() {
            @Override
            public List<Cliente> listar() {
                return List.of();
            }

            @Override
            public Optional<Cliente> buscarPorId(int id) {
                return id == 1
                        ? Optional.of(new Cliente(1, "Carlos", "2001", "c@e.com", "3001112222", new BigDecimal("2000000.00")))
                        : Optional.empty();
            }

            @Override
            public void guardar(Cliente cliente, String claveHash) {
            }
        };
        RepositorioPagos pagos = new RepositorioPagos() {
            @Override
            public List<Pago> listarTodos() {
                return pagosGuardados;
            }

            @Override
            public List<Pago> listarPorPrestamo(int prestamoId) {
                return pagosGuardados;
            }

            @Override
            public void registrarAbono(int prestamoId, BigDecimal monto, LocalDate fecha, boolean saldaPrestamo) {
                pagosGuardados.add(new Pago(pagosGuardados.size() + 1, prestamoId, fecha, monto));
                prestamoSaldado = saldaPrestamo;
            }
        };
        servicioPrestamos = new ServicioPrestamos(prestamos, clientes, Datos.reloj());
        servicioPagos = new ServicioPagos(pagos, prestamos, Datos.reloj());
    }

    @Test
    void solicitaUnPrestamoPendienteConVencimientoSegunCuotas() {
        Prestamo nuevo = servicioPrestamos.solicitar(1, new BigDecimal("100000"), 4);
        assertEquals(EstadoPrestamo.PENDIENTE, nuevo.estado());
        assertEquals(Datos.HOY.plusMonths(4), nuevo.fechaVencimiento());
        assertEquals(new BigDecimal("105000.00"), nuevo.montoTotal());
        assertEquals(new BigDecimal("26250.00"), nuevo.valorCuota());
    }

    @Test
    void rechazaMontoSobreElTopeDeIngresos() {
        assertThrows(ErrorDeNegocio.class, () -> servicioPrestamos.solicitar(1, new BigDecimal("10000001"), 4));
    }

    @Test
    void rechazaDatosInvalidos() {
        assertThrows(ErrorDeNegocio.class, () -> servicioPrestamos.solicitar(1, BigDecimal.ZERO, 4));
        assertThrows(ErrorDeNegocio.class, () -> servicioPrestamos.solicitar(1, BigDecimal.TEN, 0));
        assertThrows(ErrorDeNegocio.class, () -> servicioPrestamos.solicitar(1, BigDecimal.TEN, 121));
        assertThrows(ErrorDeNegocio.class, () -> servicioPrestamos.solicitar(99, BigDecimal.TEN, 4));
    }

    @Test
    void aprobarFijaFechasNuevasYUnPrestamoPagadoNoCambia() {
        servicioPrestamos.decidir(3, EstadoPrestamo.ACTIVO, 1);
        Prestamo aprobado = servicioPrestamos.buscar(3);
        assertEquals(EstadoPrestamo.ACTIVO, aprobado.estado());
        assertEquals(Datos.HOY.plusMonths(10), aprobado.fechaVencimiento());
        assertThrows(ErrorDeNegocio.class, () -> servicioPrestamos.decidir(2, EstadoPrestamo.CANCELADO, 1));
        assertThrows(ErrorDeNegocio.class, () -> servicioPrestamos.decidir(3, EstadoPrestamo.PAGADO, 1));
    }

    @Test
    void abonoParcialActualizaSaldoSinSaldarElPrestamo() {
        BigDecimal saldo = servicioPagos.registrarAbono(1, new BigDecimal("50000"));
        assertEquals(new BigDecimal("1000000.00"), saldo);
        assertEquals(1, pagosGuardados.size());
        assertTrue(!prestamoSaldado);
    }

    @Test
    void abonoQueCubreElSaldoMarcaElPrestamoComoPagado() {
        BigDecimal saldo = servicioPagos.registrarAbono(1, new BigDecimal("1050000"));
        assertEquals(0, saldo.signum());
        assertTrue(prestamoSaldado);
    }

    @Test
    void rechazaAbonosInvalidos() {
        assertThrows(ErrorDeNegocio.class, () -> servicioPagos.registrarAbono(1, new BigDecimal("1050000.01")));
        assertThrows(ErrorDeNegocio.class, () -> servicioPagos.registrarAbono(1, BigDecimal.ZERO));
        assertThrows(ErrorDeNegocio.class, () -> servicioPagos.registrarAbono(2, BigDecimal.TEN));
        assertThrows(ErrorDeNegocio.class, () -> servicioPagos.registrarAbono(3, BigDecimal.TEN));
        assertThrows(ErrorDeNegocio.class, () -> servicioPagos.registrarAbono(99, BigDecimal.TEN));
    }
}
