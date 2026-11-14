package crediya.pruebas;

import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;

public final class Datos {

    public static final LocalDate HOY = LocalDate.of(2026, 11, 13);

    private Datos() {
    }

    public static Clock reloj() {
        return Clock.fixed(Instant.parse("2026-11-13T12:00:00Z"), ZoneOffset.UTC);
    }

    public static Prestamo prestamo(int id, EstadoPrestamo estado, LocalDate prestado, LocalDate vence,
                                    String monto, String pagado) {
        return new Prestamo(id, 1, new BigDecimal(monto), new BigDecimal("5.00"), 10,
                prestado, vence, estado, new BigDecimal(pagado));
    }
}
