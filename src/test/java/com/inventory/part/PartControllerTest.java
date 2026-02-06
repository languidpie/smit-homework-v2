package com.inventory.part;

/**
 * Integration tests for {@link PartController}.
 * Tests HTTP endpoints for bicycle parts CRUD operations using Micronaut HTTP client.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */

import com.inventory.part.dto.PartCreateRequest;
import com.inventory.part.dto.PartResponse;
import com.inventory.part.dto.PartUpdateRequest;
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
                .exchange(HttpRequest.POST("/api/parts", request).basicAuth("mart", "mart123"), PartResponse.class);

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
        Long partId = createTestPartAndGetId();

        // when
        PartResponse response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/parts/" + partId).basicAuth("mart", "mart123"), PartResponse.class);

        // then
        assertThat(response.id()).isEqualTo(partId);
        assertThat(response.name()).isEqualTo("Test Part");
    }

    @Test
    void should_return_404_for_non_existent_part() {
        // given
        Long nonExistentId = 9999L;

        // when
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/parts/" + nonExistentId).basicAuth("mart", "mart123"), PartResponse.class));

        // then
        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
        HttpClientResponseException e = (HttpClientResponseException) throwable;
        assertThat(e.getStatus().getCode()).isEqualTo(HttpStatus.NOT_FOUND.getCode());
    }

    @Test
    void should_update_part() {
        // given
        Long partId = createTestPartAndGetId();
        PartUpdateRequest updateRequest = new PartUpdateRequest(
                "Updated Name",
                null, null, null, null, null, null
        );

        // when
        PartResponse response = client.toBlocking()
                .retrieve(HttpRequest.PUT("/api/parts/" + partId, updateRequest).basicAuth("mart", "mart123"), PartResponse.class);

        // then
        assertThat(response.name()).isEqualTo("Updated Name");
    }

    @Test
    void should_delete_part() {
        // given
        Long partId = createTestPartAndGetId();

        // when
        HttpResponse<?> response = client.toBlocking()
                .exchange(HttpRequest.DELETE("/api/parts/" + partId).basicAuth("mart", "mart123"));

        // then
        assertThat(response.status().getCode()).isEqualTo(HttpStatus.NO_CONTENT.getCode());
        Throwable throwable = catchThrowable(() ->
                client.toBlocking().retrieve(HttpRequest.GET("/api/parts/" + partId).basicAuth("mart", "mart123"), PartResponse.class));
        assertThat(throwable).isInstanceOf(HttpClientResponseException.class);
    }

    @Test
    void should_filter_by_type() {
        // given
        createTestPartWithType("Brake 1", PartType.BRAKE);
        createTestPartWithType("Brake 2", PartType.BRAKE);
        createTestPartWithType("Tire 1", PartType.TIRE);

        // when
        PartResponse[] response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/parts/type/BRAKE").basicAuth("mart", "mart123"), PartResponse[].class);

        // then
        assertThat(response).hasSize(2);
        assertThat(response).allMatch(p -> p.type() == PartType.BRAKE);
    }

    @Test
    void should_search_by_name() {
        // given
        createTestPartWithType("Shimano Brake", PartType.BRAKE);
        createTestPartWithType("SRAM Brake", PartType.BRAKE);

        // when
        PartResponse[] response = client.toBlocking()
                .retrieve(HttpRequest.GET("/api/parts/search?q=shimano").basicAuth("mart", "mart123"), PartResponse[].class);

        // then
        assertThat(response).hasSize(1);
        assertThat(response[0].name()).contains("Shimano");
    }

    private Long createTestPartAndGetId() {
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
                .retrieve(HttpRequest.POST("/api/parts", request).basicAuth("mart", "mart123"), PartResponse.class);
        return response.id();
    }

    private void createTestPartWithType(String name, PartType type) {
        PartCreateRequest request = new PartCreateRequest(
                name,
                "Test description",
                type,
                "Garage",
                5,
                PartCondition.NEW,
                null
        );

        client.toBlocking().exchange(HttpRequest.POST("/api/parts", request).basicAuth("mart", "mart123"), PartResponse.class);
    }
}
