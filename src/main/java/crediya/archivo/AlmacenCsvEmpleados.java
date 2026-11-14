package crediya.archivo;

import crediya.modelo.Empleado;
import crediya.modelo.Rol;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

public class AlmacenCsvEmpleados extends AlmacenCsv<Empleado> {

    public AlmacenCsvEmpleados(Path ruta) {
        super(ruta);
    }

    @Override
    protected String encabezado() {
        return "id,nombre,documento,rol,correo,salario";
    }

    @Override
    protected String aLinea(Empleado e) {
        return String.join(",", String.valueOf(e.id()), Csv.texto(e.nombre()), Csv.texto(e.documento()),
                e.rol().name(), Csv.texto(e.correo()), e.salario().toPlainString());
    }

    @Override
    protected Empleado deCampos(List<String> campos) {
        return new Empleado(Integer.parseInt(campos.get(0)), Csv.restaurar(campos.get(1)),
                Csv.restaurar(campos.get(2)), Rol.valueOf(campos.get(3)), Csv.restaurar(campos.get(4)),
                new BigDecimal(campos.get(5)));
    }
}
