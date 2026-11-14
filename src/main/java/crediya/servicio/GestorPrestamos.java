package crediya.servicio;

import crediya.excepcion.ErrorDeConexion;
import crediya.excepcion.ErrorDeDatos;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioPrestamos;
import crediya.util.Formato;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class GestorPrestamos {

    private static final String LISTA_VACIA = "No hay prestamos para procesar (la lista esta vacia).";

    private final RepositorioPrestamos repositorio;
    private final Clock reloj;
    private final PrintStream salida;

    public GestorPrestamos(RepositorioPrestamos repositorio, Clock reloj, PrintStream salida) {
        this.repositorio = repositorio;
        this.reloj = reloj;
        this.salida = salida;
    }

    public List<Prestamo> cargarPrestamos() {
        try {
            List<Prestamo> prestamos = repositorio.listarTodos();
            if (prestamos.isEmpty()) {
                salida.println("La tabla prestamos no tiene datos.");
            }
            return prestamos;
        } catch (ErrorDeConexion e) {
            salida.println(e.getMessage());
        } catch (ErrorDeDatos e) {
            salida.println("Error al cargar los prestamos: " + e.getMessage());
        }
        return List.of();
    }

    public List<Prestamo> filtrarPrestamosVencidos(List<Prestamo> lista) {
        try {
            if (estaVacia(lista)) {
                salida.println(LISTA_VACIA);
                return List.of();
            }
            LocalDate hoy = LocalDate.now(reloj);
            return lista.stream()
                    .filter(prestamo -> prestamo.estaVencido(hoy))
                    .toList();
        } catch (RuntimeException e) {
            salida.println("Error al filtrar los prestamos vencidos.");
            return List.of();
        }
    }

    public List<Prestamo> filtrarPrestamosActivos(List<Prestamo> lista) {
        try {
            if (estaVacia(lista)) {
                salida.println(LISTA_VACIA);
                return List.of();
            }
            return lista.stream()
                    .filter(Prestamo::estaActivo)
                    .toList();
        } catch (RuntimeException e) {
            salida.println("Error al filtrar los prestamos activos.");
            return List.of();
        }
    }

    public void mostrarResumen(List<Prestamo> lista) {
        try {
            if (estaVacia(lista)) {
                salida.println(LISTA_VACIA);
                return;
            }
            List<Prestamo> activos = filtrarPrestamosActivos(lista);
            List<Prestamo> vencidos = filtrarPrestamosVencidos(lista);
            BigDecimal totalAdeudado = activos.stream()
                    .map(Prestamo::saldo)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            salida.println("=========== RESUMEN DE PRESTAMOS ===========");
            salida.println("Total de prestamos:   " + lista.size());
            salida.println("Prestamos activos:    " + activos.size());
            salida.println("Prestamos vencidos:   " + vencidos.size());
            salida.println("Monto total adeudado: " + Formato.dinero(totalAdeudado));
            salida.println("============================================");
        } catch (RuntimeException e) {
            salida.println("Error al mostrar el resumen.");
        }
    }

    public void mostrar(String titulo, List<Prestamo> lista) {
        salida.println("--- " + titulo + " (" + lista.size() + ") ---");
        if (lista.isEmpty()) {
            salida.println("(ninguno)");
            return;
        }
        lista.forEach(salida::println);
    }

    private boolean estaVacia(List<Prestamo> lista) {
        return lista == null || lista.isEmpty();
    }
}
