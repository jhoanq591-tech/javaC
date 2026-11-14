package crediya.archivo;

import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

public class AlmacenCsvPrestamos extends AlmacenCsv<Prestamo> {

    public AlmacenCsvPrestamos(Path ruta) {
        super(ruta);
    }

    @Override
    protected String encabezado() {
        return "id,cliente_id,empleado_id,monto,interes,cuotas,fecha_prestamo,fecha_vencimiento,estado,pagado";
    }

    @Override
    protected String aLinea(Prestamo p) {
        return Csv.linea(List.of(
                String.valueOf(p.id()),
                String.valueOf(p.clienteId()),
                p.empleadoId() == null ? "" : String.valueOf(p.empleadoId()),
                p.monto().toPlainString(),
                p.interes().toPlainString(),
                String.valueOf(p.cuotas()),
                String.valueOf(p.fechaPrestamo()),
                String.valueOf(p.fechaVencimiento()),
                p.estado().name(),
                p.pagado().toPlainString()));
    }

    @Override
    protected Prestamo deCampos(List<String> c) {
        return new Prestamo(
                Integer.parseInt(c.get(0)),
                Integer.parseInt(c.get(1)),
                c.get(2).isEmpty() ? null : Integer.valueOf(c.get(2)),
                new BigDecimal(c.get(3)),
                new BigDecimal(c.get(4)),
                Integer.parseInt(c.get(5)),
                LocalDate.parse(c.get(6)),
                LocalDate.parse(c.get(7)),
                EstadoPrestamo.valueOf(c.get(8)),
                new BigDecimal(c.get(9)));
    }
}
