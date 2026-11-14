package crediya;

import crediya.archivo.AlmacenCsvEmpleados;
import crediya.archivo.AlmacenCsvPrestamos;
import crediya.archivo.Sincronizador;
import crediya.configuracion.ConexionMySql;
import crediya.configuracion.ConfiguracionBaseDatos;
import crediya.configuracion.ProveedorConexion;
import crediya.consola.AccionesArchivos;
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
import crediya.modelo.Empleado;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioClientes;
import crediya.repositorio.RepositorioCredenciales;
import crediya.repositorio.RepositorioEmpleados;
import crediya.repositorio.RepositorioPagos;
import crediya.repositorio.RepositorioPrestamos;
import crediya.repositorio.archivo.RepositorioEmpleadosConArchivo;
import crediya.repositorio.archivo.RepositorioPagosConArchivo;
import crediya.repositorio.archivo.RepositorioPrestamosConArchivo;
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
import crediya.servicio.ServicioDirectorio;
import crediya.servicio.ServicioEmpleados;
import crediya.servicio.ServicioPagos;
import crediya.servicio.ServicioPrestamos;
import crediya.servicio.ServicioReportes;
import java.io.PrintStream;
import java.nio.file.Path;
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

    private static String valorEntorno(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? porDefecto : valor;
    }

    private static MenuPrincipal construir(Scanner lector, PrintStream salida) {
        Clock reloj = Clock.systemDefaultZone();
        ProveedorConexion conexiones = new ConexionMySql(ConfiguracionBaseDatos.desdeEntorno());
        HasheadorClaves hasheador = new HasheadorPbkdf2();

        Path directorioDatos = Path.of(valorEntorno("CREDIYA_DATOS", "datos"));
        RepositorioPrestamos prestamosBd = new RepositorioPrestamosJdbc(conexiones);
        RepositorioEmpleados empleadosBd = new RepositorioEmpleadosJdbc(conexiones);
        Sincronizador<Prestamo> respaldoPrestamos = new Sincronizador<>(prestamosBd::listarTodos,
                new AlmacenCsvPrestamos(directorioDatos.resolve("prestamos.csv")));
        Sincronizador<Empleado> respaldoEmpleados = new Sincronizador<>(empleadosBd::listar,
                new AlmacenCsvEmpleados(directorioDatos.resolve("empleados.csv")));

        RepositorioPrestamos prestamos = new RepositorioPrestamosConArchivo(prestamosBd, respaldoPrestamos);
        RepositorioEmpleados empleados = new RepositorioEmpleadosConArchivo(empleadosBd, respaldoEmpleados);
        RepositorioPagos pagos = new RepositorioPagosConArchivo(new RepositorioPagosJdbc(conexiones), respaldoPrestamos);
        RepositorioClientes clientes = new RepositorioClientesJdbc(conexiones);
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
                new AccionesReportes(salida, servicioReportes, gestor, new ServicioDirectorio(clientes, empleados)),
                new AccionesArchivos(salida, respaldoEmpleados, respaldoPrestamos));

        return new MenuPrincipal(entrada, salida, autenticacion,
                new LimitadorIntentos(MAXIMO_INTENTOS, BLOQUEO, reloj), menuCliente, menuAdministrador);
    }
}
