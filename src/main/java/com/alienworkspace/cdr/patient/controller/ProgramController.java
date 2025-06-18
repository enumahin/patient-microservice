package com.alienworkspace.cdr.patient.controller;

import static com.alienworkspace.cdr.patient.helpers.Constants.PROGRAM_BASE_URL;

import com.alienworkspace.cdr.model.dto.patient.ProgramDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.service.ProgramService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing healthcare programs.
 * This controller provides endpoints for managing the various healthcare programs
 * that patients can be enrolled in.
 *
 * <p>
 * The controller supports:
 * - CRUD operations for programs
 * - Program activation/deactivation
 * - Program metadata management
 * - Program status queries
 *
 * <p>
 * All endpoints return appropriate HTTP status codes:
 * - 200: Successful operation
 * - 201: Resource created
 * - 204: Successful deletion
 * - 400: Invalid request
 * - 404: Resource not found
 * - 409: Conflict
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 */
@Tag(name = "Program", description = "Program API")
@RestController
@RequestMapping(PROGRAM_BASE_URL)
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependency injection by Spring; safe to store")
public class ProgramController {

    private final ProgramService programService;

    /**
     * Creates a new program.
     *
     * @param programDto the program to create
     * @return the created program
     */
    @Operation(summary = "Create a new program", description = "Creates a new program")
    @ApiResponses(
            {
                @ApiResponse(responseCode = "201", description = "Program created successfully",
                    content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProgramDto.class)))
            })
    @PostMapping
    public ResponseEntity<ProgramDto> createProgram(@Valid @RequestBody ProgramDto programDto) {
        return new ResponseEntity<>(programService.createProgram(programDto), HttpStatus.CREATED);
    }

    /**
     * Updates an existing program.
     *
     * @param id Program ID
     * @param programDto the program to update
     * @return the updated program
     */
    @Operation(summary = "Update an existing program", description = "Updates an existing program")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Program updated successfully",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = ProgramDto.class))),
        @ApiResponse(responseCode = "404", description = "Program not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ProgramDto> updateProgram(@PathVariable int id, @Valid @RequestBody ProgramDto programDto) {
        return ResponseEntity.ok(programService.updateProgram(id, programDto));
    }

    /**
     * Deletes a program.
     *
     * @param recordVoidRequest the request object containing the voided by user
     */
    @Operation(summary = "Delete a program", description = "Deletes a program")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Program deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Program not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable int id, @RequestBody RecordVoidRequest recordVoidRequest) {
        programService.deleteProgram(id, recordVoidRequest);
        return ResponseEntity.ok().build();
    }

    /**
     * Retrieves a program by its ID.
     *
     * @param id the ID of the program to retrieve
     * @return the program
     */
    @Operation(summary = "Get a program by ID", description = "Retrieves a program by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Program retrieved successfully",
                content = @Content(mediaType = "application/json",
                        schema = @Schema(implementation = ProgramDto.class))),
        @ApiResponse(responseCode = "404", description = "Program not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ProgramDto> getProgram(@PathVariable int id) {
        return ResponseEntity.ok(programService.getProgram(id));
    }

    /**
     * Retrieves all programs.
     *
     * @return a list of programs
     */
    @Operation(summary = "Get all programs", description = "Retrieves all programs")
    @ApiResponse(responseCode = "200", description = "Programs retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProgramDto.class)))
    @GetMapping
    public ResponseEntity<List<ProgramDto>> getAllPrograms() {
        return ResponseEntity.ok(programService.getAllPrograms());
    }
}
