package crediya.excepcion;

public class ErrorDeNegocio extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErrorDeNegocio(String mensaje) {
        super(mensaje);
    }
}
