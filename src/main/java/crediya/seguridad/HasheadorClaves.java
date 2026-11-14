package crediya.seguridad;

public interface HasheadorClaves {

    String hashear(char[] clave);

    boolean verificar(char[] clave, String hashGuardado);
}
