package com.conversor.monedas.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Pruebas unitarias de ConversionHistory, siguiendo el patrón AAA.
 *
 * El callback @PrePersist se invoca directamente: no hace falta
 * levantar JPA ni la base de datos para probar su lógica.
 */
class ConversionHistoryTest {

    @Test
    @DisplayName("prePersist debe asignar la fecha actual cuando createdAt es null")
    void prePersist_debeAsignarFechaActualSiCreatedAtEsNull() {
        // Arrange
        ConversionHistory historial = new ConversionHistory();
        LocalDateTime antes = LocalDateTime.now();

        // Act
        historial.prePersist();

        // Assert
        LocalDateTime despues = LocalDateTime.now();
        assertThat(historial.getCreatedAt())
                .isNotNull()
                .isAfterOrEqualTo(antes)
                .isBeforeOrEqualTo(despues);
    }

    @Test
    @DisplayName("prePersist no debe sobrescribir createdAt si ya tiene valor")
    void prePersist_noDebeSobrescribirCreatedAtExistente() {
        // Arrange
        LocalDateTime fechaOriginal = LocalDateTime.of(2026, 1, 15, 10, 30);
        ConversionHistory historial = new ConversionHistory();
        historial.setCreatedAt(fechaOriginal);

        // Act
        historial.prePersist();

        // Assert
        assertThat(historial.getCreatedAt()).isEqualTo(fechaOriginal);
    }
}