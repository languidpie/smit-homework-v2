package ee.smit.inventory.part;

/**
 * Unit tests for {@link PartService}.
 * Tests business logic for bicycle parts using Mockito for repository mocking.
 */

import ee.smit.inventory.exception.NotFoundException;
import ee.smit.inventory.part.dto.PartCreateRequest;
import ee.smit.inventory.part.dto.PartUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PartServiceTest {

    @Mock
    private PartRepository partRepository;

    private PartService partService;

    @BeforeEach
    void setUp() {
        partService = new PartService(partRepository);
    }

    @Test
    void should_create_part() {
        // given
        PartCreateRequest request = new PartCreateRequest(
                "Shimano Brake",
                "High quality disc brake",
                PartType.BRAKE,
                "Garage",
                10,
                PartCondition.NEW,
                "Premium item"
        );
        Part savedPart = new Part();
        savedPart.setId(1L);
        savedPart.setName(request.name());
        given(partRepository.save(any(Part.class))).willReturn(savedPart);

        // when
        Part result = partService.create(request);

        // then
        ArgumentCaptor<Part> captor = ArgumentCaptor.forClass(Part.class);
        verify(partRepository).save(captor.capture());
        Part captured = captor.getValue();
        assertThat(captured.getName()).isEqualTo("Shimano Brake");
        assertThat(captured.getType()).isEqualTo(PartType.BRAKE);
        assertThat(captured.getQuantity()).isEqualTo(10);
        assertThat(captured.getCreatedAt()).isNotNull();
    }

    @Test
    void should_find_part_by_id() {
        // given
        Part part = createTestPart(1L, "Test Part");
        given(partRepository.findById(1L)).willReturn(Optional.of(part));

        // when
        Optional<Part> result = partService.findById(1L);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Test Part");
    }

    @Test
    void should_update_part() {
        // given
        Part existingPart = createTestPart(1L, "Old Name");
        given(partRepository.findById(1L)).willReturn(Optional.of(existingPart));
        given(partRepository.update(any(Part.class))).willAnswer(i -> i.getArgument(0));
        PartUpdateRequest request = new PartUpdateRequest(
                "New Name",
                null, null, null, null, null, null
        );

        // when
        Part result = partService.update(1L, request);

        // then
        assertThat(result.getName()).isEqualTo("New Name");
        assertThat(result.getUpdatedAt()).isNotNull();
    }

    @Test
    void should_throw_exception_when_updating_non_existent_part() {
        // given
        given(partRepository.findById(999L)).willReturn(Optional.empty());
        PartUpdateRequest request = new PartUpdateRequest(
                "New Name", null, null, null, null, null, null
        );

        // when
        Throwable throwable = catchThrowable(() -> partService.update(999L, request));

        // then
        assertThat(throwable)
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("999");
    }

    @Test
    void should_delete_part() {
        // given
        Part part = createTestPart(1L, "Test Part");
        given(partRepository.findById(1L)).willReturn(Optional.of(part));

        // when
        partService.delete(1L);

        // then
        verify(partRepository).delete(part);
    }

    @Test
    void should_throw_exception_when_deleting_non_existent_part() {
        // given
        given(partRepository.findById(999L)).willReturn(Optional.empty());

        // when
        Throwable throwable = catchThrowable(() -> partService.delete(999L));

        // then
        assertThat(throwable).isInstanceOf(NotFoundException.class);
    }

    private Part createTestPart(Long id, String name) {
        Part part = new Part();
        part.setId(id);
        part.setName(name);
        part.setDescription("Test description");
        part.setType(PartType.OTHER);
        part.setLocation("Garage");
        part.setQuantity(5);
        part.setCondition(PartCondition.NEW);
        part.setCreatedAt(LocalDateTime.now());
        part.setUpdatedAt(LocalDateTime.now());
        return part;
    }
}
