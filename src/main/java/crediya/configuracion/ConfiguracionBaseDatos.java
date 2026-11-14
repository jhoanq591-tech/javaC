package crediya.configuracion;

public record ConfiguracionBaseDatos(String url, String usuario, String clave) {

    private static final String URL_LOCAL = "jdbc:mysql://localhost:3306/crediya_db";
    private static final String USUARIO_LOCAL = "root";

    public static ConfiguracionBaseDatos desdeEntorno() {
        return new ConfiguracionBaseDatos(
                valor("DB_URL", URL_LOCAL),
                valor("DB_USER", USUARIO_LOCAL),
                valor("DB_PASSWORD", ""));
    }

    private static String valor(String nombre, String porDefecto) {
        String valor = System.getenv(nombre);
        return valor == null || valor.isBlank() ? porDefecto : valor;
    }

    @Override
    public String toString() {
        return "ConfiguracionBaseDatos[url=" + url + ", usuario=" + usuario + "]";
    }
}
