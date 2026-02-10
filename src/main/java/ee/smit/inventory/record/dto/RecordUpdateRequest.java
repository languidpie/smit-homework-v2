package ee.smit.inventory.record.dto;

import ee.smit.inventory.record.Genre;
import ee.smit.inventory.record.RecordCondition;
import io.micronaut.serde.annotation.Serdeable;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

/**
 * DTO for updating an existing vinyl record.
 * All fields are optional for partial updates.
 */
@Serdeable
public record RecordUpdateRequest(
        @Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
        String title,

        @Size(min = 1, max = 255, message = "Artist must be between 1 and 255 characters")
        String artist,

        @Min(value = 1900, message = "Release year must be 1900 or later")
        @Max(value = 2100, message = "Release year must be 2100 or earlier")
        Integer releaseYear,

        Genre genre,

        @Size(max = 255, message = "Purchase source must be less than 255 characters")
        String purchaseSource,

        LocalDate purchaseDate,

        RecordCondition condition,

        @Size(max = 255, message = "Notes must be less than 255 characters")
        String notes
) {}
