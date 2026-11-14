package crediya.repositorio.jdbc;

import crediya.configuracion.ProveedorConexion;
import crediya.excepcion.ErrorDeDatos;
import crediya.excepcion.ErrorDeNegocio;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class RepositorioJdbc {

    private static final Logger REGISTRO = Logger.getLogger(RepositorioJdbc.class.getName());
    private static final String MENSAJE_GENERICO = "No se pudo completar la operacion con la base de datos.";
    private static final String MENSAJE_DUPLICADO = "Ya existe un registro con esos datos.";

    @FunctionalInterface
    protected interface Mapeador<T> {
        T mapear(ResultSet resultado) throws SQLException;
    }

    private final ProveedorConexion conexiones;

    protected RepositorioJdbc(ProveedorConexion conexiones) {
        this.conexiones = conexiones;
    }

    protected ProveedorConexion conexiones() {
        return conexiones;
    }

    protected <T> List<T> listar(String sql, Mapeador<T> mapeador, Object... parametros) {
        try (Connection conexion = conexiones.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignar(sentencia, parametros);
            try (ResultSet resultado = sentencia.executeQuery()) {
                List<T> filas = new ArrayList<>();
                while (resultado.next()) {
                    filas.add(mapeador.mapear(resultado));
                }
                return filas;
            }
        } catch (SQLException e) {
            throw traducir(e);
        }
    }

    protected <T> Optional<T> buscar(String sql, Mapeador<T> mapeador, Object... parametros) {
        return listar(sql, mapeador, parametros).stream().findFirst();
    }

    protected int actualizar(String sql, Object... parametros) {
        try (Connection conexion = conexiones.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {
            asignar(sentencia, parametros);
            return sentencia.executeUpdate();
        } catch (SQLException e) {
            throw traducir(e);
        }
    }

    protected int insertarConId(String sql, Object... parametros) {
        try (Connection conexion = conexiones.obtener();
             PreparedStatement sentencia = conexion.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            asignar(sentencia, parametros);
            sentencia.executeUpdate();
            try (ResultSet claves = sentencia.getGeneratedKeys()) {
                if (!claves.next()) {
                    throw new ErrorDeDatos(MENSAJE_GENERICO, null);
                }
                return claves.getInt(1);
            }
        } catch (SQLException e) {
            throw traducir(e);
        }
    }

    protected RuntimeException traducir(SQLException e) {
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return new ErrorDeNegocio(MENSAJE_DUPLICADO);
        }
        REGISTRO.log(Level.WARNING, "Error SQL. SQLState={0} codigo={1}",
                new Object[]{e.getSQLState(), e.getErrorCode()});
        return new ErrorDeDatos(MENSAJE_GENERICO, e);
    }

    private void asignar(PreparedStatement sentencia, Object[] parametros) throws SQLException {
        for (int i = 0; i < parametros.length; i++) {
            sentencia.setObject(i + 1, parametros[i]);
        }
    }
}
