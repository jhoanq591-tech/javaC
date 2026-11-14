package crediya.pruebas;

import crediya.modelo.Cliente;
import crediya.repositorio.RepositorioClientes;
import java.util.List;
import java.util.Optional;

public class ClientesEnMemoria implements RepositorioClientes {

    private final List<Cliente> datos;

    public ClientesEnMemoria(Cliente... clientes) {
        this.datos = List.of(clientes);
    }

    @Override
    public List<Cliente> listar() {
        return datos;
    }

    @Override
    public Optional<Cliente> buscarPorId(int id) {
        return datos.stream().filter(c -> c.id() == id).findFirst();
    }

    @Override
    public void guardar(Cliente cliente, String claveHash) {
    }
}
