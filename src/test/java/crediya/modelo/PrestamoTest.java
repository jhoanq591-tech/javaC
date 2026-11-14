package crediya.modelo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import crediya.pruebas.Datos;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class PrestamoTest {

    @Test
    void calculaMontoTotalCuotaYSaldo() {
        Prestamo p = Datos.prestamo(1, EstadoPrestamo.ACTIVO, Datos.HOY, Datos.HOY.plusMonths(10),
                "1000000.00", "50000.00");
        assertEquals(new BigDecimal("1050000.00"), p.montoTotal());
        assertEquals(new BigDecimal("105000.00"), p.valorCuota());
        assertEquals(new BigDecimal("1000000.00"), p.saldo());
    }

    @Test
    void elSaldoNuncaEsNegativo() {
        Prestamo p = Datos.prestamo(1, EstadoPrestamo.ACTIVO, Datos.HOY, Datos.HOY, "100.00", "999.00");
        assertEquals(0, p.saldo().signum());
    }

    @Test
    void vencidoSoloSiLaFechaYaPaso() {
        Prestamo ayer = Datos.prestamo(1, EstadoPrestamo.ACTIVO, Datos.HOY.minusDays(40), Datos.HOY.minusDays(1), "1.00", "0");
        Prestamo hoy = Datos.prestamo(2, EstadoPrestamo.ACTIVO, Datos.HOY.minusDays(40), Datos.HOY, "1.00", "0");
        assertTrue(ayer.estaVencido(Datos.HOY));
        assertFalse(hoy.estaVencido(Datos.HOY));
    }

    @Test
    void moraSoloEnActivosConMasDeTreintaDias() {
        Prestamo mora = Datos.prestamo(1, EstadoPrestamo.ACTIVO, Datos.HOY.minusDays(31), Datos.HOY.plusDays(5), "1.00", "0");
        Prestamo limite = Datos.prestamo(2, EstadoPrestamo.ACTIVO, Datos.HOY.minusDays(30), Datos.HOY.plusDays(5), "1.00", "0");
        Prestamo pagado = Datos.prestamo(3, EstadoPrestamo.PAGADO, Datos.HOY.minusDays(90), Datos.HOY.plusDays(5), "1.00", "0");
        assertTrue(mora.estaEnMora(Datos.HOY));
        assertFalse(limite.estaEnMora(Datos.HOY));
        assertFalse(pagado.estaEnMora(Datos.HOY));
    }
}
