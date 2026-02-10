package ee.smit.inventory.part;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Enumeration of bicycle part types.
 */
@Serdeable
public enum PartType {
    FRAME,
    BRAKE,
    TIRE,
    PUMP,
    OTHER
}
