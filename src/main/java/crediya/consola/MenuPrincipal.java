package crediya.consola;

import crediya.excepcion.ErrorDeNegocio;
import crediya.modelo.Credencial;
import crediya.servicio.LimitadorIntentos;
import crediya.servicio.ServicioAutenticacion;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.IntConsumer;

public class MenuPrincipal {

    private final EntradaConsola entrada;
    private final PrintStream salida;
    private final ServicioAutenticacion autenticacion;
    private final LimitadorIntentos limitador;
    private final MenuCliente menuCliente;
    private final MenuAdministrador menuAdministrador;

    public MenuPrincipal(EntradaConsola entrada, PrintStream salida, ServicioAutenticacion autenticacion,
                         LimitadorIntentos limitador, MenuCliente menuCliente,
                         MenuAdministrador menuAdministrador) {
        this.entrada = entrada;
        this.salida = salida;
        this.autenticacion = autenticacion;
        this.limitador = limitador;
        this.menuCliente = menuCliente;
        this.menuAdministrador = menuAdministrador;
    }

    public void ejecutar() {
        new Menu("===== CREDIYA S.A.S. =====", "Salir", entrada, salida)
                .agregar("Ingresar como cliente", () -> ingresar(autenticacion::ingresarCliente, menuCliente::ejecutar))
                .agregar("Ingresar como administrador",
                        () -> ingresar(autenticacion::ingresarAdministrador, menuAdministrador::ejecutar))
                .ejecutar();
        salida.println("Hasta pronto.");
    }

    private void ingresar(BiFunction<String, char[], Optional<Credencial>> acceso,
                          IntConsumer menu) {
        limitador.verificarDisponible();
        String documento = entrada.leerTexto("Documento: ");
        char[] clave = entrada.leerClave("Clave: ");
        Optional<Credencial> credencial;
        try {
            credencial = acceso.apply(documento.trim(), clave);
        } finally {
            Arrays.fill(clave, ' ');
        }
        if (credencial.isEmpty()) {
            limitador.registrarFallo();
            throw new ErrorDeNegocio("Documento o clave incorrectos.");
        }
        limitador.registrarExito();
        salida.println("Bienvenido, " + credencial.get().nombre() + ".");
        menu.accept(credencial.get().id());
    }
}
