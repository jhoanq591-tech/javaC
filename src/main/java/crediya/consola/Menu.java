package crediya.consola;

import crediya.excepcion.ErrorDeDatos;
import crediya.excepcion.ErrorDeNegocio;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class Menu {

    private record Opcion(String descripcion, Runnable accion) {
    }

    private final String titulo;
    private final String textoSalida;
    private final EntradaConsola entrada;
    private final PrintStream salida;
    private final List<Opcion> opciones = new ArrayList<>();

    public Menu(String titulo, String textoSalida, EntradaConsola entrada, PrintStream salida) {
        this.titulo = titulo;
        this.textoSalida = textoSalida;
        this.entrada = entrada;
        this.salida = salida;
    }

    public Menu agregar(String descripcion, Runnable accion) {
        opciones.add(new Opcion(descripcion, accion));
        return this;
    }

    public void ejecutar() {
        boolean continuar = true;
        while (continuar) {
            mostrar();
            continuar = procesar(entrada.leerTexto("Elija una opcion: ").trim());
        }
    }

    private void mostrar() {
        salida.println();
        salida.println(titulo);
        for (int i = 0; i < opciones.size(); i++) {
            salida.println((i + 1) + ". " + opciones.get(i).descripcion());
        }
        salida.println("0. " + textoSalida);
    }

    private boolean procesar(String texto) {
        if ("0".equals(texto)) {
            return false;
        }
        Opcion opcion = buscar(texto);
        if (opcion == null) {
            salida.println("Opcion invalida.");
            return true;
        }
        try {
            opcion.accion().run();
        } catch (ErrorDeNegocio | ErrorDeDatos e) {
            salida.println(e.getMessage());
        }
        return true;
    }

    private Opcion buscar(String texto) {
        try {
            int indice = Integer.parseInt(texto) - 1;
            return indice >= 0 && indice < opciones.size() ? opciones.get(indice) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
