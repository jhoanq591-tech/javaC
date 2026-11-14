package crediya.repositorio.jdbc;

import crediya.configuracion.ProveedorConexion;
import crediya.modelo.Cliente;
import crediya.repositorio.RepositorioClientes;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class RepositorioClientesJdbc extends RepositorioJdbc implements RepositorioClientes {

    private static final String SELECCION = "SELECT id, nombre, documento, correo, telefono, ingresos FROM clientes";

    public RepositorioClientesJdbc(ProveedorConexion conexiones) {
        super(conexiones);
    }

    @Override
    public List<Cliente> listar() {
        return listar(SELECCION + " ORDER BY id", this::mapear);
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return buscar(SELECCION + " WHERE id = ?", this::mapear, id);
    }

    @Override
    public void guardar(Cliente cliente, String claveHash) {
        actualizar("INSERT INTO clientes (nombre, documento, correo, telefono, clave_hash, ingresos) "
                        + "VALUES (?, ?, ?, ?, ?, ?)",
                cliente.nombre(), cliente.documento(), cliente.correo(), cliente.telefono(),
                claveHash, cliente.ingresos());
    }

    private Cliente mapear(ResultSet fila) throws SQLException {
        return new Cliente(
                fila.getInt("id"),
                fila.getString("nombre"),
                fila.getString("documento"),
                fila.getString("correo"),
                fila.getString("telefono"),
                fila.getBigDecimal("ingresos"));
    }
}
