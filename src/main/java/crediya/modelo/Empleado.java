package crediya.modelo;

import crediya.util.Formato;
import java.math.BigDecimal;
import java.util.Objects;

public class Empleado extends Persona {

    private final Rol rol;
    private final BigDecimal salario;

    public Empleado(int id, String nombre, String documento, Rol rol, String correo, BigDecimal salario) {
        super(id, nombre, documento, correo);
        this.rol = Objects.requireNonNull(rol, "rol");
        this.salario = Objects.requireNonNull(salario, "salario");
    }

    public Rol rol() {
        return rol;
    }

    public BigDecimal salario() {
        return salario;
    }

    @Override
    public String tipo() {
        return "EMPLEADO";
    }

    @Override
    public String descripcion() {
        return id() + " | " + nombre() + " | " + documento() + " | " + rol
                + " | " + correo() + " | " + Formato.dinero(salario);
    }
}
