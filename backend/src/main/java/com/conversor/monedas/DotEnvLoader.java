package com.conversor.monedas;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

/**
 * Carga un archivo .env como System properties, para no depender de que el usuario
 * exporte variables de entorno manualmente. Las variables ya definidas (entorno real
 * o System property) tienen prioridad y no se pisan.
 */
@Slf4j
public final class DotEnvLoader {

    private DotEnvLoader() {
    }

    public static void load(Path envFile) {
        if (!Files.exists(envFile)) {
            log.info("[.env] No se encontró archivo .env en: {}", envFile.toAbsolutePath());
            return;
        }
        try {
            for (String line : Files.readAllLines(envFile)) {
                parseLine(line).ifPresent(DotEnvLoader::applyIfAbsent);
            }
            log.info("[.env] Variables cargadas desde {}", envFile.toAbsolutePath());
        } catch (IOException e) {
            log.warn("[.env] No se pudo leer el archivo .env: {}", e.getMessage());
        }
    }

    static Optional<Map.Entry<String, String>> parseLine(String line) {
        String trimmed = line.trim();
        int idx = trimmed.indexOf('=');
        if (trimmed.isEmpty() || trimmed.startsWith("#") || idx < 0) {
            return Optional.empty();
        }
        String key = trimmed.substring(0, idx).trim();
        String value = stripQuotes(trimmed.substring(idx + 1).trim());
        return Optional.of(Map.entry(key, value));
    }

    static String stripQuotes(String value) {
        if (value.length() >= 2 && value.startsWith("\"") && value.endsWith("\"")) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    private static void applyIfAbsent(Map.Entry<String, String> entry) {
        String key = entry.getKey();
        if (System.getenv(key) == null && System.getProperty(key) == null) {
            System.setProperty(key, entry.getValue());
        }
    }
}
