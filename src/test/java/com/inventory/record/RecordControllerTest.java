package com.inventory.record;

/**
 * Integration tests for {@link RecordController}.
 * Tests HTTP endpoints for vinyl records CRUD operations using Micronaut HTTP client.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */

import com.inventory.record.dto.RecordCreateRequest;
import com.inventory.record.dto.RecordResponse;
import com.inventory.record.dto.RecordUpdateRequest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.client.HttpClient;
import io.micronaut.http.client.annotation.Client;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

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
                .exchange(HttpRequest.POST("/api/records", request).basicAuth("katrin", "katrin123"), RecordResponse.class);

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
        Long recordId = createTestRecordAndGetId();

        // when
        RecordResponse response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/records/" + recordId).basicAuth("katrin", "katrin123"), RecordResponse.class);

        // then
        assertThat(response.id()).isEqualTo(recordId);
        assertThat(response.title()).isEqualTo("Test Album");
    }

    @Test
    void should_return_404_for_non_existent_record() {
        // given
        Long nonExistentId = 9999L;

        // when
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/records/" + nonExistentId).basicAuth("katrin", "katrin123"), RecordResponse.class));

        // then
        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void should_update_record() {
        // given
        Long recordId = createTestRecordAndGetId();
        RecordUpdateRequest updateRequest = new RecordUpdateRequest(
                "Updated Title",
                null, null, null, null, null, null, null
        );

        // when
        RecordResponse response = client.toBlocking()
                .retrieve(HttpRequest.PUT("/api/records/" + recordId, updateRequest).basicAuth("katrin", "katrin123"), RecordResponse.class);

        // then
        assertThat(response.title()).isEqualTo("Updated Title");
    }

    @Test
    void should_delete_record() {
        // given
        Long recordId = createTestRecordAndGetId();

        // when
        HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/api/records/" + recordId).basicAuth("katrin", "katrin123"));

        // then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/records/" + recordId).basicAuth("katrin", "katrin123"), RecordResponse.class));
        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
    }

    @Test
    void should_filter_by_genre() {
        // given
        createTestRecordWithGenre("Rock Album 1", Genre.ROCK);
        createTestRecordWithGenre("Rock Album 2", Genre.ROCK);
        createTestRecordWithGenre("Jazz Album", Genre.JAZZ);

        // when
        RecordResponse[] response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/records/genre/ROCK").basicAuth("katrin", "katrin123"), RecordResponse[].class);

        // then
        assertThat(response).hasSize(2);
        assertThat(response).allMatch(r -> r.genre() == Genre.ROCK);
    }

    @Test
    void should_search_by_title_or_artist() {
        // given
        createTestRecordWithArtist("Abbey Road", "The Beatles");
        createTestRecordWithArtist("Let It Be", "The Beatles");
        createTestRecordWithArtist("Dark Side", "Pink Floyd");

        // when
        RecordResponse[] response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/records/search?q=beatles").basicAuth("katrin", "katrin123"), RecordResponse[].class);

        // then
        assertThat(response).hasSize(2);
    }

    private Long createTestRecordAndGetId() {
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
                .retrieve(HttpRequest.POST("/api/records", request).basicAuth("katrin", "katrin123"), RecordResponse.class);
        return response.id();
    }

    private void createTestRecordWithGenre(String title, Genre genre) {
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

        client.toBlocking().exchange(HttpRequest.POST("/api/records", request).basicAuth("katrin", "katrin123"), RecordResponse.class);
    }

    private void createTestRecordWithArtist(String title, String artist) {
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

        client.toBlocking().exchange(HttpRequest.POST("/api/records", request).basicAuth("katrin", "katrin123"), RecordResponse.class);
    }
}
