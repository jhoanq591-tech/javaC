package crediya.servicio;

import crediya.excepcion.ErrorDeNegocio;
import crediya.modelo.EstadoPrestamo;
import crediya.modelo.Pago;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioPagos;
import crediya.repositorio.RepositorioPrestamos;
import crediya.util.Formato;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;

public class ServicioPagos {

    private final RepositorioPagos pagos;
    private final RepositorioPrestamos prestamos;
    private final Clock reloj;

    public ServicioPagos(RepositorioPagos pagos, RepositorioPrestamos prestamos, Clock reloj) {
        this.pagos = pagos;
        this.prestamos = prestamos;
        this.reloj = reloj;
    }

    public BigDecimal registrarAbono(int prestamoId, BigDecimal abono) {
        Validaciones.montoPositivo(abono, "El abono");
        BigDecimal valor = abono.setScale(2, RoundingMode.HALF_UP);
        Prestamo prestamo = prestamos.buscarPorId(prestamoId)
                .orElseThrow(() -> new ErrorDeNegocio("No existe un prestamo con ese ID."));
        validarAbonable(prestamo);
        BigDecimal saldo = prestamo.saldo();
        if (valor.compareTo(saldo) > 0) {
            throw new ErrorDeNegocio("El abono supera el saldo pendiente (" + Formato.dinero(saldo) + ").");
        }
        BigDecimal nuevoSaldo = saldo.subtract(valor);
        pagos.registrarAbono(prestamoId, valor, LocalDate.now(reloj), nuevoSaldo.signum() == 0);
        return nuevoSaldo;
    }

    public List<Pago> listar(int prestamoId) {
        return prestamoId > 0 ? pagos.listarPorPrestamo(prestamoId) : pagos.listarTodos();
    }

    private void validarAbonable(Prestamo prestamo) {
        if (prestamo.estado() == EstadoPrestamo.PAGADO) {
            throw new ErrorDeNegocio("Ese prestamo ya esta pagado.");
        }
        if (!prestamo.estaActivo()) {
            throw new ErrorDeNegocio("Solo se pueden abonar prestamos ACTIVOS (este esta "
                    + prestamo.estado() + ").");
        }
    }
}
