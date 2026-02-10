package ee.smit.inventory.part.dto;

import ee.smit.inventory.part.PartCondition;
import ee.smit.inventory.part.PartType;
import io.micronaut.serde.annotation.Serdeable;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO for updating an existing bicycle part.
 * All fields are optional for partial updates.
 */
@Serdeable
public record PartUpdateRequest(
        @Size(min = 1, max = 255, message = "Name must be between 1 and 255 characters")
        String name,

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description,

        PartType type,

        @Size(min = 1, max = 255, message = "Location must be between 1 and 255 characters")
        String location,

        @Positive(message = "Quantity must be at least 1")
        Integer quantity,

        PartCondition condition,

        @Size(max = 255, message = "Notes must be less than 255 characters")
        String notes
) {}
