package crediya.modelo;

import java.math.BigDecimal;

public record Cliente(
        int id,
        String nombre,
        String documento,
        String correo,
        String telefono,
        BigDecimal ingresos) {
}
