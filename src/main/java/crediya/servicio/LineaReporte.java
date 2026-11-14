package crediya.servicio;

import crediya.modelo.Prestamo;

public record LineaReporte(Prestamo prestamo, String nombreCliente) {
}
