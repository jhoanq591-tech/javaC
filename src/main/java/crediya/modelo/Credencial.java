package crediya.modelo;

public record Credencial(int id, String nombre, String claveHash) {

    @Override
    public String toString() {
        return "Credencial[id=" + id + ", nombre=" + nombre + "]";
    }
}
