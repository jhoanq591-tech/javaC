package crediya.servicio;

import crediya.excepcion.ErrorDeNegocio;
import crediya.modelo.Cliente;
import crediya.repositorio.RepositorioClientes;
import crediya.seguridad.HasheadorClaves;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ServicioClientes {

    private final RepositorioClientes repositorio;
    private final HasheadorClaves hasheador;

    public ServicioClientes(RepositorioClientes repositorio, HasheadorClaves hasheador) {
        this.repositorio = repositorio;
        this.hasheador = hasheador;
    }

    public void registrar(String nombre, String documento, String correo, String telefono,
                          char[] clave, BigDecimal ingresos) {
        Cliente cliente = new Cliente(0,
                Validaciones.nombre(nombre),
                Validaciones.documento(documento),
                Validaciones.correo(correo),
                Validaciones.telefono(telefono),
                Validaciones.montoPositivo(ingresos, "Los ingresos").setScale(2, RoundingMode.HALF_UP));
        String hash = hasheador.hashear(Validaciones.clave(clave));
        repositorio.guardar(cliente, hash);
    }

    public List<Cliente> listar() {
        return repositorio.listar();
    }

    public Cliente buscar(int id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ErrorDeNegocio("No existe un cliente con ese ID."));
    }
}
