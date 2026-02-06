package com.inventory.record;

import com.inventory.exception.NotFoundException;
import com.inventory.exception.ValidationException;
import com.inventory.record.dto.RecordCreateRequest;
import com.inventory.record.dto.RecordUpdateRequest;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service class for {@link VinylRecord} entity.
 * Handles business logic and validation for vinyl records.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Singleton
public class RecordService {

    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2100;

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Transactional
    public VinylRecord create(RecordCreateRequest request) {
        // Service-level validation
        validateCreateRequest(request);

        VinylRecord record = new VinylRecord();
        record.setTitle(request.title().trim());
        record.setArtist(request.artist().trim());
        record.setReleaseYear(request.releaseYear());
        record.setGenre(request.genre());
        record.setPurchaseSource(request.purchaseSource() != null && !request.purchaseSource().trim().isEmpty()
                ? request.purchaseSource().trim() : null);
        record.setPurchaseDate(request.purchaseDate());
        record.setCondition(request.condition());
        record.setNotes(request.notes() != null && !request.notes().trim().isEmpty()
                ? request.notes().trim() : null);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());

        return recordRepository.save(record);
    }

    private void validateCreateRequest(RecordCreateRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (request.title() == null || request.title().trim().isEmpty()) {
            errors.put("title", "Title is required and cannot be blank");
        }
        if (request.artist() == null || request.artist().trim().isEmpty()) {
            errors.put("artist", "Artist is required and cannot be blank");
        }
        if (request.releaseYear() == null) {
            errors.put("releaseYear", "Release year is required");
        } else if (request.releaseYear() < MIN_YEAR || request.releaseYear() > MAX_YEAR) {
            errors.put("releaseYear", "Release year must be between " + MIN_YEAR + " and " + MAX_YEAR);
        }
        if (request.genre() == null) {
            errors.put("genre", "Genre is required");
        }
        if (request.condition() == null) {
            errors.put("condition", "Condition is required");
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    public Optional<VinylRecord> findById(Long id) {
        return recordRepository.findById(id);
    }

    public List<VinylRecord> findAll() {
        return (List<VinylRecord>) recordRepository.findAll();
    }

    public List<VinylRecord> findByGenre(Genre genre) {
        return recordRepository.findByGenre(genre);
    }

    public List<VinylRecord> search(String query) {
        return recordRepository.searchByTitleOrArtist(query);
    }

    @Transactional
    public VinylRecord update(Long id, RecordUpdateRequest request) {
        VinylRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("VinylRecord", id));

        // Validate update request
        validateUpdateRequest(request);

        if (request.title() != null) {
            record.setTitle(request.title().trim());
        }
        if (request.artist() != null) {
            record.setArtist(request.artist().trim());
        }
        if (request.releaseYear() != null) {
            record.setReleaseYear(request.releaseYear());
        }
        if (request.genre() != null) {
            record.setGenre(request.genre());
        }
        if (request.purchaseSource() != null) {
            record.setPurchaseSource(request.purchaseSource().trim());
        }
        if (request.purchaseDate() != null) {
            record.setPurchaseDate(request.purchaseDate());
        }
        if (request.condition() != null) {
            record.setCondition(request.condition());
        }
        if (request.notes() != null) {
            record.setNotes(request.notes().trim());
        }
        record.setUpdatedAt(LocalDateTime.now());

        return recordRepository.update(record);
    }

    private void validateUpdateRequest(RecordUpdateRequest request) {
        Map<String, String> errors = new HashMap<>();

        // If title is provided, it must not be blank
        if (request.title() != null && request.title().trim().isEmpty()) {
            errors.put("title", "Title cannot be blank");
        }
        // If artist is provided, it must not be blank
        if (request.artist() != null && request.artist().trim().isEmpty()) {
            errors.put("artist", "Artist cannot be blank");
        }
        // If releaseYear is provided, it must be in valid range
        if (request.releaseYear() != null && (request.releaseYear() < MIN_YEAR || request.releaseYear() > MAX_YEAR)) {
            errors.put("releaseYear", "Release year must be between " + MIN_YEAR + " and " + MAX_YEAR);
        }

        if (!errors.isEmpty()) {
            throw new ValidationException("Validation failed", errors);
        }
    }

    @Transactional
    public void delete(Long id) {
        if (!recordRepository.existsById(id)) {
            throw new NotFoundException("VinylRecord", id);
        }
        recordRepository.deleteById(id);
    }

    public long countByGenre(Genre genre) {
        return recordRepository.countByGenre(genre);
    }

    public long countTotal() {
        return recordRepository.count();
    }
}
