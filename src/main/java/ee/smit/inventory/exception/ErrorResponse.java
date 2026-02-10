package ee.smit.inventory.exception;

import io.micronaut.serde.annotation.Serdeable;

import java.util.Map;

/**
 * Standard error response DTO for API error messages.
 * Contains a user-friendly message and optional field-specific errors.
 */
@Serdeable
public record ErrorResponse(
        String message,
        Map<String, String> errors
) {
    public ErrorResponse(String message) {
        this(message, null);
    }
}
