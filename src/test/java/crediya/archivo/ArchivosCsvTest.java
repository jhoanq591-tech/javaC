package crediya.archivo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import crediya.excepcion.ErrorDeNegocio;
import crediya.modelo.Empleado;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import crediya.modelo.Rol;
import crediya.pruebas.Datos;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class ArchivosCsvTest {

    @TempDir
    Path carpeta;

    @Test
    void csvEscapaComillasYComasYSeRecuperaIgual() {
        String original = "Ana \"La Jefa\", S.A.";
        List<String> campos = Csv.separar(Csv.texto(original) + "," + Csv.texto("otro"));
        assertEquals(original, Csv.restaurar(campos.get(0)));
        assertEquals("otro", Csv.restaurar(campos.get(1)));
    }

    @Test
    void csvNeutralizaFormulasPeligrosas() {
        String protegido = Csv.texto("=HYPERLINK(\"http://malo\")");
        assertTrue(protegido.startsWith("\"'="));
        assertEquals("=HYPERLINK(\"http://malo\")", Csv.restaurar(Csv.separar(protegido).get(0)));
    }

    @Test
    void csvRechazaComillasSinCerrar() {
        assertThrows(IllegalArgumentException.class, () -> Csv.separar("\"abierta,1"));
    }

    @Test
    void guardaYLeeEmpleadosSinGuardarClaves() throws IOException {
        Path ruta = carpeta.resolve("datos").resolve("empleados.csv");
        AlmacenCsvEmpleados almacen = new AlmacenCsvEmpleados(ruta);
        Empleado original = new Empleado(7, "=Mario, Rojas", "1002", Rol.ASESOR, "mario@ejemplo.com",
                new BigDecimal("2200000.00"));
        almacen.guardar(List.of(original));

        List<Empleado> leidos = almacen.leer();
        assertEquals(1, leidos.size());
        assertEquals(original, leidos.get(0));
        assertEquals("=Mario, Rojas", leidos.get(0).nombre());
        assertEquals(Rol.ASESOR, leidos.get(0).rol());
        assertFalse(Files.readString(ruta, StandardCharsets.UTF_8).toLowerCase().contains("clave"));
    }

    @Test
    void guardaYLeePrestamosConEmpleadoNulo() {
        AlmacenCsvPrestamos almacen = new AlmacenCsvPrestamos(carpeta.resolve("prestamos.csv"));
        Prestamo sinEmpleado = Datos.prestamo(1, EstadoPrestamo.PENDIENTE, Datos.HOY, Datos.HOY.plusMonths(3),
                "300000.00", "0.00");
        Prestamo conEmpleado = new Prestamo(2, 1, 3, new BigDecimal("1000000.00"), new BigDecimal("5.00"), 10,
                Datos.HOY, Datos.HOY.plusMonths(10), EstadoPrestamo.ACTIVO, new BigDecimal("50000.00"));
        almacen.guardar(List.of(sinEmpleado, conEmpleado));

        List<Prestamo> leidos = almacen.leer();
        assertEquals(List.of(sinEmpleado, conEmpleado), leidos);
        assertEquals(3, leidos.get(1).empleadoId());
    }

    @Test
    void sobrescribeElArchivoSinDejarTemporales() throws IOException {
        AlmacenCsvPrestamos almacen = new AlmacenCsvPrestamos(carpeta.resolve("prestamos.csv"));
        almacen.guardar(List.of(Datos.prestamo(1, EstadoPrestamo.ACTIVO, Datos.HOY, Datos.HOY, "1.00", "0")));
        almacen.guardar(List.of());
        assertTrue(almacen.leer().isEmpty());
        try (var archivos = Files.list(carpeta)) {
            assertEquals(1, archivos.count());
        }
    }

    @Test
    void archivoInexistenteSeLeeVacio() {
        assertTrue(new AlmacenCsvPrestamos(carpeta.resolve("no-existe.csv")).leer().isEmpty());
    }

    @Test
    void archivoDanadoDaMensajeConLaLinea() throws IOException {
        Path ruta = carpeta.resolve("prestamos.csv");
        Files.writeString(ruta, "id,cliente_id\nbasura,1\n", StandardCharsets.UTF_8);
        ErrorDeNegocio error = assertThrows(ErrorDeNegocio.class, () -> new AlmacenCsvPrestamos(ruta).leer());
        assertTrue(error.getMessage().contains("linea 2"));
    }
}
