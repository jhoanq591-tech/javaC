package crediya.servicio;

import crediya.modelo.Credencial;
import crediya.repositorio.RepositorioCredenciales;
import crediya.seguridad.HasheadorClaves;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Optional;

public class ServicioAutenticacion {

    private final RepositorioCredenciales credenciales;
    private final HasheadorClaves hasheador;
    private final String hashFicticio;

    public ServicioAutenticacion(RepositorioCredenciales credenciales, HasheadorClaves hasheador) {
        this.credenciales = credenciales;
        this.hasheador = hasheador;
        this.hashFicticio = hasheador.hashear(claveAleatoria());
    }

    public Optional<Credencial> ingresarCliente(String documento, char[] clave) {
        return verificar(credenciales.buscarCliente(documento), clave);
    }

    public Optional<Credencial> ingresarAdministrador(String documento, char[] clave) {
        return verificar(credenciales.buscarAdministrador(documento), clave);
    }

    private Optional<Credencial> verificar(Optional<Credencial> encontrada, char[] clave) {
        String hash = encontrada.map(Credencial::claveHash).orElse(hashFicticio);
        boolean valida = hasheador.verificar(clave, hash);
        return encontrada.filter(credencial -> valida);
    }

    private static char[] claveAleatoria() {
        byte[] bytes = new byte[24];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes).toCharArray();
    }
}
