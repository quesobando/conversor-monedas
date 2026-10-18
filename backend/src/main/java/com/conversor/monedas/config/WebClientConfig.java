package com.conversor.monedas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class WebClientConfig {

@Bean
public WebClient frankfurterWebClient(
        @Value("${frankfurter.base-url:https://api.frankfurter.dev/v1}") String baseUrl) {
    return WebClient.builder().baseUrl(baseUrl).build();
}
}