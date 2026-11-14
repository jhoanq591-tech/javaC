package crediya.modelo;

import java.math.BigDecimal;

public record Empleado(
        int id,
        String nombre,
        String documento,
        Rol rol,
        String correo,
        BigDecimal salario) {
}
