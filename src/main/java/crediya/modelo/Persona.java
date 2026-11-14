package crediya.modelo;

import java.util.Objects;

public abstract class Persona {

    private final int id;
    private final String nombre;
    private final String documento;
    private final String correo;

    protected Persona(int id, String nombre, String documento, String correo) {
        this.id = id;
        this.nombre = Objects.requireNonNull(nombre, "nombre");
        this.documento = Objects.requireNonNull(documento, "documento");
        this.correo = Objects.requireNonNull(correo, "correo");
    }

    public int id() {
        return id;
    }

    public String nombre() {
        return nombre;
    }

    public String documento() {
        return documento;
    }

    public String correo() {
        return correo;
    }

    public abstract String tipo();

    public abstract String descripcion();

    @Override
    public String toString() {
        return descripcion();
    }

    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        if (otro == null || getClass() != otro.getClass()) {
            return false;
        }
        Persona persona = (Persona) otro;
        return id == persona.id && documento.equals(persona.documento);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, documento);
    }
}
