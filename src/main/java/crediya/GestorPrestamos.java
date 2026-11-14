package crediya;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Consulta y filtra los prestamos activos y vencidos.
 * Carga los datos con JDBC y los filtra con lambdas y Stream API.
 */
public class GestorPrestamos {

    private Conexion conexion = new Conexion();

    /** Carga todos los prestamos de la tabla prestamos en una lista de objetos Prestamo. */
    public List<Prestamo> cargarPrestamos() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT id, cliente_id, monto, interes, fecha_prestamo, fecha_vencimiento, estado "
                + "FROM prestamos ORDER BY id";

        try (Connection con = conexion.conectar();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Date prestamo = rs.getDate("fecha_prestamo");
                Date vencimiento = rs.getDate("fecha_vencimiento");
                lista.add(new Prestamo(
                        rs.getInt("id"),
                        rs.getInt("cliente_id"),
                        rs.getDouble("monto"),
                        rs.getDouble("interes"),
                        prestamo == null ? null : prestamo.toLocalDate(),
                        vencimiento == null ? null : vencimiento.toLocalDate(),
                        rs.getString("estado")));
            }

            if (lista.isEmpty()) {
                System.out.println("Aviso: la tabla prestamos no tiene datos.");
            }
        } catch (Exception e) {
            // conectar() ya trae un mensaje descriptivo de conexion; los errores de SQL se explican aqui
            System.out.println("Error al cargar los prestamos: " + e.getMessage());
        }
        return lista;
    }

    /** Prestamos cuya fecha de vencimiento es anterior a hoy. */
    public List<Prestamo> filtrarPrestamosVencidos(List<Prestamo> lista) {
        try {
            if (lista == null || lista.isEmpty()) {
                System.out.println("No hay prestamos para filtrar (la lista esta vacia).");
                return new ArrayList<>();
            }
            LocalDate hoy = LocalDate.now();
            return lista.stream()
                    .filter(p -> p.getFechaVencimiento() != null)
                    .filter(p -> p.getFechaVencimiento().isBefore(hoy))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error al filtrar los prestamos vencidos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /** Prestamos con estado ACTIVO. */
    public List<Prestamo> filtrarPrestamosActivos(List<Prestamo> lista) {
        try {
            if (lista == null || lista.isEmpty()) {
                System.out.println("No hay prestamos para filtrar (la lista esta vacia).");
                return new ArrayList<>();
            }
            return lista.stream()
                    .filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            System.out.println("Error al filtrar los prestamos activos: " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Imprime cuantos prestamos hay activos, cuantos vencidos y el monto total adeudado.
     * Monto adeudado = suma del monto (con interes) de los prestamos ACTIVOS.
     */
    public void mostrarResumen(List<Prestamo> lista) {
        try {
            if (lista == null || lista.isEmpty()) {
                System.out.println("No hay prestamos para resumir (la lista esta vacia).");
                return;
            }
            List<Prestamo> activos = filtrarPrestamosActivos(lista);
            List<Prestamo> vencidos = filtrarPrestamosVencidos(lista);
            double totalAdeudado = activos.stream()
                    .mapToDouble(Prestamo::getMontoTotal)
                    .sum();

            System.out.println("=========== RESUMEN DE PRESTAMOS ===========");
            System.out.println("Total de prestamos:        " + lista.size());
            System.out.println("Prestamos activos:         " + activos.size());
            System.out.println("Prestamos vencidos:        " + vencidos.size());
            System.out.printf("Monto total adeudado:      $%,.2f%n", totalAdeudado);
            System.out.println("============================================");
        } catch (Exception e) {
            System.out.println("Error al mostrar el resumen: " + e.getMessage());
        }
    }

    /** Imprime una lista de prestamos, uno por linea. */
    public void imprimir(String titulo, List<Prestamo> lista) {
        System.out.println("--- " + titulo + " (" + lista.size() + ") ---");
        if (lista.isEmpty()) {
            System.out.println("(ninguno)");
        } else {
            lista.forEach(System.out::println);
        }
    }
}
