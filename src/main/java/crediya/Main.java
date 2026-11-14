package crediya;

import crediya.configuracion.ConexionMySql;
import crediya.configuracion.ConfiguracionBaseDatos;
import crediya.configuracion.ProveedorConexion;
import crediya.consola.AccionesClientes;
import crediya.consola.AccionesEmpleados;
import crediya.consola.AccionesPagos;
import crediya.consola.AccionesPrestamos;
import crediya.consola.AccionesReportes;
import crediya.consola.EntradaConsola;
import crediya.consola.MenuAdministrador;
import crediya.consola.MenuCliente;
import crediya.consola.MenuPrincipal;
import crediya.excepcion.EntradaCerrada;
import crediya.repositorio.RepositorioClientes;
import crediya.repositorio.RepositorioCredenciales;
import crediya.repositorio.RepositorioEmpleados;
import crediya.repositorio.RepositorioPagos;
import crediya.repositorio.RepositorioPrestamos;
import crediya.repositorio.jdbc.RepositorioClientesJdbc;
import crediya.repositorio.jdbc.RepositorioCredencialesJdbc;
import crediya.repositorio.jdbc.RepositorioEmpleadosJdbc;
import crediya.repositorio.jdbc.RepositorioPagosJdbc;
import crediya.repositorio.jdbc.RepositorioPrestamosJdbc;
import crediya.seguridad.HasheadorClaves;
import crediya.seguridad.HasheadorPbkdf2;
import crediya.servicio.GestorPrestamos;
import crediya.servicio.LimitadorIntentos;
import crediya.servicio.ServicioAutenticacion;
import crediya.servicio.ServicioClientes;
import crediya.servicio.ServicioEmpleados;
import crediya.servicio.ServicioPagos;
import crediya.servicio.ServicioPrestamos;
import crediya.servicio.ServicioReportes;
import java.io.PrintStream;
import java.time.Clock;
import java.time.Duration;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class Main {

    private static final Logger REGISTRO = Logger.getLogger(Main.class.getName());
    private static final int MAXIMO_INTENTOS = 5;
    private static final Duration BLOQUEO = Duration.ofMinutes(1);

    private Main() {
    }

    public static void main(String[] args) {
        PrintStream salida = System.out;
        try (Scanner lector = new Scanner(System.in)) {
            construir(lector, salida).ejecutar();
        } catch (EntradaCerrada e) {
            salida.println("\nEntrada cerrada. Hasta pronto.");
        } catch (RuntimeException e) {
            REGISTRO.log(Level.SEVERE, "Error inesperado", e);
            salida.println("Ocurrio un error inesperado. La aplicacion se cerrara.");
        }
    }

    private static MenuPrincipal construir(Scanner lector, PrintStream salida) {
        Clock reloj = Clock.systemDefaultZone();
        ProveedorConexion conexiones = new ConexionMySql(ConfiguracionBaseDatos.desdeEntorno());
        HasheadorClaves hasheador = new HasheadorPbkdf2();

        RepositorioPrestamos prestamos = new RepositorioPrestamosJdbc(conexiones);
        RepositorioClientes clientes = new RepositorioClientesJdbc(conexiones);
        RepositorioEmpleados empleados = new RepositorioEmpleadosJdbc(conexiones);
        RepositorioPagos pagos = new RepositorioPagosJdbc(conexiones);
        RepositorioCredenciales credenciales = new RepositorioCredencialesJdbc(conexiones);

        ServicioClientes servicioClientes = new ServicioClientes(clientes, hasheador);
        ServicioEmpleados servicioEmpleados = new ServicioEmpleados(empleados, hasheador);
        ServicioPrestamos servicioPrestamos = new ServicioPrestamos(prestamos, clientes, reloj);
        ServicioPagos servicioPagos = new ServicioPagos(pagos, prestamos, reloj);
        ServicioReportes servicioReportes = new ServicioReportes(prestamos, clientes, reloj);
        ServicioAutenticacion autenticacion = new ServicioAutenticacion(credenciales, hasheador);
        GestorPrestamos gestor = new GestorPrestamos(prestamos, reloj, salida);

        EntradaConsola entrada = new EntradaConsola(lector, System.console(), salida);
        AccionesPrestamos accionesPrestamos = new AccionesPrestamos(entrada, salida, servicioPrestamos, servicioClientes);
        MenuCliente menuCliente = new MenuCliente(entrada, salida, accionesPrestamos);
        MenuAdministrador menuAdministrador = new MenuAdministrador(entrada, salida,
                new AccionesEmpleados(entrada, salida, servicioEmpleados),
                new AccionesClientes(entrada, salida, servicioClientes),
                accionesPrestamos,
                new AccionesPagos(entrada, salida, servicioPagos),
                new AccionesReportes(salida, servicioReportes, gestor));

        return new MenuPrincipal(entrada, salida, autenticacion,
                new LimitadorIntentos(MAXIMO_INTENTOS, BLOQUEO, reloj), menuCliente, menuAdministrador);
    }
}
