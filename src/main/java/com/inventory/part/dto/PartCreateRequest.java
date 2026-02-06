package com.inventory.part.dto;

import com.inventory.part.PartCondition;
import com.inventory.part.PartType;
import io.micronaut.serde.annotation.Serdeable;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * DTO for creating a new bicycle part.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Serdeable
public record PartCreateRequest(
        @NotBlank(message = "Name is required")
        @Size(max = 255, message = "Name must be less than 255 characters")
        String name,

        @Size(max = 255, message = "Description must be less than 255 characters")
        String description,

        @NotNull(message = "Type is required")
        PartType type,

        @NotBlank(message = "Location is required")
        @Size(max = 255, message = "Location must be less than 255 characters")
        String location,

        @NotNull(message = "Quantity is required")
        @Positive(message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Condition is required")
        PartCondition condition,

        @Size(max = 255, message = "Notes must be less than 255 characters")
        String notes
) {}
