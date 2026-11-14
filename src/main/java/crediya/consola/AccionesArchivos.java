package crediya.consola;

import crediya.archivo.Sincronizador;
import crediya.modelo.Empleado;
import crediya.modelo.Prestamo;
import java.io.PrintStream;
import java.util.List;

public class AccionesArchivos {

    private final PrintStream salida;
    private final Sincronizador<Empleado> empleados;
    private final Sincronizador<Prestamo> prestamos;

    public AccionesArchivos(PrintStream salida, Sincronizador<Empleado> empleados,
                            Sincronizador<Prestamo> prestamos) {
        this.salida = salida;
        this.empleados = empleados;
        this.prestamos = prestamos;
    }

    public void sincronizar() {
        empleados.sincronizar();
        prestamos.sincronizar();
        salida.println("Archivos actualizados:");
        salida.println(" - " + empleados.ubicacion());
        salida.println(" - " + prestamos.ubicacion());
    }

    public void verEmpleados() {
        mostrar("EMPLEADOS EN ARCHIVO", empleados.ubicacion(), empleados.leerArchivo());
    }

    public void verPrestamos() {
        mostrar("PRESTAMOS EN ARCHIVO", prestamos.ubicacion(), prestamos.leerArchivo());
    }

    private <T> void mostrar(String titulo, String ubicacion, List<T> elementos) {
        salida.println("--- " + titulo + " (" + elementos.size() + ") ---");
        salida.println(ubicacion);
        if (elementos.isEmpty()) {
            salida.println("(el archivo no existe o esta vacio)");
        }
        elementos.forEach(salida::println);
    }
}
