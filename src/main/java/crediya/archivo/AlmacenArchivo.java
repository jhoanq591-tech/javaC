package crediya.archivo;

import java.util.List;

public interface AlmacenArchivo<T> {

    void guardar(List<T> elementos);

    List<T> leer();

    String ubicacion();
}
