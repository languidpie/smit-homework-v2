package ee.smit.inventory.record;

/**
 * Integration tests for {@link RecordController}.
 * Tests HTTP endpoints for vinyl records CRUD operations using Micronaut HTTP client.
 */

import ee.smit.inventory.record.dto.RecordCreateRequest;
import ee.smit.inventory.record.dto.RecordResponse;
import ee.smit.inventory.record.dto.RecordUpdateRequest;
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

import java.time.LocalDate;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

@MicronautTest
class RecordControllerTest {

    @Inject
    @Client("/")
    HttpClient client;

    @Inject
    RecordRepository recordRepository;

    @BeforeEach
    void setUp() {
        recordRepository.deleteAll();
    }

    @Test
    void should_create_record() {
        // given
        String token = loginAndGetToken("katrin", "katrin123");
        RecordCreateRequest request = new RecordCreateRequest(
                "Abbey Road",
                "The Beatles",
                1969,
                Genre.ROCK,
                "Local store",
                LocalDate.of(2024, 1, 15),
                RecordCondition.EXCELLENT,
                "Great album!"
        );

        // when
        HttpResponse<RecordResponse> response = client.toBlocking()
                .exchange(HttpRequest.POST("/api/records", request).bearerAuth(token), RecordResponse.class);

        // then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.CREATED.getCode());
        assertThat(response.body()).isNotNull();
        assertThat(response.body().id()).isNotNull();
        assertThat(response.body().title()).isEqualTo("Abbey Road");
        assertThat(response.header("Location")).contains("/api/records/");
    }

    @Test
    void should_get_record_by_id() {
        // given
        String token = loginAndGetToken("katrin", "katrin123");
        Long recordId = createTestRecordAndGetId(token);

        // when
        RecordResponse response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/records/" + recordId).bearerAuth(token), RecordResponse.class);

        // then
        assertThat(response.id()).isEqualTo(recordId);
        assertThat(response.title()).isEqualTo("Test Album");
    }

    @Test
    void should_return_404_for_non_existent_record() {
        // given
        String token = loginAndGetToken("katrin", "katrin123");
        Long nonExistentId = 9999L;

        // when
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/records/" + nonExistentId).bearerAuth(token), RecordResponse.class));

        // then
        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void should_update_record() {
        // given
        String token = loginAndGetToken("katrin", "katrin123");
        Long recordId = createTestRecordAndGetId(token);
        RecordUpdateRequest updateRequest = new RecordUpdateRequest(
                "Updated Title",
                null, null, null, null, null, null, null
        );

        // when
        RecordResponse response = client.toBlocking()
                .retrieve(HttpRequest.PUT("/api/records/" + recordId, updateRequest).bearerAuth(token), RecordResponse.class);

        // then
        assertThat(response.title()).isEqualTo("Updated Title");
    }

    @Test
    void should_delete_record() {
        // given
        String token = loginAndGetToken("katrin", "katrin123");
        Long recordId = createTestRecordAndGetId(token);

        // when
        HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/api/records/" + recordId).bearerAuth(token));

        // then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/records/" + recordId).bearerAuth(token), RecordResponse.class));
        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
    }

    @Test
    void should_filter_by_genre() {
        // given
        String token = loginAndGetToken("katrin", "katrin123");
        createTestRecordWithGenre("Rock Album 1", Genre.ROCK, token);
        createTestRecordWithGenre("Rock Album 2", Genre.ROCK, token);
        createTestRecordWithGenre("Jazz Album", Genre.JAZZ, token);

        // when
        RecordResponse[] response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/records/genre/ROCK").bearerAuth(token), RecordResponse[].class);

        // then
        assertThat(response).hasSize(2);
        assertThat(response).allMatch(r -> r.genre() == Genre.ROCK);
    }

    @Test
    void should_search_by_title_or_artist() {
        // given
        String token = loginAndGetToken("katrin", "katrin123");
        createTestRecordWithArtist("Abbey Road", "The Beatles", token);
        createTestRecordWithArtist("Let It Be", "The Beatles", token);
        createTestRecordWithArtist("Dark Side", "Pink Floyd", token);

        // when
        RecordResponse[] response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/records/search?q=beatles").bearerAuth(token), RecordResponse[].class);

        // then
        assertThat(response).hasSize(2);
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

    private void createTestRecordWithGenre(String title, Genre genre, String token) {
        RecordCreateRequest request = new RecordCreateRequest(
                title,
                "Test Artist",
                1970,
                genre,
                "Test Store",
                LocalDate.now(),
                RecordCondition.EXCELLENT,
                null
        );

        client.toBlocking().exchange(HttpRequest.POST("/api/records", request).bearerAuth(token), RecordResponse.class);
    }

    private void createTestRecordWithArtist(String title, String artist, String token) {
        RecordCreateRequest request = new RecordCreateRequest(
                title,
                artist,
                1970,
                Genre.ROCK,
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
