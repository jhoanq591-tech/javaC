package crediya.consola;

import java.io.PrintStream;

public class MenuCliente {

    private final EntradaConsola entrada;
    private final PrintStream salida;
    private final AccionesPrestamos prestamos;

    public MenuCliente(EntradaConsola entrada, PrintStream salida, AccionesPrestamos prestamos) {
        this.entrada = entrada;
        this.salida = salida;
        this.prestamos = prestamos;
    }

    public void ejecutar(int clienteId) {
        new Menu("----- MENU CLIENTE -----", "Cerrar sesion", entrada, salida)
                .agregar("Solicitar un prestamo", () -> prestamos.solicitar(clienteId))
                .agregar("Ver mis prestamos", () -> prestamos.mostrarDe(clienteId))
                .ejecutar();
        salida.println("Sesion cerrada.");
    }
}
