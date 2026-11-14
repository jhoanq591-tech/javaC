package crediya.repositorio.archivo;

import crediya.archivo.Sincronizador;
import crediya.modelo.Pago;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioPagos;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class RepositorioPagosConArchivo implements RepositorioPagos {

    private final RepositorioPagos base;
    private final Sincronizador<Prestamo> respaldoPrestamos;

    public RepositorioPagosConArchivo(RepositorioPagos base, Sincronizador<Prestamo> respaldoPrestamos) {
        this.base = base;
        this.respaldoPrestamos = respaldoPrestamos;
    }

    @Override
    public List<Pago> listarTodos() {
        return base.listarTodos();
    }

    @Override
    public List<Pago> listarPorPrestamo(int prestamoId) {
        return base.listarPorPrestamo(prestamoId);
    }

    @Override
    public void registrarAbono(int prestamoId, BigDecimal monto, LocalDate fecha, boolean saldaPrestamo) {
        base.registrarAbono(prestamoId, monto, fecha, saldaPrestamo);
        respaldoPrestamos.sincronizar();
    }
}
