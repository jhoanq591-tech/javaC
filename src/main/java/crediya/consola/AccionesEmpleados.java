package crediya.consola;

import crediya.modelo.Empleado;
import crediya.servicio.ServicioEmpleados;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class AccionesEmpleados {

    private final EntradaConsola entrada;
    private final PrintStream salida;
    private final ServicioEmpleados empleados;

    public AccionesEmpleados(EntradaConsola entrada, PrintStream salida, ServicioEmpleados empleados) {
        this.entrada = entrada;
        this.salida = salida;
        this.empleados = empleados;
    }

    public void registrar() {
        String nombre = entrada.leerTexto("Nombre: ");
        String documento = entrada.leerTexto("Documento: ");
        String rol = entrada.leerTexto("Rol (Administrador / Asesor / Cobrador): ");
        String correo = entrada.leerTexto("Correo: ");
        BigDecimal salario = entrada.leerDecimal("Salario: ");
        char[] clave = entrada.leerClave("Clave: ");
        try {
            empleados.registrar(nombre, documento, rol, correo, salario, clave);
        } finally {
            Arrays.fill(clave, ' ');
        }
        salida.println("Empleado registrado correctamente.");
    }

    public void listar() {
        List<Empleado> lista = empleados.listar();
        salida.println("--- LISTA DE EMPLEADOS ---");
        if (lista.isEmpty()) {
            salida.println("(sin empleados)");
        }
        lista.forEach(e -> salida.println(e.descripcion()));
    }
}
