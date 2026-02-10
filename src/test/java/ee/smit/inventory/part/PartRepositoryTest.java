package ee.smit.inventory.part;

/**
 * Repository tests for {@link PartRepository}.
 * Tests database operations for bicycle parts using Testcontainers PostgreSQL.
 */

import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@MicronautTest
class PartRepositoryTest {

    @Inject
    PartRepository partRepository;

    @BeforeEach
    void setUp() {
        partRepository.deleteAll();
    }

    @Test
    void should_save_and_find_part() {
        // given
        Part part = createTestPart("Shimano Brake", PartType.BRAKE);

        // when
        Part saved = partRepository.save(part);

        // then
        assertThat(saved.getId()).isNotNull();
        assertThat(partRepository.findById(saved.getId())).isPresent();
    }

    @Test
    void should_find_by_type() {
        // given
        partRepository.save(createTestPart("Frame 1", PartType.FRAME));
        partRepository.save(createTestPart("Frame 2", PartType.FRAME));
        partRepository.save(createTestPart("Tire 1", PartType.TIRE));

        // when
        List<Part> frames = partRepository.findByType(PartType.FRAME);

        // then
        assertThat(frames).hasSize(2);
        assertThat(frames).allMatch(p -> p.getType() == PartType.FRAME);
    }

    @Test
    void should_find_by_condition() {
        // given
        Part newPart = createTestPart("New Part", PartType.OTHER);
        newPart.setCondition(PartCondition.NEW);
        partRepository.save(newPart);
        Part usedPart = createTestPart("Used Part", PartType.OTHER);
        usedPart.setCondition(PartCondition.GOOD);
        partRepository.save(usedPart);

        // when
        List<Part> newParts = partRepository.findByCondition(PartCondition.NEW);

        // then
        assertThat(newParts).hasSize(1);
        assertThat(newParts.get(0).getName()).isEqualTo("New Part");
    }

    @Test
    void should_search_by_name_or_description() {
        // given
        partRepository.save(createTestPart("Shimano Ultegra Brake", PartType.BRAKE));
        partRepository.save(createTestPart("SRAM Red Brake", PartType.BRAKE));

        // when - search by name
        List<Part> nameResults = partRepository.searchByNameOrDescription("shimano");

        // then
        assertThat(nameResults).hasSize(1);
        assertThat(nameResults.get(0).getName()).contains("Shimano");

        // when - search by description
        List<Part> descResults = partRepository.searchByNameOrDescription("description");

        // then - all parts have "Test description"
        assertThat(descResults).hasSize(2);
    }

    @Test
    void should_count_by_type() {
        // given
        partRepository.save(createTestPart("Frame 1", PartType.FRAME));
        partRepository.save(createTestPart("Frame 2", PartType.FRAME));
        partRepository.save(createTestPart("Tire 1", PartType.TIRE));

        // when
        long frameCount = partRepository.countByType(PartType.FRAME);
        long tireCount = partRepository.countByType(PartType.TIRE);

        // then
        assertThat(frameCount).isEqualTo(2);
        assertThat(tireCount).isEqualTo(1);
    }

    private Part createTestPart(String name, PartType type) {
        Part part = new Part();
        part.setName(name);
        part.setDescription("Test description");
        part.setType(type);
        part.setLocation("Garage");
        part.setQuantity(5);
        part.setCondition(PartCondition.NEW);
        part.setNotes("Test notes");
        part.setCreatedAt(LocalDateTime.now());
        part.setUpdatedAt(LocalDateTime.now());
        return part;
    }
}
