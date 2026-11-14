package crediya.servicio;

import crediya.excepcion.ErrorDeNegocio;
import java.math.BigDecimal;
import java.util.regex.Pattern;

public final class Validaciones {

    private static final Pattern DOCUMENTO = Pattern.compile("^[A-Za-z0-9]{4,30}$");
    private static final Pattern CORREO = Pattern.compile("^[\\w.+-]+@[\\w-]+(\\.[\\w-]+)+$");
    private static final Pattern TELEFONO = Pattern.compile("^[0-9]{7,15}$");
    public static final int LONGITUD_MINIMA_CLAVE = 8;
    public static final int LONGITUD_MAXIMA_CLAVE = 64;
    private static final int LONGITUD_MAXIMA_NOMBRE = 80;
    private static final int LONGITUD_MAXIMA_CORREO = 80;

    private Validaciones() {
    }

    public static String nombre(String valor) {
        return textoObligatorio(valor, "El nombre", LONGITUD_MAXIMA_NOMBRE);
    }

    public static String documento(String valor) {
        String limpio = textoObligatorio(valor, "El documento", 30);
        if (!DOCUMENTO.matcher(limpio).matches()) {
            throw new ErrorDeNegocio("El documento debe tener entre 4 y 30 letras o numeros.");
        }
        return limpio;
    }

    public static String correo(String valor) {
        String limpio = textoObligatorio(valor, "El correo", LONGITUD_MAXIMA_CORREO);
        if (!CORREO.matcher(limpio).matches()) {
            throw new ErrorDeNegocio("El correo no tiene un formato valido.");
        }
        return limpio;
    }

    public static String telefono(String valor) {
        String limpio = textoObligatorio(valor, "El telefono", 15);
        if (!TELEFONO.matcher(limpio).matches()) {
            throw new ErrorDeNegocio("El telefono debe tener entre 7 y 15 digitos.");
        }
        return limpio;
    }

    public static BigDecimal montoPositivo(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new ErrorDeNegocio(campo + " debe ser mayor a cero.");
        }
        return valor;
    }

    public static char[] clave(char[] valor) {
        if (valor == null || valor.length < LONGITUD_MINIMA_CLAVE || valor.length > LONGITUD_MAXIMA_CLAVE) {
            throw new ErrorDeNegocio("La clave debe tener entre " + LONGITUD_MINIMA_CLAVE
                    + " y " + LONGITUD_MAXIMA_CLAVE + " caracteres.");
        }
        return valor;
    }

    private static String textoObligatorio(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new ErrorDeNegocio(campo + " no puede estar vacio.");
        }
        String limpio = valor.trim();
        if (limpio.length() > maximo) {
            throw new ErrorDeNegocio(campo + " no puede superar " + maximo + " caracteres.");
        }
        return limpio;
    }
}
