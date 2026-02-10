package com.inventory.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * Exception handler for {@link ValidationException}.
 * Returns a user-friendly 400 response with field-specific error messages.
 */
@Produces
@Singleton
public class ValidationExceptionHandler implements
        ExceptionHandler<ValidationException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, ValidationException exception) {
        return HttpResponse.badRequest(new ErrorResponse(
                "Please check your input and try again.",
                exception.getErrors()
        ));
    }
}
