package ee.smit.inventory;

import io.micronaut.runtime.Micronaut;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Main application entry point for the Inventory Management System.
 * Configures OpenAPI documentation for the REST API.
 */
@OpenAPIDefinition(
        info = @Info(
                title = "Inventory Management API",
                version = "1.0",
                description = "API for managing Katrin's vinyl record collection and Mart's bicycle parts inventory",
                contact = @Contact(name = "Mari-Liis", email = "mari@liis.ee")
        )
)
public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
