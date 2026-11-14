package crediya.repositorio;

import crediya.modelo.Empleado;
import java.util.List;

public interface RepositorioEmpleados {

    List<Empleado> listar();

    void guardar(Empleado empleado, String claveHash);
}
