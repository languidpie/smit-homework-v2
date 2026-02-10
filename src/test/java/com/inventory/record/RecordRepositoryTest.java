package com.inventory.record;

/**
 * Repository tests for {@link RecordRepository}.
 * Tests database operations for vinyl records using H2 in-memory database.
 */

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
class RecordRepositoryTest {

    @Inject
    RecordRepository recordRepository;

    @BeforeEach
    void setUp() {
        recordRepository.deleteAll();
    }

    @Test
    void should_save_and_find_record() {
        // given
        VinylRecord record = createTestRecord("Abbey Road", "The Beatles");

        // when
        VinylRecord saved = recordRepository.save(record);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(recordRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void should_find_by_genre() {
        // given
        recordRepository.save(createTestRecordWithGenre("Rock Album 1", Genre.ROCK));
        recordRepository.save(createTestRecordWithGenre("Rock Album 2", Genre.ROCK));
        recordRepository.save(createTestRecordWithGenre("Jazz Album", Genre.JAZZ));

        // when
        List<VinylRecord> rockRecords = recordRepository.findByGenre(Genre.ROCK);

        // then
        assertThat(rockRecords).hasSize(2);
        assertThat(rockRecords).allMatch(r -> r.getGenre() == Genre.ROCK);
    }

    @Test
    void should_find_by_condition() {
        // given
        VinylRecord mintRecord = createTestRecord("Mint Album", "Artist");
        mintRecord.setCondition(RecordCondition.MINT);
        recordRepository.save(mintRecord);
        VinylRecord goodRecord = createTestRecord("Good Album", "Artist");
        goodRecord.setCondition(RecordCondition.GOOD);
        recordRepository.save(goodRecord);

        // when
        List<VinylRecord> mintRecords = recordRepository.findByCondition(RecordCondition.MINT);

        // then
        assertThat(mintRecords).hasSize(1);
        assertThat(mintRecords.get(0).getTitle()).isEqualTo("Mint Album");
    }

    @Test
    void should_search_by_title_or_artist() {
        // given
        recordRepository.save(createTestRecord("Abbey Road", "The Beatles"));
        recordRepository.save(createTestRecord("Let It Be", "The Beatles"));
        recordRepository.save(createTestRecord("Dark Side of the Moon", "Pink Floyd"));

        // when
        List<VinylRecord> results = recordRepository.searchByTitleOrArtist("beatles");

        // then
        assertThat(results).hasSize(2);
        assertThat(results).allMatch(r -> r.getArtist().contains("Beatles"));
    }

    @Test
    void should_count_by_genre() {
        // given
        recordRepository.save(createTestRecordWithGenre("Rock 1", Genre.ROCK));
        recordRepository.save(createTestRecordWithGenre("Rock 2", Genre.ROCK));
        recordRepository.save(createTestRecordWithGenre("Jazz 1", Genre.JAZZ));

        // when
        long rockCount = recordRepository.countByGenre(Genre.ROCK);
        long jazzCount = recordRepository.countByGenre(Genre.JAZZ);

        // then
        assertThat(rockCount).isEqualTo(2);
        assertThat(jazzCount).isEqualTo(1);
    }

    private VinylRecord createTestRecord(String title, String artist) {
        VinylRecord record = new VinylRecord();
        record.setTitle(title);
        record.setArtist(artist);
        record.setReleaseYear(1969);
        record.setGenre(Genre.ROCK);
        record.setPurchaseSource("Local store");
        record.setPurchaseDate(LocalDate.now());
        record.setCondition(RecordCondition.EXCELLENT);
        record.setNotes("Test notes");
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return record;
    }

    private VinylRecord createTestRecordWithGenre(String title, Genre genre) {
        VinylRecord record = createTestRecord(title, "Test Artist");
        record.setGenre(genre);
        return record;
    }
}
