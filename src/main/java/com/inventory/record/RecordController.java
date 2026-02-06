package com.inventory.record;

import com.inventory.common.PageResponse;
import com.inventory.exception.ErrorResponse;
import com.inventory.exception.NotFoundException;
import com.inventory.record.dto.RecordCreateRequest;
import com.inventory.record.dto.RecordResponse;
import com.inventory.record.dto.RecordUpdateRequest;
import io.micronaut.data.model.Pageable;
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

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * REST controller for managing vinyl records.
 * Provides CRUD operations for Katrin's vinyl record collection.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Controller("/api/records")
@ExecuteOn(TaskExecutors.BLOCKING)
@Validated
@Secured({"ROLE_RECORDS"})
@Tag(name = "Vinyl Records", description = "Manage Katrin's vinyl record collection")
public class RecordController {

    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @Post
    @Status(HttpStatus.CREATED)
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

    @Get
    @Operation(summary = "Get all records", description = "Retrieve all vinyl records in the collection with pagination")
    @ApiResponse(responseCode = "200", description = "Paginated list of records")
    public PageResponse<RecordResponse> findAll(
            @Parameter(description = "Page number (0-based)") @QueryValue(defaultValue = "0") int page,
            @Parameter(description = "Page size") @QueryValue(defaultValue = "20") int size) {
        Pageable pageable = Pageable.from(page, size);
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
