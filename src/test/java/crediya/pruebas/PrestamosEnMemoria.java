package crediya.pruebas;

import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioPrestamos;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.UnaryOperator;

public class PrestamosEnMemoria implements RepositorioPrestamos {

    public final List<Prestamo> datos = new ArrayList<>();
    public RuntimeException falla;

    public PrestamosEnMemoria(Prestamo... iniciales) {
        datos.addAll(List.of(iniciales));
    }

    @Override
    public List<Prestamo> listarTodos() {
        if (falla != null) {
            throw falla;
        }
        return List.copyOf(datos);
    }

    @Override
    public List<Prestamo> listarPorCliente(int clienteId) {
        return datos.stream().filter(p -> p.clienteId() == clienteId).toList();
    }

    @Override
    public Optional<Prestamo> buscarPorId(int id) {
        return datos.stream().filter(p -> p.id() == id).findFirst();
    }

    @Override
    public int crear(Prestamo prestamo) {
        int id = datos.size() + 100;
        datos.add(prestamo.conId(id));
        return id;
    }

    @Override
    public void aprobar(int id, int empleadoId, LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
        reemplazar(id, p -> new Prestamo(id, p.clienteId(), empleadoId, p.monto(), p.interes(), p.cuotas(),
                fechaPrestamo, fechaVencimiento, EstadoPrestamo.ACTIVO, p.pagado()));
    }

    @Override
    public void cambiarEstado(int id, EstadoPrestamo estado, int empleadoId) {
        reemplazar(id, p -> new Prestamo(id, p.clienteId(), empleadoId, p.monto(), p.interes(), p.cuotas(),
                p.fechaPrestamo(), p.fechaVencimiento(), estado, p.pagado()));
    }

    private void reemplazar(int id, UnaryOperator<Prestamo> cambio) {
        for (int i = 0; i < datos.size(); i++) {
            if (datos.get(i).id() == id) {
                datos.set(i, cambio.apply(datos.get(i)));
            }
        }
    }
}
