package ee.smit.inventory.security;

import io.micronaut.core.annotation.NonNull;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.http.HttpRequest;
import io.micronaut.security.authentication.AuthenticationFailureReason;
import io.micronaut.security.authentication.AuthenticationRequest;
import io.micronaut.security.authentication.AuthenticationResponse;
import io.micronaut.security.authentication.provider.HttpRequestAuthenticationProvider;
import jakarta.inject.Singleton;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Map;

@Singleton
public class AuthenticationProviderUserPassword<B> implements HttpRequestAuthenticationProvider<B> {

    private static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    // Passwords hashed with BCrypt (mart123, katrin123)
    private static final Map<String, UserCredentials> USERS = Map.of(
            "mart", new UserCredentials("$2a$12$AnBLNLp0.JrvxnnEh0IGQOFuGYrwCIIVfXCj1tg6DsoFVLTHheLhW", List.of(Roles.ROLE_PARTS)),
            "katrin", new UserCredentials("$2a$12$2l6BOapDlZcMruaGQfVFeOM.pfICYR9MZ7Kz91KBSdSZnq55DT52S", List.of(Roles.ROLE_RECORDS))
    );

    @Override
    public @NonNull AuthenticationResponse authenticate(
            @Nullable HttpRequest<B> httpRequest,
            @NonNull AuthenticationRequest<String, String> authenticationRequest) {

        String username = authenticationRequest.getIdentity();
        String password = authenticationRequest.getSecret();

        UserCredentials user = USERS.get(username);

        if (user == null) {
            return AuthenticationResponse.failure(AuthenticationFailureReason.USER_NOT_FOUND);
        }

        if (!PASSWORD_ENCODER.matches(password, user.passwordHash())) {
            return AuthenticationResponse.failure(AuthenticationFailureReason.CREDENTIALS_DO_NOT_MATCH);
        }

        return AuthenticationResponse.success(username, user.roles());
    }

    private record UserCredentials(String passwordHash, List<String> roles) {}
}
