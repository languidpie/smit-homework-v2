package ee.smit.inventory.part;

/**
 * End-to-end integration tests for bicycle parts API.
 * Tests complete workflows including validation errors and error responses.
 */

import ee.smit.inventory.exception.ErrorResponse;
import ee.smit.inventory.part.dto.PartCreateRequest;
import ee.smit.inventory.part.dto.PartResponse;
import ee.smit.inventory.part.dto.PartUpdateRequest;
import io.micronaut.core.type.Argument;
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
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@MicronautTest
class PartIntegrationTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    PartRepository partRepository;

    @BeforeEach
    void setUp() {
        partRepository.deleteAll();
    }

    @Nested
    class ValidationErrorTests {

        @Test
        void should_return_validation_error_when_name_is_blank() {
            // given
            String token = loginAndGetToken("mart", "mart123");
            PartCreateRequest request = new PartCreateRequest(
                    "",
                    "Description",
                    PartType.BRAKE,
                    "Garage",
                    5,
                    PartCondition.NEW,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/parts", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_quantity_is_zero() {
            // given
            String token = loginAndGetToken("mart", "mart123");
            PartCreateRequest request = new PartCreateRequest(
                    "Test Part",
                    "Description",
                    PartType.BRAKE,
                    "Garage",
                    0,
                    PartCondition.NEW,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/parts", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_quantity_is_negative() {
            // given
            String token = loginAndGetToken("mart", "mart123");
            PartCreateRequest request = new PartCreateRequest(
                    "Test Part",
                    "Description",
                    PartType.BRAKE,
                    "Garage",
                    -5,
                    PartCondition.NEW,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/parts", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_type_is_null() {
            // given
            String token = loginAndGetToken("mart", "mart123");
            PartCreateRequest request = new PartCreateRequest(
                    "Test Part",
                    "Description",
                    null,
                    "Garage",
                    5,
                    PartCondition.NEW,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/parts", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_updating_with_negative_quantity() {
            // given
            String token = loginAndGetToken("mart", "mart123");
            Long partId = createTestPartAndGetId(token);
            PartUpdateRequest updateRequest = new PartUpdateRequest(
                    null, null, null, null,
                    -5,
                    null, null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.PUT("/api/parts/" + partId, updateRequest).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }
    }

    @Nested
    class CrudWorkflowTests {

        @Test
        void should_complete_full_crud_lifecycle() {
            // given
            String token = loginAndGetToken("mart", "mart123");
            PartCreateRequest createRequest = new PartCreateRequest(
                    "Shimano Brake",
                    "High quality disc brake",
                    PartType.BRAKE,
                    "Garage",
                    10,
                    PartCondition.NEW,
                    "Premium item"
            );

            // when - create
            HttpResponse<PartResponse> createResponse = client.toBlocking()
                    .exchange(HttpRequest.POST("/api/parts", createRequest).bearerAuth(token), PartResponse.class);

            // then - create
            assertThat(createResponse.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
            Long partId = createResponse.body().id();
            assertThat(partId).isNotNull();

            // when - read
            PartResponse readResponse = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/parts/" + partId).bearerAuth(token), PartResponse.class);

            // then - read
            assertThat(readResponse.name()).isEqualTo("Shimano Brake");
            assertThat(readResponse.type()).isEqualTo(PartType.BRAKE);
            assertThat(readResponse.quantity()).isEqualTo(10);

            // given - update
            PartUpdateRequest updateRequest = new PartUpdateRequest(
                    "Shimano XT Brake",
                    "Updated description",
                    null, null,
                    15,
                    PartCondition.EXCELLENT,
                    "Updated notes"
            );

            // when - update
            PartResponse updateResponse = client.toBlocking()
                    .retrieve(HttpRequest.PUT("/api/parts/" + partId, updateRequest).bearerAuth(token), PartResponse.class);

            // then - update
            assertThat(updateResponse.name()).isEqualTo("Shimano XT Brake");
            assertThat(updateResponse.quantity()).isEqualTo(15);
            assertThat(updateResponse.condition()).isEqualTo(PartCondition.EXCELLENT);

            // when - delete
            HttpResponse<?> deleteResponse = client.toBlocking()
                    .exchange(HttpRequest.DELETE("/api/parts/" + partId).bearerAuth(token));

            // then - delete
            assertThat(deleteResponse.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());

            // when - verify deleted
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().retrieve(HttpRequest.GET("/api/parts/" + partId).bearerAuth(token), PartResponse.class));

            // then - verify deleted
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
        }

        @Test
        void should_filter_and_search_parts_correctly() {
            // given
            String token = loginAndGetToken("mart", "mart123");
            createPartWithDetails("Shimano Brake", PartType.BRAKE, token);
            createPartWithDetails("SRAM Brake", PartType.BRAKE, token);
            createPartWithDetails("Continental Tire", PartType.TIRE, token);
            createPartWithDetails("Topeak Pump", PartType.PUMP, token);

            // when - filter by type
            PartResponse[] brakes = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/parts/type/BRAKE").bearerAuth(token), PartResponse[].class);

            // then - filter by type
            assertThat(brakes).hasSize(2);
            assertThat(brakes).allMatch(p -> p.type() == PartType.BRAKE);

            // when - search by name
            PartResponse[] shimanoResults = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/parts/search?q=shimano").bearerAuth(token), PartResponse[].class);

            // then - search by name
            assertThat(shimanoResults).hasSize(1);
            assertThat(shimanoResults[0].name()).contains("Shimano");

            // when - get all (paginated)
            Map<String, Object> pageResponse = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/parts").bearerAuth(token), Argument.of(Map.class, String.class, Object.class));

            // then - get all
            List<?> content = (List<?>) pageResponse.get("content");
            assertThat(content).hasSize(4);
            assertThat(pageResponse.get("totalElements")).isEqualTo(4);
        }
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

    private void createPartWithDetails(String name, PartType type, String token) {
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
