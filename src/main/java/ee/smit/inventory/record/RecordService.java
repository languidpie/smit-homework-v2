package ee.smit.inventory.record;

import ee.smit.inventory.common.QueryUtils;
import ee.smit.inventory.exception.NotFoundException;
import ee.smit.inventory.record.dto.RecordCreateRequest;
import ee.smit.inventory.record.dto.RecordUpdateRequest;
import io.micronaut.data.model.Page;
import io.micronaut.data.model.Pageable;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link VinylRecord} entity.
 * Handles business logic and validation for vinyl records.
 */
@Singleton
public class RecordService {

    private final RecordRepository recordRepository;

    public RecordService(RecordRepository recordRepository) {
        this.recordRepository = recordRepository;
    }

    @Transactional
    public VinylRecord create(RecordCreateRequest request) {
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

    public Optional<VinylRecord> findById(Long id) {
        return recordRepository.findById(id);
    }

    public Page<VinylRecord> findAll(Pageable pageable) {
        return recordRepository.findAll(pageable);
    }

    public List<VinylRecord> findByGenre(Genre genre) {
        return recordRepository.findByGenre(genre);
    }

    public List<VinylRecord> search(String query) {
        return recordRepository.searchByTitleOrArtist(QueryUtils.escapeLikePattern(query));
    }

    @Transactional
    public VinylRecord update(Long id, RecordUpdateRequest request) {
        VinylRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("VinylRecord", id));

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
            record.setPurchaseSource(request.purchaseSource().trim().isEmpty() ? null : request.purchaseSource().trim());
        }
        if (request.purchaseDate() != null) {
            record.setPurchaseDate(request.purchaseDate());
        }
        if (request.condition() != null) {
            record.setCondition(request.condition());
        }
        if (request.notes() != null) {
            record.setNotes(request.notes().trim().isEmpty() ? null : request.notes().trim());
        }
        record.setUpdatedAt(LocalDateTime.now());

        return recordRepository.update(record);
    }

    @Transactional
    public void delete(Long id) {
        VinylRecord record = recordRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("VinylRecord", id));
        recordRepository.delete(record);
    }

}
