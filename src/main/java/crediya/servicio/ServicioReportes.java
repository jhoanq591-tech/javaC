package crediya.servicio;

import crediya.modelo.Cliente;
import crediya.modelo.Prestamo;
import crediya.repositorio.RepositorioClientes;
import crediya.repositorio.RepositorioPrestamos;
import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ServicioReportes {

    private final RepositorioPrestamos prestamos;
    private final RepositorioClientes clientes;
    private final Clock reloj;

    public ServicioReportes(RepositorioPrestamos prestamos, RepositorioClientes clientes, Clock reloj) {
        this.prestamos = prestamos;
        this.clientes = clientes;
        this.reloj = reloj;
    }

    public List<LineaReporte> prestamosActivos() {
        return filtrar(Prestamo::estaActivo);
    }

    public List<LineaReporte> prestamosMorosos() {
        LocalDate hoy = LocalDate.now(reloj);
        return filtrar(prestamo -> prestamo.estaEnMora(hoy));
    }

    private List<LineaReporte> filtrar(Predicate<Prestamo> criterio) {
        Map<Integer, String> nombres = clientes.listar().stream()
                .collect(Collectors.toMap(Cliente::id, Cliente::nombre));
        return prestamos.listarTodos().stream()
                .filter(criterio)
                .map(prestamo -> new LineaReporte(prestamo, nombres.getOrDefault(prestamo.clienteId(), "?")))
                .toList();
    }
}
