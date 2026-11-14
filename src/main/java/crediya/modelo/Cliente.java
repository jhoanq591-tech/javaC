package crediya.modelo;

import crediya.util.Formato;
import java.math.BigDecimal;
import java.util.Objects;

public class Cliente extends Persona {

    private final String telefono;
    private final BigDecimal ingresos;

    public Cliente(int id, String nombre, String documento, String correo,
                   String telefono, BigDecimal ingresos) {
        super(id, nombre, documento, correo);
        this.telefono = Objects.requireNonNull(telefono, "telefono");
        this.ingresos = Objects.requireNonNull(ingresos, "ingresos");
    }

    public String telefono() {
        return telefono;
    }

    public BigDecimal ingresos() {
        return ingresos;
    }

    @Override
    public String tipo() {
        return "CLIENTE";
    }

    @Override
    public String descripcion() {
        return id() + " | " + nombre() + " | " + documento() + " | " + correo()
                + " | " + telefono + " | ingresos " + Formato.dinero(ingresos);
    }
}
