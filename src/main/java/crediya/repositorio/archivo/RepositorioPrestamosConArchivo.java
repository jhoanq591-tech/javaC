package crediya.repositorio.archivo;

import crediya.archivo.Sincronizador;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioPrestamos;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RepositorioPrestamosConArchivo implements RepositorioPrestamos {

    private final RepositorioPrestamos base;
    private final Sincronizador<Prestamo> respaldo;

    public RepositorioPrestamosConArchivo(RepositorioPrestamos base, Sincronizador<Prestamo> respaldo) {
        this.base = base;
        this.respaldo = respaldo;
    }

    @Override
    public List<Prestamo> listarTodos() {
        return base.listarTodos();
    }

    @Override
    public List<Prestamo> listarPorCliente(int clienteId) {
        return base.listarPorCliente(clienteId);
    }

    @Override
    public Optional<Prestamo> buscarPorId(int id) {
        return base.buscarPorId(id);
    }

    @Override
    public int crear(Prestamo prestamo) {
        int id = base.crear(prestamo);
        respaldo.sincronizar();
        return id;
    }

    @Override
    public void aprobar(int id, int empleadoId, LocalDate fechaPrestamo, LocalDate fechaVencimiento) {
        base.aprobar(id, empleadoId, fechaPrestamo, fechaVencimiento);
        respaldo.sincronizar();
    }

    @Override
    public void cambiarEstado(int id, EstadoPrestamo estado, int empleadoId) {
        base.cambiarEstado(id, estado, empleadoId);
        respaldo.sincronizar();
    }
}
