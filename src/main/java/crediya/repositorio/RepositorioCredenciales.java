package crediya.repositorio;

import crediya.modelo.Credencial;
import java.util.Optional;

public interface RepositorioCredenciales {

    Optional<Credencial> buscarCliente(String documento);

    Optional<Credencial> buscarAdministrador(String documento);
}
