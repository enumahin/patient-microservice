package com.alienworkspace.cdr.patient.controller;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierDto;
import com.alienworkspace.cdr.model.dto.patient.PatientProgramDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.AlreadyExistException;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.helpers.Constants;
import com.alienworkspace.cdr.patient.service.PatientIdentifierService;
import com.alienworkspace.cdr.patient.service.PatientProgramService;
import com.alienworkspace.cdr.patient.service.PatientService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing patient-related operations.
 * This controller provides endpoints for managing patients, their identifiers,
 * and program enrollments in the healthcare system.
 *
 * <p>
 * The controller supports:
 * - Patient CRUD operations
 * - Patient identifier management
 * - Program enrollment operations
 * - Patient search by various criteria
 *
 * <p>
 * All endpoints return appropriate HTTP status codes:
 * - 200: Successful operation
 * - 201: Resource created
 * - 204: Successful deletion
 * - 400: Invalid request
 * - 404: Resource not found
 * - 409: Conflict (e.g., duplicate identifier)
 *
 * <p>
 * Base URL: {@value Constants#PATIENT_BASE_URL}
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 */
@Tag(name = "Patient", description = "Patient APIs")
@RestController
@RequestMapping(Constants.PATIENT_BASE_URL)
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependency injection by Spring; safe to store")
public class PatientController {

    private static final Logger log = LoggerFactory.getLogger(PatientController.class);

    private final PatientService patientService;
    private final PatientIdentifierService patientIdentifierService;
    private final PatientProgramService patientProgramService;

    /**
     * Retrieves all patients in the system.
     * Returns a list of all non-voided patients with their basic information.
     *
     * @return ResponseEntity of List of PatientDto List of all patients with 200 OK status
     */
    @Operation(summary = "Get all patients", description = "Retrieves all patients")
    @ApiResponse(responseCode = "200", description = "Patients retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class)))
    @GetMapping
    public ResponseEntity<List<PatientDto>> getAllPatients() {
        return ResponseEntity.ok(patientService.getAllPatients());
    }

    /**
     * Retrieves a specific patient by their ID.
     * Returns detailed information about a single patient.
     *
     * @param id The unique identifier of the patient
     * @return ResponseEntity of PatientDto Patient information with 200 OK status
     * @throws ResourceNotFoundException if patient not found
     */
    @Operation(summary = "Get a patient by ID", description = "Retrieves a patient by their ID")
    @ApiResponse(responseCode = "200", description = "Patient retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<PatientDto> getPatient(@RequestHeader("X-cdr-correlation-id") String correlationId,
                                                 @PathVariable long id) {
        log.debug("Retrieving patient with ID: {} with correlationId: {}", id, correlationId);
        return ResponseEntity.ok(patientService.getPatient(id, correlationId));
    }

    /**
     * Creates a new patient record.
     * Registers a new patient in the system with their basic information.
     *
     * @param patientDto The patient information to create
     * @return ResponseEntity of PatientDto Created patient with 201 Created status
     * @throws IllegalArgumentException if the patient data is invalid
     */
    @Operation(summary = "Create a new patient", description = "Creates a new patient")
    @ApiResponse(responseCode = "201", description = "Patient created successfully",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class))})
    @PostMapping
    public ResponseEntity<PatientDto> createPatient(@RequestHeader("X-cdr-correlation-id") String correlationId,
                                                    @Valid @RequestBody PatientDto patientDto) {
        log.debug("Creating patient with correlationId: {}", correlationId);
        return ResponseEntity.status(HttpStatus.CREATED).body(patientService.createPatient(patientDto, correlationId));
    }

    /**
     * Updates an existing patient's information.
     * Modifies the information of a patient identified by their ID.
     *
     * @param id The unique identifier of the patient to update
     * @param patientDto The updated patient information
     * @return ResponseEntity of PatientDto Updated patient with 200 OK status
     * @throws ResourceNotFoundException if patient not found
     */
    @Operation(summary = "Update an existing patient", description = "Updates an existing patient")
    @ApiResponse(responseCode = "200", description = "Patient updated successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class)))
    @PutMapping("/{id}")
    public ResponseEntity<PatientDto> updatePatient(@PathVariable long id, @Valid @RequestBody PatientDto patientDto) {
        return ResponseEntity.ok(patientService.updatePatient(id, patientDto));
    }

    /**
     * Performs a soft delete of a patient record.
     * Marks a patient as voided rather than physically deleting the record.
     *
     * @param id The unique identifier of the patient to delete
     * @param recordVoidRequest The void request containing reason and metadata
     * @return ResponseEntity of Void Empty response with 204 No Content status
     * @throws ResourceNotFoundException if patient not found
     */
    @Operation(summary = "Delete a patient", description = "Deletes a patient")
    @ApiResponse(responseCode = "200", description = "Patient deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable int id, @RequestBody RecordVoidRequest recordVoidRequest) {
        patientService.deletePatient(id, recordVoidRequest);
        return ResponseEntity.noContent().build();
    }

    /**
     * Finds a patient by their identifier value.
     * Searches for a patient using any type of identifier (e.g., hospital number, national ID).
     *
     * @param identifier The identifier value to search for
     * @return ResponseEntity of PatientDto Found patient with 200 OK status
     * @throws ResourceNotFoundException if patient not found
     */
    @Operation(summary = "Get a patient by identifier", description = "Retrieves a patient by their identifier")
    @ApiResponse(responseCode = "200", description = "Patient retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class)))
    @GetMapping("/identifier/{identifier}")
    public ResponseEntity<PatientDto> getPatientByIdentifier(@PathVariable String identifier) {
        return ResponseEntity.ok(patientService.getPatientByIdentifier(identifier));
    }

    /**
     * Finds patients by identifier type.
     * Retrieves all patients who have a specific type of identifier.
     *
     * @param identifierType The ID of the identifier type to search for
     * @return ResponseEntity of List of PatientDto List of matching patients with 200 OK status
     */
    @Operation(summary = "Get a patient by identifier", description = "Retrieves a patient by their identifier")
    @ApiResponse(responseCode = "200", description = "Patient retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class)))
    @GetMapping("/identifier/type/{identifierType}")
    public ResponseEntity<List<PatientDto>> getPatientByIdentifierType(@PathVariable int identifierType) {
        return ResponseEntity.ok(patientService.getPatientsByIdentifierType(identifierType));
    }

    /**
     * Finds patients by program enrollment.
     * Retrieves all patients enrolled in a specific program.
     *
     * @param programId The ID of the program to search for
     * @return ResponseEntity of List of PatientDto List of enrolled patients with 200 OK status
     */
    @Operation(summary = "Get a patient by program", description = "Retrieves a patient by their program")
    @ApiResponse(responseCode = "200", description = "Patient retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class)))
    @GetMapping("/program/{programId}")
    public ResponseEntity<List<PatientDto>> getPatientsByProgram(@PathVariable int programId) {
        return ResponseEntity.ok(patientService.getPatientsByProgram(programId));
    }

    /**
     * Finds patients by program and active status.
     * Retrieves patients based on their program enrollment and the program's active status.
     *
     * @param programId The ID of the program to search for
     * @param status The active status of the program
     * @return ResponseEntity of List of PatientDto List of matching patients with 200 OK status
     */
    @Operation(summary = "Get a patient by program and status",
            description = "Retrieves a patient by their program and status")
    @ApiResponse(responseCode = "200", description = "Patient retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class)))
    @GetMapping("/program/{programId}/status/{status}")
    public ResponseEntity<List<PatientDto>> getPatientByProgramAndStatus(@PathVariable int programId,
                                                                         @PathVariable boolean status) {
        return ResponseEntity.ok(patientService.getPatientsByProgramAndStatus(programId, status));
    }

    /**
     * Retrieves all voided (soft-deleted) patients.
     * Returns a list of all patients that have been marked as voided.
     *
     * @return ResponseEntity of List of PatientDto voided patients with 200 OK status
     */
    @Operation(summary = "Get all patients both voided", description = "Retrieves all patients both voided")
    @ApiResponse(responseCode = "200", description = "Patients retrieved successfully",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PatientDto.class)))
    @GetMapping("/both-voided")
    public ResponseEntity<List<PatientDto>> getAllPatientsBothVoided() {
        return ResponseEntity.ok(patientService.getAllPatientsBothVoided());
    }

    /**
     * Enrolls a patient in a healthcare program.
     * Creates a new program enrollment record for a patient.
     *
     * @param patientId The ID of the patient to enroll
     * @param programId The ID of the program to enroll in
     * @param patientProgramDto The enrollment details
     * @return ResponseEntity Void Empty response with 200 OK status
     * @throws ResourceNotFoundException if patient or program not found
     * @throws AlreadyExistException if patient already enrolled
     */
    @Operation(summary = "Enroll a patient in a program", description = "Enrolls a patient in a program")
    @ApiResponse(responseCode = "200", description = "Patient enrolled in program successfully")
    @PostMapping("{patientId}/program/{programId}")
    public ResponseEntity<Void> enrollPatientInProgram(@PathVariable long patientId, @PathVariable int programId,
                                                       @Valid @RequestBody PatientProgramDto patientProgramDto) {
        patientProgramService.enrollPatientInProgram(patientId, programId, patientProgramDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates a patient's program enrollment.
     * Modifies the details of an existing program enrollment.
     *
     * @param patientId The ID of the enrolled patient
     * @param programId The ID of the program
     * @param patientProgramDto The updated enrollment details
     * @return ResponseEntity Void Empty response with 200 OK status
     * @throws ResourceNotFoundException if enrollment not found
     */
    @Operation(summary = "Update a patient's program enrollment",
            description = "Updates a patient's program enrollment")
    @ApiResponse(responseCode = "200", description = "Patient program enrollment updated successfully")
    @PutMapping("{patientId}/program/{programId}")
    public ResponseEntity<Void> updatePatientProgramEnrollment(
            @PathVariable long patientId,
            @PathVariable int programId,
            @Valid @RequestBody PatientProgramDto patientProgramDto) {
        patientProgramService.updateProgramEnrollment(patientId, programId, patientProgramDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Adds a new identifier to a patient.
     * Associates a new identifier (e.g., hospital number, national ID) with a patient.
     *
     * @param patientIdentifierDto The identifier information to add
     * @return ResponseEntity of Void Empty response with 200 OK status
     * @throws ResourceNotFoundException if patient not found
     * @throws AlreadyExistException if identifier already exists
     */
    @Operation(summary = "Add a patient identifier", description = "Adds a patient identifier")
    @ApiResponse(responseCode = "200", description = "Patient identifier added successfully")
    @PostMapping("/identifier")
    public ResponseEntity<Void> addPatientIdentifier(@Valid @RequestBody PatientIdentifierDto patientIdentifierDto) {
        patientIdentifierService.savePatientIdentifier(patientIdentifierDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Updates an existing patient identifier.
     * Modifies the details of a patient's identifier.
     *
     * @param id The ID of the identifier to update
     * @param patientIdentifierDto The updated identifier information
     * @return ResponseEntity of Void Empty response with 200 OK status
     * @throws ResourceNotFoundException if identifier not found
     */
    @Operation(summary = "Update a patient identifier", description = "Updates a patient identifier")
    @ApiResponse(responseCode = "200", description = "Patient identifier updated successfully")
    @PutMapping("/identifier/{id}")
    public ResponseEntity<Void> updatePatientIdentifier(@PathVariable int id,
                                                        @Valid @RequestBody PatientIdentifierDto patientIdentifierDto) {
        patientIdentifierService.updatePatientIdentifier(id, patientIdentifierDto);
        return ResponseEntity.ok().build();
    }

    /**
     * Deletes (voids) a patient identifier.
     * Performs a soft delete of a patient's identifier.
     *
     * @param id The ID of the identifier to delete
     * @param recordVoidRequest The void request containing reason and metadata
     * @return ResponseEntity of Void Empty response with 200 OK status
     * @throws ResourceNotFoundException if identifier not found
     */
    @Operation(summary = "Delete a patient identifier", description = "Deletes a patient identifier")
    @ApiResponse(responseCode = "200", description = "Patient identifier deleted successfully")
    @DeleteMapping("/identifier/{id}")
    public ResponseEntity<Void> deletePatientIdentifier(@PathVariable int id,
                                                        @RequestBody RecordVoidRequest recordVoidRequest) {
        patientIdentifierService.deletePatientIdentifier(id, recordVoidRequest);
        return ResponseEntity.ok().build();
    }
}
