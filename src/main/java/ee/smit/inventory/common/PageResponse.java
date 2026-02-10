package ee.smit.inventory.common;

import io.micronaut.data.model.Page;
import io.micronaut.serde.annotation.Serdeable;

import java.util.List;
import java.util.function.Function;

@Serdeable
public record PageResponse<T>(
        List<T> content,
        int pageNumber,
        int pageSize,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
) {
    public static <E, T> PageResponse<T> from(Page<E> page, Function<E, T> mapper) {
        return new PageResponse<>(
                page.getContent().stream().map(mapper).toList(),
                page.getPageNumber(),
                page.getSize(),
                page.getTotalSize(),
                page.getTotalPages(),
                page.getPageNumber() == 0,
                page.getPageNumber() >= page.getTotalPages() - 1
        );
    }
}
