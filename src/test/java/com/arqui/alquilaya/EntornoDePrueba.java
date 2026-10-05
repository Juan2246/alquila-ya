package com.arqui.alquilaya;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import java.security.SecureRandom;
import java.util.Base64;

/** Clave efímera por ejecución, sin secretos de pruebas versionados. */
abstract class EntornoDePrueba {
    private static final String CLAVE = nuevaClave();

    private static String nuevaClave() {
        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    @DynamicPropertySource
    static void configuracion(DynamicPropertyRegistry propiedades) {
        propiedades.add("jwt.secret", () -> CLAVE);
    }
}
