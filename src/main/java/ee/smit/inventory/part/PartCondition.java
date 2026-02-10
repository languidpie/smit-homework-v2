package ee.smit.inventory.part;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Enumeration of bicycle part conditions.
 */
@Serdeable
public enum PartCondition {
    NEW("New"),
    EXCELLENT("Excellent"),
    GOOD("Good"),
    FAIR("Fair"),
    POOR("Poor");

    private final String displayName;

    PartCondition(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
