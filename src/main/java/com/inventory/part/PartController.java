package com.inventory.part;

import com.inventory.exception.ErrorResponse;
import com.inventory.exception.NotFoundException;
import com.inventory.part.dto.PartCreateRequest;
import com.inventory.part.dto.PartResponse;
import com.inventory.part.dto.PartUpdateRequest;
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

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;

/**
 * REST controller for managing bicycle parts.
 * Provides CRUD operations for Mart's bicycle parts inventory.
 *
 * @author Mari-Liis
 * Date: 04.02.2026
 */
@Controller("/api/parts")
@ExecuteOn(TaskExecutors.BLOCKING)
@Validated
@Tag(name = "Bicycle Parts", description = "Manage Mart's bicycle parts inventory")
public class PartController {

    private final PartService partService;

    public PartController(PartService partService) {
        this.partService = partService;
    }

    @Post
    @Status(HttpStatus.CREATED)
    @Operation(summary = "Create a new part", description = "Add a new bicycle part to the inventory")
    @ApiResponse(responseCode = "201", description = "Part created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public HttpResponse<PartResponse> create(@Body @Valid PartCreateRequest request) {
        Part part = partService.create(request);
        PartResponse response = PartResponse.fromEntity(part);
        return HttpResponse.created(response)
                .headers(headers -> headers.location(URI.create("/api/parts/" + part.getId())));
    }

    @Get("/{id}")
    @Operation(summary = "Get a part by ID", description = "Retrieve a specific bicycle part by its ID")
    @ApiResponse(responseCode = "200", description = "Part found")
    @ApiResponse(responseCode = "404", description = "Part not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PartResponse findById(@Parameter(description = "Part ID") @PathVariable Long id) {
        return partService.findById(id)
                .map(PartResponse::fromEntity)
                .orElseThrow(() -> new NotFoundException("Part", id));
    }

    @Get
    @Operation(summary = "Get all parts", description = "Retrieve all bicycle parts in the inventory")
    @ApiResponse(responseCode = "200", description = "List of all parts")
    public List<PartResponse> findAll() {
        return partService.findAll().stream()
                .map(PartResponse::fromEntity)
                .toList();
    }

    @Get("/type/{type}")
    @Operation(summary = "Get parts by type", description = "Filter bicycle parts by their type")
    @ApiResponse(responseCode = "200", description = "List of parts matching the type")
    public List<PartResponse> findByType(
            @Parameter(description = "Part type (FRAME, BRAKE, TIRE, PUMP, OTHER)") @PathVariable PartType type) {
        return partService.findByType(type).stream()
                .map(PartResponse::fromEntity)
                .toList();
    }

    @Get("/search")
    @Operation(summary = "Search parts", description = "Search bicycle parts by name")
    @ApiResponse(responseCode = "200", description = "List of matching parts")
    public List<PartResponse> searchByName(
            @Parameter(description = "Search query") @QueryValue String q) {
        return partService.searchByName(q).stream()
                .map(PartResponse::fromEntity)
                .toList();
    }

    @Put("/{id}")
    @Operation(summary = "Update a part", description = "Update an existing bicycle part")
    @ApiResponse(responseCode = "200", description = "Part updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid input", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    @ApiResponse(responseCode = "404", description = "Part not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public PartResponse update(
            @Parameter(description = "Part ID") @PathVariable Long id,
            @Body @Valid PartUpdateRequest request) {
        Part part = partService.update(id, request);
        return PartResponse.fromEntity(part);
    }

    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a part", description = "Remove a bicycle part from the inventory")
    @ApiResponse(responseCode = "204", description = "Part deleted successfully")
    @ApiResponse(responseCode = "404", description = "Part not found", content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    public void delete(@Parameter(description = "Part ID") @PathVariable Long id) {
        partService.delete(id);
    }
}
