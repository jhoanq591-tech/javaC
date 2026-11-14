package crediya.servicio;

import crediya.excepcion.ErrorDeNegocio;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public class LimitadorIntentos {

    private final int maximoIntentos;
    private final Duration bloqueo;
    private final Clock reloj;
    private int fallos;
    private Instant bloqueadoHasta = Instant.MIN;

    public LimitadorIntentos(int maximoIntentos, Duration bloqueo, Clock reloj) {
        this.maximoIntentos = maximoIntentos;
        this.bloqueo = bloqueo;
        this.reloj = reloj;
    }

    public void verificarDisponible() {
        Instant ahora = reloj.instant();
        if (ahora.isBefore(bloqueadoHasta)) {
            long segundos = Duration.between(ahora, bloqueadoHasta).toSeconds() + 1;
            throw new ErrorDeNegocio("Demasiados intentos fallidos. Intente de nuevo en " + segundos + " segundos.");
        }
    }

    public void registrarFallo() {
        fallos++;
        if (fallos >= maximoIntentos) {
            bloqueadoHasta = reloj.instant().plus(bloqueo);
            fallos = 0;
        }
    }

    public void registrarExito() {
        fallos = 0;
    }
}
