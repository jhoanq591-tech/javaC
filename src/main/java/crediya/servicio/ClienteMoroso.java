package crediya.servicio;

import crediya.modelo.Cliente;
import java.math.BigDecimal;

public record ClienteMoroso(Cliente cliente, int prestamosEnMora, BigDecimal saldoEnMora) {
}
