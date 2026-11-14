package crediya.excepcion;

public class EntradaCerrada extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public EntradaCerrada() {
        super("La entrada de datos se cerro.");
    }
}
