package crediya.repositorio.jdbc;

import crediya.configuracion.ProveedorConexion;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioPrestamos;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RepositorioPrestamosJdbc extends RepositorioJdbc implements RepositorioPrestamos {

    private static final String SELECCION = "SELECT p.id, p.cliente_id, p.monto, p.interes, p.cuotas, "
            + "p.fecha_prestamo, p.fecha_vencimiento, p.estado, "
            + "(SELECT COALESCE(SUM(g.monto), 0) FROM pagos g WHERE g.prestamo_id = p.id) AS pagado "
            + "FROM prestamos p";

    public RepositorioPrestamosJdbc(ProveedorConexion conexiones) {
        super(conexiones);
    }

    @Override
    public List<Prestamo> listarTodos() {
        return listar(SELECCION + " ORDER BY p.id", this::mapear);
    }

    @Override
    public List<Prestamo> listarPorCliente(int clienteId) {
        return listar(SELECCION + " WHERE p.cliente_id = ? ORDER BY p.id", this::mapear, clienteId);
    }

    @Override
    public Optional<Prestamo> buscarPorId(int id) {
        return buscar(SELECCION + " WHERE p.id = ?", this::mapear, id);
    }

    @Override
    public int crear(Prestamo prestamo) {
        return insertarConId(
                "INSERT INTO prestamos (cliente_id, monto, interes, cuotas, fecha_prestamo, fecha_vencimiento, estado) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)",
                prestamo.clienteId(), prestamo.monto(), prestamo.interes(), prestamo.cuotas(),
                prestamo.fechaPrestamo(), prestamo.fechaVencimiento(), prestamo.estado().name());
    }

    @Override
    public void aprobar(int id, int empleadoId, LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
        actualizar("UPDATE prestamos SET estado = ?, empleado_id = ?, fecha_prestamo = ?, fecha_vencimiento = ? "
                        + "WHERE id = ?",
                EstadoPrestamo.ACTIVO.name(), empleadoId, fechaPrestamo, fechaVencimiento, id);
    }

    @Override
    public void cambiarEstado(int id, EstadoPrestamo estado, int empleadoId) {
        actualizar("UPDATE prestamos SET estado = ?, empleado_id = ? WHERE id = ?",
                estado.name(), empleadoId, id);
    }

    private Prestamo mapear(ResultSet fila) throws SQLException {
        return new Prestamo(
                fila.getInt("id"),
                fila.getInt("cliente_id"),
                fila.getBigDecimal("monto"),
                fila.getBigDecimal("interes"),
                fila.getInt("cuotas"),
                fila.getObject("fecha_prestamo", LocalDate.class),
                fila.getObject("fecha_vencimiento", LocalDate.class),
                EstadoPrestamo.valueOf(fila.getString("estado")),
                fila.getBigDecimal("pagado"));
    }
}
