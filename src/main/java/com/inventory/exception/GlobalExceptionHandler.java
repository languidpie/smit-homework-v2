package com.inventory.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;

/**
 * Exception handler for {@link NotFoundException}.
 * Returns a user-friendly 404 response when an entity is not found.
 */
@Produces
@Singleton
public class GlobalExceptionHandler implements
        ExceptionHandler<NotFoundException, HttpResponse<ErrorResponse>> {

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, NotFoundException exception) {
        return HttpResponse.notFound(new ErrorResponse("The requested item was not found."));
    }
}
