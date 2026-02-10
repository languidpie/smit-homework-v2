package ee.smit.inventory.part.dto;

import ee.smit.inventory.part.Part;
import ee.smit.inventory.part.PartCondition;
import ee.smit.inventory.part.PartType;
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
