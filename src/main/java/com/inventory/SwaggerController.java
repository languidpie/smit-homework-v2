package com.inventory;

import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Produces;
import io.swagger.v3.oas.annotations.Hidden;

/**
 * Controller for serving Swagger UI interface.
 * Provides an HTML page that loads the Swagger UI for API documentation.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Controller
@Hidden
public class SwaggerController {

    private static final String SWAGGER_UI_HTML = """
            <!DOCTYPE html>
            <html lang="en">
            <head>
                <meta charset="UTF-8">
                <title>Inventory API - Swagger UI</title>
                <link rel="stylesheet" type="text/css" href="/webjars/swagger-ui/5.10.3/swagger-ui.css" />
                <style>
                    html { box-sizing: border-box; overflow-y: scroll; }
                    *, *:before, *:after { box-sizing: inherit; }
                    body { margin: 0; background: #fafafa; }
                </style>
            </head>
            <body>
                <div id="swagger-ui"></div>
                <script src="/webjars/swagger-ui/5.10.3/swagger-ui-bundle.js" charset="UTF-8"></script>
                <script src="/webjars/swagger-ui/5.10.3/swagger-ui-standalone-preset.js" charset="UTF-8"></script>
                <script>
                    window.onload = function() {
                        window.ui = SwaggerUIBundle({
                            url: "/swagger/inventory-management-api-1.0.yml",
                            dom_id: '#swagger-ui',
                            deepLinking: true,
                            presets: [
                                SwaggerUIBundle.presets.apis,
                                SwaggerUIStandalonePreset
                            ],
                            plugins: [
                                SwaggerUIBundle.plugins.DownloadUrl
                            ],
                            layout: "StandaloneLayout"
                        });
                    };
                </script>
            </body>
            </html>
            """;

    @Get("/swagger-ui")
    @Produces(MediaType.TEXT_HTML)
    public String swaggerUi() {
        return SWAGGER_UI_HTML;
    }
}
