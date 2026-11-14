package crediya.repositorio;

import crediya.modelo.Cliente;
import java.util.List;
import java.util.Optional;

public interface RepositorioClientes {

    List<Cliente> listar();

    Optional<Cliente> buscarPorId(int id);

    void guardar(Cliente cliente, String claveHash);
}
