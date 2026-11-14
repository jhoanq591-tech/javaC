package crediya.archivo;

import java.util.ArrayList;
import java.util.List;

public final class Csv {

    private static final String PELIGROSOS = "=+-@\t\r";
    private static final char COMILLA = '"';
    private static final char SEPARADOR = ',';
    private static final char MARCA_PROTECCION = '\'';

    private Csv() {
    }

    public static String texto(String valor) {
        String limpio = valor.replace('\r', ' ').replace('\n', ' ');
        if (!limpio.isEmpty() && PELIGROSOS.indexOf(limpio.charAt(0)) >= 0) {
            limpio = MARCA_PROTECCION + limpio;
        }
        return COMILLA + limpio.replace("\"", "\"\"") + COMILLA;
    }

    public static String linea(List<String> campos) {
        return String.join(String.valueOf(SEPARADOR), campos);
    }

    public static List<String> separar(String linea) {
        List<String> campos = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean entreComillas = false;
        for (int i = 0; i < linea.length(); i++) {
            char c = linea.charAt(i);
            if (entreComillas) {
                if (c == COMILLA && i + 1 < linea.length() && linea.charAt(i + 1) == COMILLA) {
                    actual.append(COMILLA);
                    i++;
                } else if (c == COMILLA) {
                    entreComillas = false;
                } else {
                    actual.append(c);
                }
            } else if (c == COMILLA) {
                entreComillas = true;
            } else if (c == SEPARADOR) {
                campos.add(actual.toString());
                actual.setLength(0);
            } else {
                actual.append(c);
            }
        }
        if (entreComillas) {
            throw new IllegalArgumentException("Comillas sin cerrar.");
        }
        campos.add(actual.toString());
        return campos;
    }

    public static String restaurar(String campo) {
        if (campo.length() > 1 && campo.charAt(0) == MARCA_PROTECCION
                && PELIGROSOS.indexOf(campo.charAt(1)) >= 0) {
            return campo.substring(1);
        }
        return campo;
    }
}
