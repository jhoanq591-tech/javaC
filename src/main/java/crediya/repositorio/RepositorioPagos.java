package crediya.repositorio;

import crediya.modelo.Pago;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface RepositorioPagos {

    List<Pago> listarTodos();

    List<Pago> listarPorPrestamo(int prestamoId);

    void registrarAbono(int prestamoId, BigDecimal monto, LocalDate fecha, boolean saldaPrestamo);
}
