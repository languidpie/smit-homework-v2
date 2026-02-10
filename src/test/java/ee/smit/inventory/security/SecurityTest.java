package ee.smit.inventory.security;

import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@MicronautTest
class SecurityTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Test
    void should_return_401_for_unauthenticated_parts_request() {
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/parts")));

        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }

    @Test
    void should_return_401_for_unauthenticated_records_request() {
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/records")));

        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }

    @Test
    void should_allow_mart_to_access_parts() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/parts").basicAuth("mart", "mart123"));

        assertThat(response).isNotNull();
    }

    @Test
    void should_deny_mart_access_to_records() {
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(
                        HttpRequest.GET("/api/records").basicAuth("mart", "mart123")));

        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.FORBIDDEN.getCode());
    }

    @Test
    void should_allow_katrin_to_access_records() {
        String response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/records").basicAuth("katrin", "katrin123"));

        assertThat(response).isNotNull();
    }

    @Test
    void should_deny_katrin_access_to_parts() {
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(
                        HttpRequest.GET("/api/parts").basicAuth("katrin", "katrin123")));

        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.FORBIDDEN.getCode());
    }

    @Test
    void should_return_401_for_wrong_password() {
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(
                        HttpRequest.GET("/api/parts").basicAuth("mart", "wrongpassword")));

        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.UNAUTHORIZED.getCode());
    }

    @Test
    void should_return_user_info_for_authenticated_user() {
        AuthController.UserInfo response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/auth/me").basicAuth("mart", "mart123"),
                        AuthController.UserInfo.class);

        assertThat(response.username()).isEqualTo("mart");
        assertThat(response.role()).isEqualTo("ROLE_PARTS");
    }

    @Test
    void should_allow_anonymous_access_to_health() {
        String response = client.toBlocking().retrieve(HttpRequest.GET("/health"));

        assertThat(response).contains("UP");
    }
}
