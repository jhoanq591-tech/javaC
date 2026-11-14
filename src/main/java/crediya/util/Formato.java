package crediya.util;

import java.math.BigDecimal;
import java.util.Locale;

public final class Formato {

    private static final Locale COLOMBIA = Locale.forLanguageTag("es-CO");

    private Formato() {
    }

    public static String dinero(BigDecimal valor) {
        return String.format(COLOMBIA, "$%,.2f", valor);
    }
}
