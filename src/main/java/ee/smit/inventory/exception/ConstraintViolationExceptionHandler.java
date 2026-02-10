package ee.smit.inventory.exception;

import io.micronaut.context.annotation.Replaces;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import io.micronaut.validation.exceptions.ConstraintExceptionHandler;
import jakarta.inject.Singleton;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.HashMap;
import java.util.Map;

/**
 * Exception handler for Bean Validation constraint violations.
 * Converts validation errors to user-friendly field-specific messages.
 */
@Produces
@Singleton
@Replaces(ConstraintExceptionHandler.class)
public class ConstraintViolationExceptionHandler implements
        ExceptionHandler<ConstraintViolationException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, ConstraintViolationException exception) {
        Map<String, String> errors = new HashMap<>();

        for (ConstraintViolation<?> violation : exception.getConstraintViolations()) {
            String fieldName = extractFieldName(violation.getPropertyPath().toString());
            String message = violation.getMessage();
            errors.put(fieldName, message);
        }

        return HttpResponse.badRequest(new ErrorResponse(
                "Please check your input and try again.",
                errors
        ));
    }

    private String extractFieldName(String propertyPath) {
        // Property path looks like "create.request.name" or "update.request.title"
        // We want just the field name
        if (propertyPath.contains(".")) {
            String[] parts = propertyPath.split("\\.");
            return parts[parts.length - 1];
        }
        return propertyPath;
    }
}
