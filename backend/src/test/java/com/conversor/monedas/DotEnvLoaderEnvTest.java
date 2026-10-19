package com.conversor.monedas;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Cubre la rama de DotEnvLoader que no cubre DotEnvLoaderTest:
 * una variable de entorno REAL del sistema tiene prioridad sobre el archivo .env.
 */
class DotEnvLoaderEnvTest {

    @TempDir
    Path tempDir;

    @Test
    @DisplayName("load no pisa una variable de entorno real del sistema")
    void load_noPisaVariableDeEntornoReal() throws IOException {
        // Arrange: se toma una variable de entorno que exista en el sistema donde corre el test
        // (nombre válido y sin System property equivalente, para poder comprobar que no se crea).
        String clave = System.getenv().keySet().stream()
                .filter(k -> k.matches("[A-Za-z_][A-Za-z0-9_]*"))
                .filter(k -> System.getProperty(k) == null)
                .findFirst()
                .orElseThrow();
        Path env = tempDir.resolve(".env");
        Files.writeString(env, clave + "=valor-del-archivo-env\n");

        // Act
        DotEnvLoader.load(env);

        // Assert: el valor del .env no se convirtió en System property
        assertThat(System.getProperty(clave)).isNull();
    }
}
