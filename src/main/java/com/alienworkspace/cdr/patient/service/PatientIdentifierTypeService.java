package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierTypeDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import java.util.List;

/**
 * Service interface for managing patient identifier types.
 * This service provides business logic for managing the different types of
 * identifiers that can be assigned to patients.
 *
 * <p>
 * Key responsibilities:
 * - Identifier type registration
 * - Format validation rules
 * - Type metadata management
 * - Status tracking
 *
 * <p>
 * The service ensures:
 * - Proper type configuration
 * - Business rule enforcement
 * - Audit trail maintenance
 * - Data consistency
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 */
public interface PatientIdentifierTypeService {

    /**
     * Creates a new patient identifier type in the system.
     *
     * @param patientIdentifierDto The DTO containing the patient identifier type information to be created
     * @return PatientIdentifierTypeDto The created patient identifier type with generated ID and metadata
     * @throws IllegalArgumentException if the required fields are missing or invalid
     */
    PatientIdentifierTypeDto createPatientIdentifierType(PatientIdentifierTypeDto patientIdentifierDto);

    /**
     * Updates an existing patient identifier type.
     *
     * @param id The ID of the patient identifier type to update
     * @param patientIdentifierDto The DTO containing the updated patient identifier type information
     * @return PatientIdentifierTypeDto The updated patient identifier type
     * @throws ResourceNotFoundException if the identifier type is not found
     * @throws IllegalArgumentException if the update data is invalid
     */
    PatientIdentifierTypeDto updatePatientIdentifierType(int id, PatientIdentifierTypeDto patientIdentifierDto);

    /**
     * Marks a patient identifier type as voided (soft delete).
     *
     * @param id The ID of the patient identifier type to delete
     * @param recordVoidRequest The request containing the reason for voiding and other metadata
     * @throws ResourceNotFoundException if the identifier type is not found
     */
    void deletePatientIdentifierType(int id, RecordVoidRequest recordVoidRequest);

    /**
     * Retrieves a specific patient identifier type by its ID.
     *
     * @param id The ID of the patient identifier type to retrieve
     * @return PatientIdentifierTypeDto The requested patient identifier type
     * @throws ResourceNotFoundException if the identifier type is not found
     */
    PatientIdentifierTypeDto getPatientIdentifierType(int id);

    /**
     * Retrieves all active (non-voided) patient identifier types.
     *
     * @return List of PatientIdentifierTypeDto A list of all active patient identifier types
     */
    List<PatientIdentifierTypeDto> getAllPatientIdentifierTypes();

    /**
     * Retrieves all patient identifier types including voided ones.
     *
     * @return List of PatientIdentifierTypeDto A list of all patient identifier types, both active and voided
     */
    List<PatientIdentifierTypeDto> getAllPatientIdentifierTypesBothVoided();
}
