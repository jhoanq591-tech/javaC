package crediya.archivo;

import java.util.List;
import java.util.function.Supplier;

public class Sincronizador<T> {

    private final Supplier<List<T>> origen;
    private final AlmacenArchivo<T> destino;

    public Sincronizador(Supplier<List<T>> origen, AlmacenArchivo<T> destino) {
        this.origen = origen;
        this.destino = destino;
    }

    public void sincronizar() {
        destino.guardar(origen.get());
    }

    public List<T> leerArchivo() {
        return destino.leer();
    }

    public String ubicacion() {
        return destino.ubicacion();
    }
}
