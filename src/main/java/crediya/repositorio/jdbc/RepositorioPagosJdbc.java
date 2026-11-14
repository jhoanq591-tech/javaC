package crediya.repositorio.jdbc;

import crediya.configuracion.ProveedorConexion;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Pago;
import crediya.repositorio.RepositorioPagos;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class RepositorioPagosJdbc extends RepositorioJdbc implements RepositorioPagos {

    private static final String SELECCION = "SELECT id, prestamo_id, fecha_pago, monto FROM pagos";

    public RepositorioPagosJdbc(ProveedorConexion conexiones) {
        super(conexiones);
    }

    @Override
    public List<Pago> listarTodos() {
        return listar(SELECCION + " ORDER BY id", this::mapear);
    }

    @Override
    public List<Pago> listarPorPrestamo(int prestamoId) {
        return listar(SELECCION + " WHERE prestamo_id = ? ORDER BY id", this::mapear, prestamoId);
    }

    @Override
    public void registrarAbono(int prestamoId, BigDecimal monto, LocalDate fecha, boolean saldaPrestamo) {
        try (Connection conexion = conexiones().obtener()) {
            conexion.setAutoCommit(false);
            try {
                insertarPago(conexion, prestamoId, monto, fecha);
                if (saldaPrestamo) {
                    marcarPagado(conexion, prestamoId);
                }
                conexion.commit();
            } catch (SQLException | RuntimeException e) {
                conexion.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw traducir(e);
        }
    }

    private void insertarPago(Connection conexion, int prestamoId, BigDecimal monto, LocalDate fecha)
            throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "INSERT INTO pagos (prestamo_id, fecha_pago, monto) VALUES (?, ?, ?)")) {
            sentencia.setInt(1, prestamoId);
            sentencia.setObject(2, fecha);
            sentencia.setBigDecimal(3, monto);
            sentencia.executeUpdate();
        }
    }

    private void marcarPagado(Connection conexion, int prestamoId) throws SQLException {
        try (PreparedStatement sentencia = conexion.prepareStatement(
                "UPDATE prestamos SET estado = ? WHERE id = ?")) {
            sentencia.setString(1, EstadoPrestamo.PAGADO.name());
            sentencia.setInt(2, prestamoId);
            sentencia.executeUpdate();
        }
    }

    private Pago mapear(ResultSet fila) throws SQLException {
        return new Pago(
                fila.getInt("id"),
                fila.getInt("prestamo_id"),
                fila.getObject("fecha_pago", LocalDate.class),
                fila.getBigDecimal("monto"));
    }
}
