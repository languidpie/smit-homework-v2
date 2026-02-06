package com.inventory.exception;

/**
 * Exception thrown when a requested entity is not found in the database.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
public class NotFoundException extends RuntimeException {

    private final String entityType;
    private final Long entityId;

    public NotFoundException(String entityType, Long entityId) {
        super(entityType + " not found with id: " + entityId);
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public Long getEntityId() {
        return entityId;
    }
}
