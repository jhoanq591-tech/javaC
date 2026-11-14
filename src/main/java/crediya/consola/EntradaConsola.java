package crediya.consola;

import crediya.excepcion.EntradaCerrada;
import crediya.excepcion.ErrorDeNegocio;
import java.io.Console;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class EntradaConsola {

    private final Scanner lector;
    private final Console consola;
    private final PrintStream salida;

    public EntradaConsola(Scanner lector, Console consola, PrintStream salida) {
        this.lector = lector;
        this.consola = consola;
        this.salida = salida;
    }

    public String leerTexto(String mensaje) {
        if (consola != null) {
            String linea = consola.readLine("%s", mensaje);
            if (linea == null) {
                throw new EntradaCerrada();
            }
            return linea;
        }
        salida.print(mensaje);
        try {
            return lector.nextLine();
        } catch (NoSuchElementException e) {
            throw new EntradaCerrada();
        }
    }

    public int leerEntero(String mensaje) {
        try {
            return Integer.parseInt(leerTexto(mensaje).trim());
        } catch (NumberFormatException e) {
            throw new ErrorDeNegocio("Ingrese un numero entero valido.");
        }
    }

    public BigDecimal leerDecimal(String mensaje) {
        try {
            return new BigDecimal(leerTexto(mensaje).trim());
        } catch (NumberFormatException e) {
            throw new ErrorDeNegocio("Ingrese un valor numerico valido.");
        }
    }

    public char[] leerClave(String mensaje) {
        if (consola != null) {
            char[] clave = consola.readPassword("%s", mensaje);
            if (clave == null) {
                throw new EntradaCerrada();
            }
            return clave;
        }
        return leerTexto(mensaje).toCharArray();
    }
}
