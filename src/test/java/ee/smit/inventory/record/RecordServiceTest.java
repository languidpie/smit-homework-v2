package ee.smit.inventory.record;

/**
 * Unit tests for {@link RecordService}.
 * Tests business logic for vinyl records using Mockito for repository mocking.
 */

import ee.smit.inventory.exception.NotFoundException;
import ee.smit.inventory.record.dto.RecordCreateRequest;
import ee.smit.inventory.record.dto.RecordUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class RecordServiceTest {

    @Mock
    private RecordRepository recordRepository;

    private RecordService recordService;

    @BeforeEach
    void setUp() {
        recordService = new RecordService(recordRepository);
    }

    @Test
    void should_create_record() {
        // given
        RecordCreateRequest request = new RecordCreateRequest(
                "Abbey Road",
                "The Beatles",
                1969,
                Genre.ROCK,
                "Local record store",
                LocalDate.of(2024, 1, 15),
                RecordCondition.EXCELLENT,
                "Great find!"
        );
        VinylRecord savedRecord = new VinylRecord();
        savedRecord.setId(1L);
        savedRecord.setTitle(request.title());
        given(recordRepository.save(any(VinylRecord.class))).willReturn(savedRecord);

        // when
        VinylRecord result = recordService.create(request);

        // then
        ArgumentCaptor<VinylRecord> captor = ArgumentCaptor.forClass(VinylRecord.class);
        verify(recordRepository).save(captor.capture());
        VinylRecord captured = captor.getValue();
        assertThat(captured.getTitle()).isEqualTo("Abbey Road");
        assertThat(captured.getArtist()).isEqualTo("The Beatles");
        assertThat(captured.getReleaseYear()).isEqualTo(1969);
        assertThat(captured.getCreatedAt()).isNotNull();
    }

    @Test
    void should_find_record_by_id() {
        // given
        VinylRecord record = createTestRecord(1L, "Test Album");
        given(recordRepository.findById(1L)).willReturn(Optional.of(record));

        // when
        Optional<VinylRecord> result = recordService.findById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getTitle()).isEqualTo("Test Album");
    }

    @Test
    void should_update_record() {
        // given
        VinylRecord existingRecord = createTestRecord(1L, "Old Title");
        given(recordRepository.findById(1L)).willReturn(Optional.of(existingRecord));
        given(recordRepository.update(any(VinylRecord.class))).willAnswer(i -> i.getArgument(0));
        RecordUpdateRequest request = new RecordUpdateRequest(
                "New Title",
                null, null, null, null, null, null, null
        );

        // when
        VinylRecord result = recordService.update(1L, request);

        // then
        assertThat(result.getTitle()).isEqualTo("New Title");
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_updating_non_existent_record() {
        // given
        given(recordRepository.findById(999L)).willReturn(Optional.empty());
        RecordUpdateRequest request = new RecordUpdateRequest(
                "New Title", null, null, null, null, null, null, null
        );

        // when
        Throwable throwable = catchThrowable(() -> recordService.update(999L, request));

        // then
        assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void should_delete_record() {
        // given
        given(recordRepository.existsById(1L)).willReturn(true);

        // when
        recordService.delete(1L);

        // then
        verify(recordRepository).deleteById(1L);
    }

    @Test
    void should_throw_exception_when_deleting_non_existent_record() {
        // given
        given(recordRepository.existsById(999L)).willReturn(false);

        // when
        Throwable throwable = catchThrowable(() -> recordService.delete(999L));

        // then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    private VinylRecord createTestRecord(Long id, String title) {
        VinylRecord record = new VinylRecord();
        record.setId(id);
        record.setTitle(title);
        record.setArtist("Test Artist");
        record.setReleaseYear(1969);
        record.setGenre(Genre.ROCK);
        record.setPurchaseSource("Test Store");
        record.setPurchaseDate(LocalDate.now());
        record.setCondition(RecordCondition.EXCELLENT);
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return record;
    }
}
