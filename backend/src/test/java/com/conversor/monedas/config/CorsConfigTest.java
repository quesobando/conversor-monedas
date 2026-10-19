package com.conversor.monedas.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CorsConfigTest {

    @Test
    @DisplayName("addCorsMappings registra /api/** con los orígenes y métodos configurados")
    void addCorsMappings_registraRutaApi() {
        CorsConfig config = new CorsConfig();
        ReflectionTestUtils.setField(config, "allowedOrigins", new String[]{"http://localhost:5173"});
        CorsRegistry registry = new CorsRegistry();

        config.addCorsMappings(registry);

        @SuppressWarnings("unchecked")
        Map<String, ?> mappings = (Map<String, ?>) ReflectionTestUtils.invokeMethod(registry, "getCorsConfigurations");
        assertThat(mappings).containsKey("/api/**");
        var cors = (org.springframework.web.cors.CorsConfiguration) mappings.get("/api/**");
        assertThat(cors.getAllowedOrigins()).containsExactly("http://localhost:5173");
        assertThat(cors.getAllowedMethods()).contains("GET", "POST", "PUT", "DELETE", "OPTIONS");
        assertThat(cors.getAllowedHeaders()).containsExactly("*");
    }
}
