package ee.smit.inventory.record;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Enumeration of music genres for vinyl records.
 */
@Serdeable
public enum Genre {
    ROCK,
    JAZZ,
    BLUES,
    CLASSICAL,
    ELECTRONIC,
    POP,
    HIP_HOP,
    COUNTRY,
    FOLK,
    SOUL,
    PUNK,
    METAL,
    OTHER
}
