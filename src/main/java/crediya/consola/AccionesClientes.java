package crediya.consola;

import crediya.modelo.Cliente;
import crediya.servicio.ServicioClientes;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class AccionesClientes {

    private final EntradaConsola entrada;
    private final PrintStream salida;
    private final ServicioClientes clientes;

    public AccionesClientes(EntradaConsola entrada, PrintStream salida, ServicioClientes clientes) {
        this.entrada = entrada;
        this.salida = salida;
        this.clientes = clientes;
    }

    public void registrar() {
        String nombre = entrada.leerTexto("Nombre: ");
        String documento = entrada.leerTexto("Documento: ");
        String correo = entrada.leerTexto("Correo: ");
        String telefono = entrada.leerTexto("Telefono: ");
        BigDecimal ingresos = entrada.leerDecimal("Ingresos mensuales: ");
        char[] clave = entrada.leerClave("Clave: ");
        try {
            clientes.registrar(nombre, documento, correo, telefono, clave, ingresos);
        } finally {
            Arrays.fill(clave, ' ');
        }
        salida.println("Cliente registrado correctamente.");
    }

    public void listar() {
        List<Cliente> lista = clientes.listar();
        salida.println("--- LISTA DE CLIENTES ---");
        if (lista.isEmpty()) {
            salida.println("(sin clientes)");
        }
        lista.forEach(c -> salida.println(c.descripcion()));
    }
}
