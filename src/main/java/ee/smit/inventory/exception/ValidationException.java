package ee.smit.inventory.exception;

import java.util.Map;

/**
 * Exception thrown when service-level validation fails.
 * Contains a map of field-specific error messages.
 */
public class ValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public ValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }

    public ValidationException(String field, String message) {
        super(message);
        this.errors = Map.of(field, message);
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
