package com.conversor.monedas;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DotEnvLoaderTest {

    private static final String[] KEYS = {"DOTENV_TEST_A", "DOTENV_TEST_B", "DOTENV_TEST_C"};

    @TempDir
    Path tempDir;

    @AfterEach
    void limpiar() {
        for (String key : KEYS) {
            System.clearProperty(key);
        }
    }

    @Test
    @DisplayName("parseLine ignora líneas vacías, comentarios y líneas sin '='")
    void parseLine_ignoraLineasNoValidas() {
        assertThat(DotEnvLoader.parseLine("")).isEmpty();
        assertThat(DotEnvLoader.parseLine("   ")).isEmpty();
        assertThat(DotEnvLoader.parseLine("# comentario=x")).isEmpty();
        assertThat(DotEnvLoader.parseLine("SIN_IGUAL")).isEmpty();
    }

    @Test
    @DisplayName("parseLine separa clave y valor en el primer '=' y recorta espacios")
    void parseLine_separaClaveYValor() {
        assertThat(DotEnvLoader.parseLine("  KEY = a=b  "))
                .contains(Map.entry("KEY", "a=b"));
    }

    @Test
    @DisplayName("stripQuotes quita comillas dobles solo si envuelven todo el valor")
    void stripQuotes_quitaComillasEnvolventes() {
        assertThat(DotEnvLoader.stripQuotes("\"hola\"")).isEqualTo("hola");
        assertThat(DotEnvLoader.stripQuotes("\"hola")).isEqualTo("\"hola");
        assertThat(DotEnvLoader.stripQuotes("\"")).isEqualTo("\"");
        assertThat(DotEnvLoader.stripQuotes("plano")).isEqualTo("plano");
    }

    @Test
    @DisplayName("load carga las variables como System properties sin pisar las existentes")
    void load_cargaVariablesSinPisarExistentes() throws IOException {
        Path env = tempDir.resolve(".env");
        Files.writeString(env, "# cabecera\nDOTENV_TEST_A=uno\nDOTENV_TEST_B=\"dos\"\nDOTENV_TEST_C=nuevo\n");
        System.setProperty("DOTENV_TEST_C", "original");

        DotEnvLoader.load(env);

        assertThat(System.getProperty("DOTENV_TEST_A")).isEqualTo("uno");
        assertThat(System.getProperty("DOTENV_TEST_B")).isEqualTo("dos");
        assertThat(System.getProperty("DOTENV_TEST_C")).isEqualTo("original");
    }

    @Test
    @DisplayName("load no falla si el archivo no existe")
    void load_archivoInexistente_noHaceNada() {
        DotEnvLoader.load(tempDir.resolve("no-existe.env"));

        assertThat(System.getProperty("DOTENV_TEST_A")).isNull();
    }

    @Test
    @DisplayName("load no propaga la excepción si no puede leer el archivo")
    void load_noSePuedeLeer_noPropagaExcepcion() throws IOException {
        Path directorio = Files.createDirectory(tempDir.resolve("carpeta.env"));

        DotEnvLoader.load(directorio);

        assertThat(System.getProperty("DOTENV_TEST_A")).isNull();
    }
}
