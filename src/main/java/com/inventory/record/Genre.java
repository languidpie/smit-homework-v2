package com.inventory.record;

import io.micronaut.serde.annotation.Serdeable;

/**
 * Enumeration of music genres for vinyl records.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Serdeable
public enum Genre {
    ROCK("Rock"),
    JAZZ("Jazz"),
    BLUES("Blues"),
    CLASSICAL("Classical"),
    ELECTRONIC("Electronic"),
    POP("Pop"),
    HIP_HOP("Hip Hop"),
    COUNTRY("Country"),
    FOLK("Folk"),
    SOUL("Soul"),
    PUNK("Punk"),
    METAL("Metal"),
    OTHER("Other");

    private final String displayName;

    Genre(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
