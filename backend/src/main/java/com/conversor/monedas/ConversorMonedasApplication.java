package com.conversor.monedas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;

@SpringBootApplication
public class ConversorMonedasApplication {

    public static void main(String[] args) {
        DotEnvLoader.load(Path.of(".env"));
        SpringApplication.run(ConversorMonedasApplication.class, args);
    }
}
