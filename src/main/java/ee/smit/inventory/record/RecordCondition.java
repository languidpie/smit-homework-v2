package ee.smit.inventory.record;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Enumeration of vinyl record conditions using standard grading.
 */
@Serdeable
public enum RecordCondition {
    MINT,
    NEAR_MINT,
    EXCELLENT,
    VERY_GOOD,
    GOOD,
    FAIR,
    POOR
}
