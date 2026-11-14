package crediya.servicio;

import crediya.modelo.Persona;
import crediya.repositorio.RepositorioClientes;
import crediya.repositorio.RepositorioEmpleados;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ServicioDirectorio {

    private final RepositorioClientes clientes;
    private final RepositorioEmpleados empleados;

    public ServicioDirectorio(RepositorioClientes clientes, RepositorioEmpleados empleados) {
        this.clientes = clientes;
        this.empleados = empleados;
    }

    public List<Persona> listar() {
        List<Persona> personas = new ArrayList<>(clientes.listar());
        personas.addAll(empleados.listar());
        personas.sort(Comparator.comparing(Persona::nombre, String.CASE_INSENSITIVE_ORDER));
        return personas;
    }
}
