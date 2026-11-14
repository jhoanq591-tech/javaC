package crediya.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import crediya.excepcion.ErrorDeConexion;
import crediya.excepcion.ErrorDeDatos;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import crediya.pruebas.Datos;
import crediya.pruebas.PrestamosEnMemoria;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GestorPrestamosTest {

    private PrestamosEnMemoria repositorio;
    private ByteArrayOutputStream buffer;
    private GestorPrestamos gestor;

    @BeforeEach
    void preparar() {
        repositorio = new PrestamosEnMemoria(
                Datos.prestamo(1, EstadoPrestamo.ACTIVO, Datos.HOY, Datos.HOY.plusMonths(10), "1000000.00", "0.00"),
                Datos.prestamo(2, EstadoPrestamo.ACTIVO, Datos.HOY.minusDays(390), Datos.HOY.minusDays(30), "2000000.00", "100000.00"),
                Datos.prestamo(3, EstadoPrestamo.PAGADO, Datos.HOY.minusDays(240), Datos.HOY.minusDays(90), "500000.00", "525000.00"),
                Datos.prestamo(4, EstadoPrestamo.PENDIENTE, Datos.HOY, Datos.HOY.plusMonths(3), "300000.00", "0.00"));
        buffer = new ByteArrayOutputStream();
        gestor = new GestorPrestamos(repositorio, Datos.reloj(), new PrintStream(buffer, true, StandardCharsets.UTF_8));
    }

    private String salida() {
        return buffer.toString(StandardCharsets.UTF_8);
    }

    @Test
    void cargaTodosLosPrestamos() {
        assertEquals(4, gestor.cargarPrestamos().size());
    }

    @Test
    void filtraActivosPorEstado() {
        List<Prestamo> activos = gestor.filtrarPrestamosActivos(gestor.cargarPrestamos());
        assertEquals(List.of(1, 2), activos.stream().map(Prestamo::id).toList());
    }

    @Test
    void filtraVencidosPorFechaDeVencimiento() {
        List<Prestamo> vencidos = gestor.filtrarPrestamosVencidos(gestor.cargarPrestamos());
        assertEquals(List.of(2, 3), vencidos.stream().map(Prestamo::id).toList());
    }

    @Test
    void resumenCuentaYSumaElSaldoDeLosActivos() {
        gestor.mostrarResumen(gestor.cargarPrestamos());
        String texto = salida();
        assertTrue(texto.contains("Prestamos activos:    2"));
        assertTrue(texto.contains("Prestamos vencidos:   2"));
        assertTrue(texto.contains("$3.050.000,00"));
    }

    @Test
    void listaVaciaONulaNoFallaYAvisa() {
        assertTrue(gestor.filtrarPrestamosActivos(List.of()).isEmpty());
        assertTrue(gestor.filtrarPrestamosVencidos(null).isEmpty());
        gestor.mostrarResumen(null);
        assertTrue(salida().contains("la lista esta vacia"));
    }

    @Test
    void tablaSinDatosAvisa() {
        repositorio.datos.clear();
        assertTrue(gestor.cargarPrestamos().isEmpty());
        assertTrue(salida().contains("no tiene datos"));
    }

    @Test
    void errorDeConexionMuestraMensajeDescriptivo() {
        repositorio.falla = new ErrorDeConexion(new SQLException("boom"));
        assertTrue(gestor.cargarPrestamos().isEmpty());
        assertTrue(salida().contains("No se pudo conectar a la base de datos"));
    }

    @Test
    void errorDeDatosNoFiltraDetallesInternos() {
        repositorio.falla = new ErrorDeDatos("No se pudo completar la operacion con la base de datos.", new SQLException("secreto"));
        gestor.cargarPrestamos();
        assertTrue(salida().contains("Error al cargar los prestamos"));
        assertTrue(!salida().contains("secreto"));
    }
}
