package crediya.seguridad;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class HasheadorPbkdf2Test {

    private final HasheadorClaves hasheador = new HasheadorPbkdf2();

    @Test
    void verificaLaClaveCorrectaYRechazaLaIncorrecta() {
        String hash = hasheador.hashear("Clave-Segura-1".toCharArray());
        assertTrue(hasheador.verificar("Clave-Segura-1".toCharArray(), hash));
        assertFalse(hasheador.verificar("otra-clave".toCharArray(), hash));
    }

    @Test
    void cadaHashUsaUnaSalDistinta() {
        char[] clave = "Clave-Segura-1".toCharArray();
        assertNotEquals(hasheador.hashear(clave), hasheador.hashear(clave));
    }

    @Test
    void hashMalFormadoSeRechazaSinLanzarExcepcion() {
        char[] clave = "x".toCharArray();
        assertFalse(hasheador.verificar(clave, "texto plano"));
        assertFalse(hasheador.verificar(clave, "pbkdf2_sha256$abc$!!$!!"));
        assertFalse(hasheador.verificar(clave, null));
    }
}
