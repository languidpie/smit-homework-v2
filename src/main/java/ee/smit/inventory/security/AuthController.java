package ee.smit.inventory.security;

import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.micronaut.serde.annotation.Serdeable;

import java.util.Collection;

@Controller("/api/auth")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class AuthController {

    @Get("/me")
    public UserInfo me(Authentication authentication) {
        String username = authentication.getName();
        Collection<String> roles = authentication.getRoles();
        String role = roles.stream().findFirst().orElse("");
        return new UserInfo(username, role);
    }

    @Serdeable
    public record UserInfo(String username, String role) {}
}
