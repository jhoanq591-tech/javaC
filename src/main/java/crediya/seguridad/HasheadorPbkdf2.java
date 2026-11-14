package crediya.seguridad;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class HasheadorPbkdf2 implements HasheadorClaves {

    private static final String ALGORITMO = "PBKDF2WithHmacSHA256";
    private static final String PREFIJO = "pbkdf2_sha256";
    private static final String SEPARADOR = "$";
    private static final int ITERACIONES = 600_000;
    private static final int BYTES_SAL = 16;
    private static final int BITS_HASH = 256;

    private final SecureRandom aleatorio = new SecureRandom();

    @Override
    public String hashear(char[] clave) {
        byte[] sal = new byte[BYTES_SAL];
        aleatorio.nextBytes(sal);
        byte[] hash = derivar(clave, sal, ITERACIONES);
        Base64.Encoder codificador = Base64.getEncoder();
        return String.join(SEPARADOR, PREFIJO, String.valueOf(ITERACIONES),
                codificador.encodeToString(sal), codificador.encodeToString(hash));
    }

    @Override
    public boolean verificar(char[] clave, String hashGuardado) {
        try {
            String[] partes = hashGuardado.split("\\" + SEPARADOR);
            if (partes.length != 4 || !PREFIJO.equals(partes[0])) {
                return false;
            }
            int iteraciones = Integer.parseInt(partes[1]);
            byte[] sal = Base64.getDecoder().decode(partes[2]);
            byte[] esperado = Base64.getDecoder().decode(partes[3]);
            byte[] calculado = derivar(clave, sal, iteraciones);
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException | NullPointerException e) {
            return false;
        }
    }

    private byte[] derivar(char[] clave, byte[] sal, int iteraciones) {
        PBEKeySpec especificacion = new PBEKeySpec(clave, sal, iteraciones, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance(ALGORITMO).generateSecret(especificacion).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Algoritmo de hash no disponible.", e);
        } finally {
            especificacion.clearPassword();
        }
    }
}
