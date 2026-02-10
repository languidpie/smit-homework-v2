package com.inventory.part;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Enumeration of bicycle part types.
 */
@Serdeable
public enum PartType {
    FRAME("Frame"),
    BRAKE("Brake"),
    TIRE("Tire"),
    PUMP("Pump"),
    OTHER("Other");

    private final String displayName;

    PartType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
