package ee.smit.inventory.part;

/**
 * Integration tests for {@link PartController}.
 * Tests HTTP endpoints for bicycle parts CRUD operations using Micronaut HTTP client.
 */

import ee.smit.inventory.part.dto.PartCreateRequest;
import ee.smit.inventory.part.dto.PartResponse;
import ee.smit.inventory.part.dto.PartUpdateRequest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.security.token.render.BearerAccessRefreshToken;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@MicronautTest
class PartControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    PartRepository partRepository;

    @BeforeEach
    void setUp() {
        partRepository.deleteAll();
    }

    @Test
    void should_create_part() {
        // given
        String token = loginAndGetToken("mart", "mart123");
        PartCreateRequest request = new PartCreateRequest(
                "Test Brake",
                "High quality brake",
                PartType.BRAKE,
                "Garage",
                10,
                PartCondition.NEW,
                "Premium"
        );

        // when
        HttpResponse<PartResponse> response = client.toBlocking()
                .exchange(HttpRequest.POST("/api/parts", request).bearerAuth(token), PartResponse.class);

        // then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().id()).isNotNull();
        assertThat(response.body().name()).isEqualTo("Test Brake");
        assertThat(response.header("Location")).contains("/api/parts/");
    }

    @Test
    void should_get_part_by_id() {
        // given
        String token = loginAndGetToken("mart", "mart123");
        Long partId = createTestPartAndGetId(token);

        // when
        PartResponse response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/parts/" + partId).bearerAuth(token), PartResponse.class);

        // then
        assertThat(response.id()).isEqualTo(partId);
        assertThat(response.name()).isEqualTo("Test Part");
    }

    @Test
    void should_return_404_for_non_existent_part() {
        // given
        String token = loginAndGetToken("mart", "mart123");
        Long nonExistentId = 9999L;

        // when
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/parts/" + nonExistentId).bearerAuth(token), PartResponse.class));

        // then
        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void should_update_part() {
        // given
        String token = loginAndGetToken("mart", "mart123");
        Long partId = createTestPartAndGetId(token);
        PartUpdateRequest updateRequest = new PartUpdateRequest(
                "Updated Name",
                null, null, null, null, null, null
        );

        // when
        PartResponse response = client.toBlocking()
                .retrieve(HttpRequest.PUT("/api/parts/" + partId, updateRequest).bearerAuth(token), PartResponse.class);

        // then
        assertThat(response.name()).isEqualTo("Updated Name");
    }

    @Test
    void should_delete_part() {
        // given
        String token = loginAndGetToken("mart", "mart123");
        Long partId = createTestPartAndGetId(token);

        // when
        HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/api/parts/" + partId).bearerAuth(token));

        // then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/parts/" + partId).bearerAuth(token), PartResponse.class));
        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
    }

    @Test
    void should_filter_by_type() {
        // given
        String token = loginAndGetToken("mart", "mart123");
        createTestPartWithType("Brake 1", PartType.BRAKE, token);
        createTestPartWithType("Brake 2", PartType.BRAKE, token);
        createTestPartWithType("Tire 1", PartType.TIRE, token);

        // when
        PartResponse[] response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/parts/type/BRAKE").bearerAuth(token), PartResponse[].class);

        // then
        assertThat(response).hasSize(2);
        assertThat(response).allMatch(p -> p.type() == PartType.BRAKE);
    }

    @Test
    void should_search_by_name() {
        // given
        String token = loginAndGetToken("mart", "mart123");
        createTestPartWithType("Shimano Brake", PartType.BRAKE, token);
        createTestPartWithType("SRAM Brake", PartType.BRAKE, token);

        // when
        PartResponse[] response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/parts/search?q=shimano").bearerAuth(token), PartResponse[].class);

        // then
        assertThat(response).hasSize(1);
        assertThat(response[0].name()).contains("Shimano");
    }

    private Long createTestPartAndGetId(String token) {
        PartCreateRequest request = new PartCreateRequest(
                "Test Part",
                "Test description",
                PartType.OTHER,
                "Garage",
                5,
                PartCondition.NEW,
                null
        );

        PartResponse response = client.toBlocking()
                .retrieve(HttpRequest.POST("/api/parts", request).bearerAuth(token), PartResponse.class);
        return response.id();
    }

    private void createTestPartWithType(String name, PartType type, String token) {
        PartCreateRequest request = new PartCreateRequest(
                name,
                "Test description",
                type,
                "Garage",
                5,
                PartCondition.NEW,
                null
        );

        client.toBlocking().exchange(HttpRequest.POST("/api/parts", request).bearerAuth(token), PartResponse.class);
    }

    private String loginAndGetToken(String username, String password) {
        HttpResponse<BearerAccessRefreshToken> response = client.toBlocking()
                .exchange(HttpRequest.POST("/login", Map.of("username", username, "password", password)),
                        BearerAccessRefreshToken.class);
        return response.body().getAccessToken();
    }
}
