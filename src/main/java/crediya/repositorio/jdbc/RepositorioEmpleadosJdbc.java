package crediya.repositorio.jdbc;

import crediya.configuracion.ProveedorConexion;
import crediya.modelo.Empleado;
import crediya.modelo.Rol;
import crediya.repositorio.RepositorioEmpleados;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class RepositorioEmpleadosJdbc extends RepositorioJdbc implements RepositorioEmpleados {

    public RepositorioEmpleadosJdbc(ProveedorConexion conexiones) {
        super(conexiones);
    }

    @Override
    public List<Empleado> listar() {
        return listar("SELECT id, nombre, documento, rol, correo, salario FROM empleados ORDER BY id", this::mapear);
    }

    @Override
    public void guardar(Empleado empleado, String claveHash) {
        actualizar("INSERT INTO empleados (nombre, documento, rol, correo, salario, clave_hash) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                empleado.nombre(), empleado.documento(), empleado.rol().name(), empleado.correo(),
                empleado.salario(), claveHash);
    }

    private Empleado mapear(ResultSet fila) throws SQLException {
        return new Empleado(
                fila.getInt("id"),
                fila.getString("nombre"),
                fila.getString("documento"),
                Rol.valueOf(fila.getString("rol")),
                fila.getString("correo"),
                fila.getBigDecimal("salario"));
    }
}
