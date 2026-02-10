package ee.smit.inventory.record;

/**
 * End-to-end integration tests for vinyl records API.
 * Tests complete workflows including validation errors and error responses.
 */

import ee.smit.inventory.exception.ErrorResponse;
import ee.smit.inventory.record.dto.RecordCreateRequest;
import ee.smit.inventory.record.dto.RecordResponse;
import ee.smit.inventory.record.dto.RecordUpdateRequest;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@MicronautTest
class RecordIntegrationTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    RecordRepository recordRepository;

    @BeforeEach
    void setUp() {
        recordRepository.deleteAll();
    }

    @Nested
    class ValidationErrorTests {

        @Test
        void should_return_validation_error_when_title_is_blank() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            RecordCreateRequest request = new RecordCreateRequest(
                    "",
                    "The Beatles",
                    1969,
                    Genre.ROCK,
                    "Local store",
                    LocalDate.now(),
                    RecordCondition.EXCELLENT,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/records", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_artist_is_blank() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            RecordCreateRequest request = new RecordCreateRequest(
                    "Abbey Road",
                    "",
                    1969,
                    Genre.ROCK,
                    "Local store",
                    LocalDate.now(),
                    RecordCondition.EXCELLENT,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/records", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_release_year_is_too_old() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            RecordCreateRequest request = new RecordCreateRequest(
                    "Abbey Road",
                    "The Beatles",
                    1800,
                    Genre.ROCK,
                    "Local store",
                    LocalDate.now(),
                    RecordCondition.EXCELLENT,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/records", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_release_year_is_too_far_in_future() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            RecordCreateRequest request = new RecordCreateRequest(
                    "Abbey Road",
                    "The Beatles",
                    2200,
                    Genre.ROCK,
                    "Local store",
                    LocalDate.now(),
                    RecordCondition.EXCELLENT,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/records", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_genre_is_null() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            RecordCreateRequest request = new RecordCreateRequest(
                    "Abbey Road",
                    "The Beatles",
                    1969,
                    null,
                    "Local store",
                    LocalDate.now(),
                    RecordCondition.EXCELLENT,
                    null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.POST("/api/records", request).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_updating_with_release_year_too_old() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            Long recordId = createTestRecordAndGetId(token);
            RecordUpdateRequest updateRequest = new RecordUpdateRequest(
                    null, null,
                    1800,
                    null, null, null, null, null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.PUT("/api/records/" + recordId, updateRequest).bearerAuth(token), ErrorResponse.class));

            // then
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.BAD_REQUEST.getCode());
        }

        @Test
        void should_return_validation_error_when_updating_with_release_year_too_new() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            Long recordId = createTestRecordAndGetId(token);
            RecordUpdateRequest updateRequest = new RecordUpdateRequest(
                    null, null,
                    2200,
                    null, null, null, null, null
            );

            // when
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().exchange(HttpRequest.PUT("/api/records/" + recordId, updateRequest).bearerAuth(token), ErrorResponse.class));

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
            String token = loginAndGetToken("katrin", "katrin123");
            RecordCreateRequest createRequest = new RecordCreateRequest(
                    "Abbey Road",
                    "The Beatles",
                    1969,
                    Genre.ROCK,
                    "Local record store",
                    LocalDate.of(2024, 1, 15),
                    RecordCondition.EXCELLENT,
                    "Classic album!"
            );

            // when - create
            HttpResponse<RecordResponse> createResponse = client.toBlocking()
                    .exchange(HttpRequest.POST("/api/records", createRequest).bearerAuth(token), RecordResponse.class);

            // then - create
            assertThat(createResponse.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
            Long recordId = createResponse.body().id();
            assertThat(recordId).isNotNull();

            // when - read
            RecordResponse readResponse = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/records/" + recordId).bearerAuth(token), RecordResponse.class);

            // then - read
            assertThat(readResponse.title()).isEqualTo("Abbey Road");
            assertThat(readResponse.artist()).isEqualTo("The Beatles");
            assertThat(readResponse.releaseYear()).isEqualTo(1969);
            assertThat(readResponse.genre()).isEqualTo(Genre.ROCK);

            // given - update
            RecordUpdateRequest updateRequest = new RecordUpdateRequest(
                    "Abbey Road (Remastered)",
                    null,
                    2019,
                    null, null, null,
                    RecordCondition.MINT,
                    "Remastered edition"
            );

            // when - update
            RecordResponse updateResponse = client.toBlocking()
                    .retrieve(HttpRequest.PUT("/api/records/" + recordId, updateRequest).bearerAuth(token), RecordResponse.class);

            // then - update
            assertThat(updateResponse.title()).isEqualTo("Abbey Road (Remastered)");
            assertThat(updateResponse.releaseYear()).isEqualTo(2019);
            assertThat(updateResponse.condition()).isEqualTo(RecordCondition.MINT);
            assertThat(updateResponse.artist()).isEqualTo("The Beatles");

            // when - delete
            HttpResponse<?> deleteResponse = client.toBlocking()
                    .exchange(HttpRequest.DELETE("/api/records/" + recordId).bearerAuth(token));

            // then - delete
            assertThat(deleteResponse.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());

            // when - verify deleted
            Throwable throwable = catchThrowable(() ->
                    client.toBlocking().retrieve(HttpRequest.GET("/api/records/" + recordId).bearerAuth(token), RecordResponse.class));

            // then - verify deleted
            assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
            HttpClientResponseException e = (HttpClientResponseException) throwable;
            assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
        }

        @Test
        void should_filter_and_search_records_correctly() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            createRecordWithDetails("Abbey Road", "The Beatles", Genre.ROCK, token);
            createRecordWithDetails("Let It Be", "The Beatles", Genre.ROCK, token);
            createRecordWithDetails("Kind of Blue", "Miles Davis", Genre.JAZZ, token);
            createRecordWithDetails("Blue Train", "John Coltrane", Genre.JAZZ, token);

            // when - filter by genre (rock)
            RecordResponse[] rockRecords = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/records/genre/ROCK").bearerAuth(token), RecordResponse[].class);

            // then - filter by genre (rock)
            assertThat(rockRecords).hasSize(2);
            assertThat(rockRecords).allMatch(r -> r.genre() == Genre.ROCK);

            // when - filter by genre (jazz)
            RecordResponse[] jazzRecords = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/records/genre/JAZZ").bearerAuth(token), RecordResponse[].class);

            // then - filter by genre (jazz)
            assertThat(jazzRecords).hasSize(2);
            assertThat(jazzRecords).allMatch(r -> r.genre() == Genre.JAZZ);

            // when - search by artist
            RecordResponse[] beatlesResults = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/records/search?q=beatles").bearerAuth(token), RecordResponse[].class);

            // then - search by artist
            assertThat(beatlesResults).hasSize(2);

            // when - search by title
            RecordResponse[] blueResults = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/records/search?q=blue").bearerAuth(token), RecordResponse[].class);

            // then - search by title
            assertThat(blueResults).hasSize(2);

            // when - get all (paginated)
            Map<String, Object> pageResponse = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/records").bearerAuth(token), Argument.of(Map.class, String.class, Object.class));

            // then - get all
            List<?> content = (List<?>) pageResponse.get("content");
            assertThat(content).hasSize(4);
            assertThat(pageResponse.get("totalElements")).isEqualTo(4);
        }

        @Test
        void should_handle_multiple_records_with_same_artist() {
            // given
            String token = loginAndGetToken("katrin", "katrin123");
            createRecordWithDetails("Abbey Road", "The Beatles", Genre.ROCK, token);
            createRecordWithDetails("Let It Be", "The Beatles", Genre.ROCK, token);
            createRecordWithDetails("Help!", "The Beatles", Genre.ROCK, token);
            createRecordWithDetails("Revolver", "The Beatles", Genre.ROCK, token);

            // when
            RecordResponse[] beatlesRecords = client.toBlocking()
                    .retrieve(HttpRequest.GET("/api/records/search?q=beatles").bearerAuth(token), RecordResponse[].class);

            // then
            assertThat(beatlesRecords).hasSize(4);
            assertThat(beatlesRecords).allMatch(r -> r.artist().equals("The Beatles"));
        }
    }

    private Long createTestRecordAndGetId(String token) {
        RecordCreateRequest request = new RecordCreateRequest(
                "Test Album",
                "Test Artist",
                1970,
                Genre.ROCK,
                "Test Store",
                LocalDate.now(),
                RecordCondition.EXCELLENT,
                null
        );

        RecordResponse response = client.toBlocking()
                .retrieve(HttpRequest.POST("/api/records", request).bearerAuth(token), RecordResponse.class);
        return response.id();
    }

    private void createRecordWithDetails(String title, String artist, Genre genre, String token) {
        RecordCreateRequest request = new RecordCreateRequest(
                title,
                artist,
                1969,
                genre,
                "Test Store",
                LocalDate.now(),
                RecordCondition.EXCELLENT,
                null
        );

        client.toBlocking().exchange(HttpRequest.POST("/api/records", request).bearerAuth(token), RecordResponse.class);
    }

    private String loginAndGetToken(String username, String password) {
        HttpResponse<BearerAccessRefreshToken> response = client.toBlocking()
                .exchange(HttpRequest.POST("/login", Map.of("username", username, "password", password)),
                        BearerAccessRefreshToken.class);
        return response.body().getAccessToken();
    }
}
