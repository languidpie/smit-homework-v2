package ee.smit.inventory.exception;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.Produces;
import io.micronaut.http.server.exceptions.ExceptionHandler;
import jakarta.inject.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Catch-all exception handler for unexpected errors.
 * Logs the error details and returns a generic user-friendly message.
 */
@Produces
@Singleton
public class GeneralExceptionHandler implements
        ExceptionHandler<Exception, HttpResponse<ErrorResponse>> {

    private static final Logger LOG = LoggerFactory.getLogger(GeneralExceptionHandler.class);

    @Override
    public HttpResponse<ErrorResponse> handle(HttpRequest request, Exception exception) {
        // Log the actual error for debugging
        LOG.error("Unexpected error occurred: {}", exception.getMessage(), exception);

        // Return a user-friendly message
        return HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Something went wrong. Please try again later."));
    }
}
