package ee.smit.inventory.record;

import ee.smit.inventory.common.PageResponse;
import ee.smit.inventory.exception.ErrorResponse;
import ee.smit.inventory.exception.NotFoundException;
import ee.smit.inventory.exception.ValidationException;
import ee.smit.inventory.record.dto.RecordCreateRequest;
import ee.smit.inventory.record.dto.RecordResponse;
import ee.smit.inventory.record.dto.RecordUpdateRequest;
import io.micronaut.core.annotation.Nullable;
import io.micronaut.data.model.Pageable;
import io.micronaut.data.model.Sort;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.scheduling.TaskExecutors;
import io.micronaut.scheduling.annotation.ExecuteOn;
import io.micronaut.validation.Validated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.micronaut.security.annotation.Secured;

import ee.smit.inventory.security.Roles;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * REST controller for managing vinyl records.
 * Provides CRUD operations for Katrin's vinyl record collection.
 */
@Controller("/api/records")
@ExecuteOn(TaskExecutors.BLOCKING)
@Validated
@Secured({Roles.ROLE_RECORDS})
@Tag(name = "Vinyl Records", description = "Manage Katrin's vinyl record collection")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @Post
    @Operation(summary = "Create a new record", description = "Add a new vinyl record to the collection")
    @ApiResponse(responseCode = "201", description = "Record created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public HttpResponse<RecordResponse> create(@Body @Valid RecordCreateRequest request) {
        VinylRecord record = recordService.create(request);
        RecordResponse response = RecordResponse.fromEntity(record);
        return HttpResponse.created(response)
                .headers(headers -> headers.location(URI.create("/api/records/" + record.getId())));
    }

    @Get("/{id}")
    @Operation(summary = "Get a record by ID", description = "Retrieve a specific vinyl record by its ID")
    @ApiResponse(responseCode = "200", description = "Record found")
    @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public RecordResponse findById(@Parameter(description = "Record ID") @PathVariable Long id) {
        return recordService.findById(id)
                .map(RecordResponse::fromEntity)
                .orElseThrow(() -> new NotFoundException("VinylRecord", id));
    }

    private static final Set<String> SORTABLE_FIELDS = Set.of("title", "artist", "releaseYear", "genre", "condition");

    @Get
    @Operation(summary = "Get all records", description = "Retrieve all vinyl records in the collection with pagination")
    @ApiResponse(responseCode = "200", description = "Paginated list of records")
    @ApiResponse(responseCode = "400", description = "Invalid sort field", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PageResponse<RecordResponse> findAll(
            @Parameter(description = "Page number (0-based)") @QueryValue(defaultValue = "0") int page,
            @Parameter(description = "Page size") @QueryValue(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @Nullable @QueryValue String sort,
            @Parameter(description = "Sort direction (ASC or DESC)") @Nullable @QueryValue String direction) {
        if (page < 0) {
            throw new ValidationException("page", "Page number must be non-negative");
        }
        if (size < 1 || size > 100) {
            throw new ValidationException("size", "Page size must be between 1 and 100");
        }
        if (sort != null && !SORTABLE_FIELDS.contains(sort)) {
            throw new ValidationException("sort", "Invalid sort field: " + sort);
        }
        Pageable pageable;
        if (sort != null) {
            Sort.Order order = "DESC".equalsIgnoreCase(direction)
                    ? Sort.Order.desc(sort)
                    : Sort.Order.asc(sort);
            pageable = Pageable.from(page, size, Sort.of(order));
        } else {
            pageable = Pageable.from(page, size, Sort.of(Sort.Order.asc("id")));
        }
        return PageResponse.from(recordService.findAll(pageable), RecordResponse::fromEntity);
    }

    @Get("/genre/{genre}")
    @Operation(summary = "Get records by genre", description = "Filter vinyl records by their genre")
    @ApiResponse(responseCode = "200", description = "List of records matching the genre")
    public List<RecordResponse> findByGenre(
            @Parameter(description = "Genre (ROCK, JAZZ, BLUES, CLASSICAL, ELECTRONIC, POP, OTHER)") @PathVariable Genre genre) {
        return recordService.findByGenre(genre).stream()
                .map(RecordResponse::fromEntity)
                .toList();
    }

    @Get("/search")
    @Operation(summary = "Search records", description = "Search vinyl records by title or artist")
    @ApiResponse(responseCode = "200", description = "List of matching records")
    public List<RecordResponse> search(
            @Parameter(description = "Search query") @QueryValue String q) {
        if (q == null || q.isBlank()) {
            throw new ValidationException("q", "Search query must not be blank");
        }
        return recordService.search(q).stream()
                .map(RecordResponse::fromEntity)
                .toList();
    }

    @Put("/{id}")
    @Operation(summary = "Update a record", description = "Update an existing vinyl record")
    @ApiResponse(responseCode = "200", description = "Record updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public RecordResponse update(
            @Parameter(description = "Record ID") @PathVariable Long id,
            @Body @Valid RecordUpdateRequest request) {
        VinylRecord record = recordService.update(id, request);
        return RecordResponse.fromEntity(record);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a record", description = "Remove a vinyl record from the collection")
    @ApiResponse(responseCode = "204", description = "Record deleted successfully")
    @ApiResponse(responseCode = "404", description = "Record not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public void delete(@Parameter(description = "Record ID") @PathVariable Long id) {
        recordService.delete(id);
    }
}
