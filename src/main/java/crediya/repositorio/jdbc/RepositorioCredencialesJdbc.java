package crediya.repositorio.jdbc;

import crediya.configuracion.ProveedorConexion;
import crediya.modelo.Credencial;
import crediya.modelo.Rol;
import crediya.repositorio.RepositorioCredenciales;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class RepositorioCredencialesJdbc extends RepositorioJdbc implements RepositorioCredenciales {

    public RepositorioCredencialesJdbc(ProveedorConexion conexiones) {
        super(conexiones);
    }

    @Override
    public Optional<Credencial> buscarCliente(String documento) {
        return buscar("SELECT id, nombre, clave_hash FROM clientes WHERE documento = ?",
                this::mapear, documento);
    }

    @Override
    public Optional<Credencial> buscarAdministrador(String documento) {
        return buscar("SELECT id, nombre, clave_hash FROM empleados WHERE documento = ? AND rol = ?",
                this::mapear, documento, Rol.ADMINISTRADOR.name());
    }

    private Credencial mapear(ResultSet fila) throws SQLException {
        return new Credencial(fila.getInt("id"), fila.getString("nombre"), fila.getString("clave_hash"));
    }
}
