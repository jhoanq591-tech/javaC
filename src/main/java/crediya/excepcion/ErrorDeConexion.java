package crediya.excepcion;

public class ErrorDeConexion extends ErrorDeDatos {

    private static final long serialVersionUID = 1L;

    private static final String MENSAJE = "No se pudo conectar a la base de datos. "
            + "Verifique que MySQL este encendido y que la configuracion de conexion sea correcta.";

    public ErrorDeConexion(Throwable causa) {
        super(MENSAJE, causa);
    }
}
