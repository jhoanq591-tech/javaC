package crediya.consola;

import java.io.PrintStream;

public class MenuAdministrador {

    private static final String VOLVER = "Volver";

    private final EntradaConsola entrada;
    private final PrintStream salida;
    private final AccionesEmpleados empleados;
    private final AccionesClientes clientes;
    private final AccionesPrestamos prestamos;
    private final AccionesPagos pagos;
    private final AccionesReportes reportes;

    public MenuAdministrador(EntradaConsola entrada, PrintStream salida, AccionesEmpleados empleados,
                             AccionesClientes clientes, AccionesPrestamos prestamos,
                             AccionesPagos pagos, AccionesReportes reportes) {
        this.entrada = entrada;
        this.salida = salida;
        this.empleados = empleados;
        this.clientes = clientes;
        this.prestamos = prestamos;
        this.pagos = pagos;
        this.reportes = reportes;
    }

    public void ejecutar(int administradorId) {
        new Menu("----- MENU ADMINISTRADOR -----", "Cerrar sesion", entrada, salida)
                .agregar("Empleados", menuEmpleados()::ejecutar)
                .agregar("Clientes", menuClientes()::ejecutar)
                .agregar("Prestamos", menuPrestamos(administradorId)::ejecutar)
                .agregar("Pagos", menuPagos()::ejecutar)
                .agregar("Reportes", menuReportes()::ejecutar)
                .ejecutar();
        salida.println("Sesion cerrada.");
    }

    private Menu menuEmpleados() {
        return new Menu("-- EMPLEADOS --", VOLVER, entrada, salida)
                .agregar("Registrar", empleados::registrar)
                .agregar("Listar", empleados::listar);
    }

    private Menu menuClientes() {
        return new Menu("-- CLIENTES --", VOLVER, entrada, salida)
                .agregar("Registrar", clientes::registrar)
                .agregar("Listar", clientes::listar);
    }

    private Menu menuPrestamos(int administradorId) {
        return new Menu("-- PRESTAMOS --", VOLVER, entrada, salida)
                .agregar("Ver prestamos de todos los clientes (por cliente)", prestamos::verTodosPorCliente)
                .agregar("Ver prestamos de un cliente", prestamos::verDeUnCliente)
                .agregar("Aprobar / cancelar / dejar pendiente un prestamo", () -> prestamos.decidir(administradorId))
                .agregar("Crear un prestamo para un cliente", prestamos::crearParaCliente);
    }

    private Menu menuPagos() {
        return new Menu("-- PAGOS --", VOLVER, entrada, salida)
                .agregar("Registrar abono", pagos::registrarAbono)
                .agregar("Listar pagos", pagos::listar);
    }

    private Menu menuReportes() {
        return new Menu("-- REPORTES --", VOLVER, entrada, salida)
                .agregar("Prestamos activos", reportes::prestamosActivos)
                .agregar("Prestamos morosos", reportes::prestamosMorosos)
                .agregar("Gestor de prestamos (activos, vencidos y resumen)", reportes::gestorDePrestamos);
    }
}
