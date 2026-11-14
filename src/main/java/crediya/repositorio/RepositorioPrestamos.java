package crediya.repositorio;

import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RepositorioPrestamos {

    List<Prestamo> listarTodos();

    List<Prestamo> listarPorCliente(int clienteId);

    Optional<Prestamo> buscarPorId(int id);

    int crear(Prestamo prestamo);

    void aprobar(int id, int empleadoId, LocalDate fechaPrestamo, LocalDate fechaVencimiento);

    void cambiarEstado(int id, EstadoPrestamo estado, int empleadoId);
}
