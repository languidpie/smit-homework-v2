package com.inventory.record;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Enumeration of vinyl record conditions using standard grading.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Serdeable
public enum RecordCondition {
    MINT("Mint"),
    NEAR_MINT("Near Mint"),
    EXCELLENT("Excellent"),
    VERY_GOOD("Very Good"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor");

    private final String displayName;

    RecordCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
