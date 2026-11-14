package crediya.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Pago(int id, int prestamoId, LocalDate fecha, BigDecimal monto) {
}
