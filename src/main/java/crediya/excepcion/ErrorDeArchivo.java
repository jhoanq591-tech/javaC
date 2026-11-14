package crediya.excepcion;

public class ErrorDeArchivo extends ErrorDeDatos {

    private static final long serialVersionUID = 1L;

    public ErrorDeArchivo(Throwable causa) {
        super("No se pudo leer o escribir el archivo de respaldo.", causa);
    }
}
