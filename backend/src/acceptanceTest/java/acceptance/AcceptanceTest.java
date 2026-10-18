package acceptance;

import io.karatelabs.core.Runner;
import io.karatelabs.core.SuiteResult;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Punto de entrada de las pruebas de aceptación (Karate).
 * Ejecuta todos los .feature de src/acceptanceTest/resources/features contra la API ya levantada.
 *
 * Se lanza con:  gradle acceptanceTest [-DbaseUrl=http://localhost:8080]
 */
class AcceptanceTest {

    @Test
    void acceptance() {
        String baseUrl = System.getProperty("baseUrl", "http://localhost:8080");

        SuiteResult result = Runner.builder()
                .path("classpath:features")
                .tags("~@ignore")
                .systemProperty("baseUrl", baseUrl)
                .outputJunitXml(true)
                .outputDir("build/karate-reports")
                .parallel(4);

        assertEquals(0, result.getScenarioFailedCount(), String.join("\n", result.getErrors()));
    }
}
