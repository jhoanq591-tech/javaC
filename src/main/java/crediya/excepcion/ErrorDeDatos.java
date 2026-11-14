package crediya.excepcion;

public class ErrorDeDatos extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public ErrorDeDatos(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
