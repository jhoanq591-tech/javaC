package crediya.repositorio.archivo;

import crediya.archivo.Sincronizador;
import crediya.modelo.Empleado;
import crediya.repositorio.RepositorioEmpleados;
import java.util.List;

public class RepositorioEmpleadosConArchivo implements RepositorioEmpleados {

    private final RepositorioEmpleados base;
    private final Sincronizador<Empleado> respaldo;

    public RepositorioEmpleadosConArchivo(RepositorioEmpleados base, Sincronizador<Empleado> respaldo) {
        this.base = base;
        this.respaldo = respaldo;
    }

    @Override
    public List<Empleado> listar() {
        return base.listar();
    }

    @Override
    public void guardar(Empleado empleado, String claveHash) {
        base.guardar(empleado, claveHash);
        respaldo.sincronizar();
    }
}
