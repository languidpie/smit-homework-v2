package ee.smit.inventory.part;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Enumeration of bicycle part conditions.
 */
@Serdeable
public enum PartCondition {
    NEW,
    EXCELLENT,
    GOOD,
    FAIR,
    POOR
}
