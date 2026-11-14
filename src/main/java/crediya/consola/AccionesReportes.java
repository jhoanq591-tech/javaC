package crediya.consola;

import crediya.modelo.Prestamo;
import crediya.servicio.GestorPrestamos;
import crediya.servicio.LineaReporte;
import crediya.servicio.ServicioReportes;
import crediya.util.Formato;
import java.io.PrintStream;
import java.util.List;

public class AccionesReportes {

    private final PrintStream salida;
    private final ServicioReportes reportes;
    private final GestorPrestamos gestor;

    public AccionesReportes(PrintStream salida, ServicioReportes reportes, GestorPrestamos gestor) {
        this.salida = salida;
        this.reportes = reportes;
        this.gestor = gestor;
    }

    public void prestamosActivos() {
        imprimir("PRESTAMOS ACTIVOS", reportes.prestamosActivos());
    }

    public void prestamosMorosos() {
        imprimir("PRESTAMOS MOROSOS (activos con mas de 30 dias)", reportes.prestamosMorosos());
    }

    public void gestorDePrestamos() {
        List<Prestamo> todos = gestor.cargarPrestamos();
        gestor.mostrar("PRESTAMOS ACTIVOS", gestor.filtrarPrestamosActivos(todos));
        gestor.mostrar("PRESTAMOS VENCIDOS", gestor.filtrarPrestamosVencidos(todos));
        gestor.mostrarResumen(todos);
    }

    private void imprimir(String titulo, List<LineaReporte> lineas) {
        salida.println("--- " + titulo + " ---");
        if (lineas.isEmpty()) {
            salida.println("(ninguno)");
        }
        lineas.forEach(l -> salida.println("Prestamo " + l.prestamo().id() + " | " + l.nombreCliente()
                + " | " + Formato.dinero(l.prestamo().monto()) + " | desde " + l.prestamo().fechaPrestamo()));
    }
}
