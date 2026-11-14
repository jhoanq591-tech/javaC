package crediya.archivo;

import crediya.excepcion.ErrorDeArchivo;
import crediya.excepcion.ErrorDeNegocio;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AlmacenCsv<T> implements AlmacenArchivo<T> {

    private static final Logger REGISTRO = Logger.getLogger(AlmacenCsv.class.getName());

    private final Path ruta;

    protected AlmacenCsv(Path ruta) {
        this.ruta = ruta;
    }

    protected abstract String encabezado();

    protected abstract String aLinea(T elemento);

    protected abstract T deCampos(List<String> campos);

    @Override
    public String ubicacion() {
        return ruta.toAbsolutePath().toString();
    }

    @Override
    public void guardar(List<T> elementos) {
        List<String> lineas = new ArrayList<>();
        lineas.add(encabezado());
        elementos.forEach(elemento -> lineas.add(aLinea(elemento)));
        try {
            Path directorio = ruta.toAbsolutePath().getParent();
            Files.createDirectories(directorio);
            Path temporal = Files.createTempFile(directorio, "respaldo", ".tmp");
            try {
                Files.write(temporal, lineas, StandardCharsets.UTF_8);
                Files.move(temporal, ruta, StandardCopyOption.REPLACE_EXISTING);
            } finally {
                Files.deleteIfExists(temporal);
            }
        } catch (IOException e) {
            REGISTRO.log(Level.WARNING, "No se pudo escribir el respaldo {0}", ruta.getFileName());
            throw new ErrorDeArchivo(e);
        }
    }

    @Override
    public List<T> leer() {
        if (!Files.exists(ruta)) {
            return List.of();
        }
        try {
            List<String> lineas = Files.readAllLines(ruta, StandardCharsets.UTF_8);
            List<T> elementos = new ArrayList<>();
            for (int i = 1; i < lineas.size(); i++) {
                if (!lineas.get(i).isBlank()) {
                    elementos.add(convertir(lineas.get(i), i + 1));
                }
            }
            return elementos;
        } catch (IOException e) {
            REGISTRO.log(Level.WARNING, "No se pudo leer el respaldo {0}", ruta.getFileName());
            throw new ErrorDeArchivo(e);
        }
    }

    private T convertir(String linea, int numeroLinea) {
        try {
            return deCampos(Csv.separar(linea));
        } catch (RuntimeException e) {
            throw new ErrorDeNegocio("El archivo " + ruta.getFileName()
                    + " esta danado en la linea " + numeroLinea + ".");
        }
    }
}
