package com.conversor.monedas.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;

class WebClientConfigTest {

    @Test
    @DisplayName("frankfurterWebClient crea un WebClient a partir de la URL base indicada")
    void frankfurterWebClient_creaClienteConBaseUrl() {
        WebClient client = new WebClientConfig().frankfurterWebClient("http://localhost:9999/v1");

        assertThat(client).isNotNull();
    }
}
