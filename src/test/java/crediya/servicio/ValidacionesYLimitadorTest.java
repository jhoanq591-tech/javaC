package crediya.servicio;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import crediya.excepcion.ErrorDeNegocio;
import crediya.pruebas.Datos;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class ValidacionesYLimitadorTest {

    @Test
    void validaDocumentoCorreoTelefonoYClave() {
        assertDoesNotThrow(() -> Validaciones.documento("2001"));
        assertDoesNotThrow(() -> Validaciones.correo("ana@ejemplo.com"));
        assertDoesNotThrow(() -> Validaciones.telefono("3001112222"));
        assertDoesNotThrow(() -> Validaciones.clave("12345678".toCharArray()));
        assertThrows(ErrorDeNegocio.class, () -> Validaciones.documento("1; DROP TABLE"));
        assertThrows(ErrorDeNegocio.class, () -> Validaciones.correo("sin-arroba"));
        assertThrows(ErrorDeNegocio.class, () -> Validaciones.telefono("abc"));
        assertThrows(ErrorDeNegocio.class, () -> Validaciones.clave("corta".toCharArray()));
        assertThrows(ErrorDeNegocio.class, () -> Validaciones.nombre("   "));
    }

    @Test
    void bloqueaTrasDemasiadosFallosYSeLiberaConElTiempo() {
        MutableClock reloj = new MutableClock(Instant.parse("2026-11-13T12:00:00Z"));
        LimitadorIntentos limitador = new LimitadorIntentos(3, Duration.ofMinutes(1), reloj);
        for (int i = 0; i < 3; i++) {
            limitador.verificarDisponible();
            limitador.registrarFallo();
        }
        assertThrows(ErrorDeNegocio.class, limitador::verificarDisponible);
        reloj.avanzar(Duration.ofSeconds(61));
        assertDoesNotThrow(limitador::verificarDisponible);
    }

    @Test
    void unExitoReiniciaElContadorDeFallos() {
        LimitadorIntentos limitador = new LimitadorIntentos(2, Duration.ofMinutes(1), Datos.reloj());
        limitador.registrarFallo();
        limitador.registrarExito();
        limitador.registrarFallo();
        assertDoesNotThrow(limitador::verificarDisponible);
    }

    private static final class MutableClock extends Clock {

        private Instant actual;

        MutableClock(Instant inicio) {
            this.actual = inicio;
        }

        void avanzar(Duration duracion) {
            actual = actual.plus(duracion);
        }

        @Override
        public ZoneOffset getZone() {
            return ZoneOffset.UTC;
        }

        @Override
        public Clock withZone(java.time.ZoneId zona) {
            return this;
        }

        @Override
        public Instant instant() {
            return actual;
        }
    }
}
