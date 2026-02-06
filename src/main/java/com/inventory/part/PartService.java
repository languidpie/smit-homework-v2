package com.inventory.part;

import com.inventory.exception.NotFoundException;
import com.inventory.part.dto.PartCreateRequest;
import com.inventory.part.dto.PartUpdateRequest;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for {@link Part} entity.
 * Handles business logic and validation for bicycle parts.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Singleton
public class PartService {

    private final PartRepository partRepository;

    public PartService(PartRepository partRepository) {
        this.partRepository = partRepository;
    }

    @Transactional
    public Part create(PartCreateRequest request) {
        Part part = new Part();
        part.setName(request.name().trim());
        part.setDescription(request.description() != null ? request.description().trim() : "");
        part.setType(request.type());
        part.setLocation(request.location().trim());
        part.setQuantity(request.quantity());
        part.setCondition(request.condition());
        part.setNotes(request.notes() != null ? request.notes().trim() : "");
        part.setCreatedAt(LocalDateTime.now());
        part.setUpdatedAt(LocalDateTime.now());

        return partRepository.save(part);
    }

    public Optional<Part> findById(Long id) {
        return partRepository.findById(id);
    }

    public List<Part> findAll() {
        return (List<Part>) partRepository.findAll();
    }

    public List<Part> findByType(PartType type) {
        return partRepository.findByType(type);
    }

    public List<Part> searchByName(String name) {
        return partRepository.findByNameContainsIgnoreCase(name);
    }

    @Transactional
    public Part update(Long id, PartUpdateRequest request) {
        Part part = partRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Part", id));

        if (request.name() != null) {
            part.setName(request.name().trim());
        }
        if (request.description() != null) {
            part.setDescription(request.description().trim());
        }
        if (request.type() != null) {
            part.setType(request.type());
        }
        if (request.location() != null) {
            part.setLocation(request.location().trim());
        }
        if (request.quantity() != null) {
            part.setQuantity(request.quantity());
        }
        if (request.condition() != null) {
            part.setCondition(request.condition());
        }
        if (request.notes() != null) {
            part.setNotes(request.notes().trim());
        }
        part.setUpdatedAt(LocalDateTime.now());

        return partRepository.update(part);
    }

    @Transactional
    public void delete(Long id) {
        if (!partRepository.existsById(id)) {
            throw new NotFoundException("Part", id);
        }
        partRepository.deleteById(id);
    }

    public long countByType(PartType type) {
        return partRepository.countByType(type);
    }

    public long countTotal() {
        return partRepository.count();
    }
}
