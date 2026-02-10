package com.inventory.part.dto;

import com.inventory.part.Part;
import com.inventory.part.PartCondition;
import com.inventory.part.PartType;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDateTime;

/**
 * DTO for returning bicycle part data in API responses.
 */
@Serdeable
public record PartResponse(
        Long id,
        String name,
        String description,
        PartType type,
        String location,
        Integer quantity,
        PartCondition condition,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static PartResponse fromEntity(Part part) {
        return new PartResponse(
                part.getId(),
                part.getName(),
                part.getDescription(),
                part.getType(),
                part.getLocation(),
                part.getQuantity(),
                part.getCondition(),
                part.getNotes(),
                part.getCreatedAt(),
                part.getUpdatedAt()
        );
    }
}
