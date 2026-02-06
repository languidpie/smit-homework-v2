package com.inventory.record.dto;

import com.inventory.record.Genre;
import com.inventory.record.RecordCondition;
import io.micronaut.serde.annotation.Serdeable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * DTO for creating a new vinyl record.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Serdeable
public record RecordCreateRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must be less than 255 characters")
        String title,

        @NotBlank(message = "Artist is required")
        @Size(max = 255, message = "Artist must be less than 255 characters")
        String artist,

        @NotNull(message = "Release year is required")
        @Min(value = 1900, message = "Release year must be 1900 or later")
        @Max(value = 2100, message = "Release year must be 2100 or earlier")
        Integer releaseYear,

        @NotNull(message = "Genre is required")
        Genre genre,

        @Size(max = 255, message = "Purchase source must be less than 255 characters")
        String purchaseSource,

        LocalDate purchaseDate,

        @NotNull(message = "Condition is required")
        RecordCondition condition,

        @Size(max = 255, message = "Notes must be less than 255 characters")
        String notes
) {}
