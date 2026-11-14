package crediya.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import crediya.modelo.Cliente;
import crediya.modelo.Empleado;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Persona;
import crediya.modelo.Prestamo;
import crediya.modelo.Rol;
import crediya.pruebas.ClientesEnMemoria;
import crediya.pruebas.Datos;
import crediya.pruebas.PrestamosEnMemoria;
import crediya.repositorio.RepositorioEmpleados;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;

class ReportesYPersonasTest {

    private final Cliente carlos = new Cliente(1, "Carlos Ruiz", "2001", "c@e.com", "3001112222", new BigDecimal("2000000.00"));
    private final Cliente ana = new Cliente(2, "Ana Torres", "2002", "a@e.com", "3003334444", new BigDecimal("3000000.00"));
    private final Cliente pedro = new Cliente(3, "Pedro Castro", "2003", "p@e.com", "3005556666", new BigDecimal("1500000.00"));

    private Prestamo de(int id, int clienteId, EstadoPrestamo estado, int diasDesdeInicio, String monto) {
        return new Prestamo(id, clienteId, null, new BigDecimal(monto), new BigDecimal("5.00"), 10,
                Datos.HOY.minusDays(diasDesdeInicio), Datos.HOY.plusDays(60), estado, BigDecimal.ZERO);
    }

    @Test
    void clientesMorososAgrupaPorClienteYOrdenaPorSaldo() {
        PrestamosEnMemoria prestamos = new PrestamosEnMemoria(
                de(1, 1, EstadoPrestamo.ACTIVO, 40, "1000000.00"),
                de(2, 1, EstadoPrestamo.ACTIVO, 50, "1000000.00"),
                de(3, 2, EstadoPrestamo.ACTIVO, 45, "5000000.00"),
                de(4, 3, EstadoPrestamo.ACTIVO, 5, "900000.00"),
                de(5, 3, EstadoPrestamo.PAGADO, 100, "900000.00"),
                de(6, 2, EstadoPrestamo.PENDIENTE, 90, "900000.00"));
        ServicioReportes reportes = new ServicioReportes(prestamos, new ClientesEnMemoria(carlos, ana, pedro), Datos.reloj());

        List<ClienteMoroso> morosos = reportes.clientesMorosos();

        assertEquals(2, morosos.size());
        assertEquals("Ana Torres", morosos.get(0).cliente().nombre());
        assertEquals(1, morosos.get(0).prestamosEnMora());
        assertEquals(new BigDecimal("5250000.00"), morosos.get(0).saldoEnMora());
        assertEquals("Carlos Ruiz", morosos.get(1).cliente().nombre());
        assertEquals(2, morosos.get(1).prestamosEnMora());
        assertEquals(new BigDecimal("2100000.00"), morosos.get(1).saldoEnMora());
    }

    @Test
    void sinMorososDevuelveListaVacia() {
        PrestamosEnMemoria prestamos = new PrestamosEnMemoria(de(1, 1, EstadoPrestamo.ACTIVO, 3, "1000000.00"));
        assertTrue(new ServicioReportes(prestamos, new ClientesEnMemoria(carlos), Datos.reloj()).clientesMorosos().isEmpty());
    }

    @Test
    void directorioMezclaClientesYEmpleadosPolimorficamenteOrdenadosPorNombre() {
        Empleado laura = new Empleado(1, "Laura Gomez", "1001", Rol.ADMINISTRADOR, "l@e.com", new BigDecimal("3500000.00"));
        RepositorioEmpleados empleados = new RepositorioEmpleados() {
            @Override
            public List<Empleado> listar() {
                return List.of(laura);
            }

            @Override
            public void guardar(Empleado empleado, String claveHash) {
            }
        };
        List<Persona> personas = new ServicioDirectorio(new ClientesEnMemoria(carlos, ana), empleados).listar();

        assertEquals(List.of("Ana Torres", "Carlos Ruiz", "Laura Gomez"), personas.stream().map(Persona::nombre).toList());
        assertEquals(List.of("CLIENTE", "CLIENTE", "EMPLEADO"), personas.stream().map(Persona::tipo).toList());
        assertTrue(personas.get(2).descripcion().contains("ADMINISTRADOR"));
        assertTrue(personas.get(0).descripcion().contains("ingresos"));
    }

    @Test
    void elPrestamoCreadoPorUnEmpleadoQuedaAsociadoYElSolicitadoPorElClienteNo() {
        PrestamosEnMemoria prestamos = new PrestamosEnMemoria();
        ServicioPrestamos servicio = new ServicioPrestamos(prestamos, new ClientesEnMemoria(carlos), Datos.reloj());

        Prestamo porEmpleado = servicio.crearParaCliente(1, 3, new BigDecimal("100000"), 4);
        Prestamo porCliente = servicio.solicitar(1, new BigDecimal("100000"), 4);

        assertEquals(3, porEmpleado.empleadoId());
        assertEquals(1, porEmpleado.clienteId());
        assertNull(porCliente.empleadoId());
    }
}
