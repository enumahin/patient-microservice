package com.alienworkspace.cdr.patient.controller;

import static com.alienworkspace.cdr.patient.helpers.Constants.PATIENT_IDENTIFIER_TYPE_BASE_URL;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierTypeDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.service.PatientIdentifierTypeService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing patient identifier types.
 * This controller provides endpoints for managing the different types of identifiers
 * that can be assigned to patients in the healthcare system.
 *
 * <p>
 * The controller supports:
 * - CRUD operations for identifier types
 * - Activation/deactivation of identifier types
 * - Metadata management
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
@Tag(name = "Patient Identifier", description = "Patient Identifier API")   
@RestController
@RequestMapping(PATIENT_IDENTIFIER_TYPE_BASE_URL)
@AllArgsConstructor
@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Dependency injection by Spring; safe to store")
public class PatientIdentifierTypeController {

    private PatientIdentifierTypeService patientIdentifierTypeService;

    /**
     * Creates a new patient identifier type.
     *
     * @param patientIdentifierTypeDto The patient identifier type to create
     * @return ResponseEntity of PatientIdentifierTypeDto The created patient identifier type
     */
    @Operation(summary = "Create a new patient identifier type", description = "Creates a new patient identifier type")
    @ApiResponse(responseCode = "200", description = "Patient identifier type created successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientIdentifierTypeDto.class)))
    @PostMapping
    public ResponseEntity<PatientIdentifierTypeDto> createPatientIdentifierType(
            @Valid @RequestBody PatientIdentifierTypeDto patientIdentifierTypeDto) {
        return ResponseEntity.ok(patientIdentifierTypeService.createPatientIdentifierType(patientIdentifierTypeDto));
    }

    /**
     * Updates an existing patient identifier type.
     *
     * @param id The ID of the patient identifier type to update
     * @param patientIdentifierTypeDto The updated patient identifier type
     * @return ResponseEntity of PatientIdentifierTypeDto The updated patient identifier type
     */
    @Operation(summary = "Update an existing patient identifier type",
            description = "Updates an existing patient identifier type")
    @ApiResponse(responseCode = "200", description = "Patient identifier type updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = PatientIdentifierTypeDto.class)))
    @PutMapping("/{id}")
    public ResponseEntity<PatientIdentifierTypeDto> updatePatientIdentifierType(
            @PathVariable int id,
            @Valid @RequestBody PatientIdentifierTypeDto patientIdentifierTypeDto) {
        return ResponseEntity.ok(
                patientIdentifierTypeService.updatePatientIdentifierType(id, patientIdentifierTypeDto));
    }

    /**
     * Deletes a patient identifier type.
     *
     * @param id The ID of the patient identifier type to delete
     * @param recordVoidRequest The request containing the voided by user
     * @return ResponseEntity of Void Empty response with 204 No Content status
     */
    @Operation(summary = "Delete a patient identifier type", description = "Deletes a patient identifier type")
    @ApiResponse(responseCode = "200", description = "Patient identifier type deleted successfully")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatientIdentifierType(@PathVariable int id,
                                                            @RequestBody RecordVoidRequest recordVoidRequest) {
        patientIdentifierTypeService.deletePatientIdentifierType(id, recordVoidRequest);
        return ResponseEntity.noContent().build();
    }
}
