package crediya.servicio;

import crediya.modelo.Empleado;
import crediya.modelo.Rol;
import crediya.repositorio.RepositorioEmpleados;
import crediya.seguridad.HasheadorClaves;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class ServicioEmpleados {

    private final RepositorioEmpleados repositorio;
    private final HasheadorClaves hasheador;

    public ServicioEmpleados(RepositorioEmpleados repositorio, HasheadorClaves hasheador) {
        this.repositorio = repositorio;
        this.hasheador = hasheador;
    }

    public void registrar(String nombre, String documento, String rol, String correo,
                          BigDecimal salario, char[] clave) {
        Empleado empleado = new Empleado(0,
                Validaciones.nombre(nombre),
                Validaciones.documento(documento),
                Rol.desde(rol),
                Validaciones.correo(correo),
                Validaciones.montoPositivo(salario, "El salario").setScale(2, RoundingMode.HALF_UP));
        String hash = hasheador.hashear(Validaciones.clave(clave));
        repositorio.guardar(empleado, hash);
    }

    public List<Empleado> listar() {
        return repositorio.listar();
    }
}
