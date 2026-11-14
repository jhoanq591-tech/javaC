package crediya.configuracion;

import crediya.excepcion.ErrorDeConexion;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConexionMySql implements ProveedorConexion {

    private static final Logger REGISTRO = Logger.getLogger(ConexionMySql.class.getName());

    private final ConfiguracionBaseDatos configuracion;

    public ConexionMySql(ConfiguracionBaseDatos configuracion) {
        this.configuracion = configuracion;
    }

    @Override
    public Connection obtener() {
        try {
            return DriverManager.getConnection(
                    configuracion.url(), configuracion.usuario(), configuracion.clave());
        } catch (SQLException e) {
            REGISTRO.log(Level.WARNING, "Fallo de conexion. SQLState={0} codigo={1}",
                    new Object[]{e.getSQLState(), e.getErrorCode()});
            throw new ErrorDeConexion(e);
        }
    }
}
