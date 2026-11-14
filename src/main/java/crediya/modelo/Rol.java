package crediya.modelo;

import crediya.excepcion.ErrorDeNegocio;
import java.util.Locale;

public enum Rol {
    ADMINISTRADOR,
    ASESOR,
    COBRADOR;

    public static Rol desde(String texto) {
        try {
            return valueOf(texto.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            throw new ErrorDeNegocio("El rol debe ser Administrador, Asesor o Cobrador.");
        }
    }
}
