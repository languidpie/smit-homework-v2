package com.inventory.record.dto;

import com.inventory.record.Genre;
import com.inventory.record.RecordCondition;
import com.inventory.record.VinylRecord;
import io.micronaut.serde.annotation.Serdeable;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO for returning vinyl record data in API responses.
 */
@Serdeable
public record RecordResponse(
        Long id,
        String title,
        String artist,
        Integer releaseYear,
        Genre genre,
        String purchaseSource,
        LocalDate purchaseDate,
        RecordCondition condition,
        String notes,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static RecordResponse fromEntity(VinylRecord record) {
        return new RecordResponse(
                record.getId(),
                record.getTitle(),
                record.getArtist(),
                record.getReleaseYear(),
                record.getGenre(),
                record.getPurchaseSource(),
                record.getPurchaseDate(),
                record.getCondition(),
                record.getNotes(),
                record.getCreatedAt(),
                record.getUpdatedAt()
        );
    }
}
