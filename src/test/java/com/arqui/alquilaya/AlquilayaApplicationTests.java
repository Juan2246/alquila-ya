package com.arqui.alquilaya;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Prueba de humo del arranque de la aplicación.
 *
 * <p>Levanta el contexto completo de Spring contra la base de datos en memoria
 * del perfil {@code test}. Falla si algún bean, repositorio o pieza de la
 * configuración de seguridad deja de resolverse, de modo que un error de
 * cableado se detecta en el build y no en tiempo de ejecución.
 */
@SpringBootTest
@ActiveProfiles("test")
class AlquilayaApplicationTests {

    @Test
    @DisplayName("El contexto de la aplicación se levanta sin errores")
    void contextLoads() {
        // El propio arranque del contexto es la aserción: si algún bean no se
        // puede construir, SpringBootTest hace fallar la prueba.
    }
}
