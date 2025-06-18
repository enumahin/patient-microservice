package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.AlreadyExistException;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;

/**
 * Service interface for managing patient identifiers.
 * This service provides business logic for managing various types of patient
 * identifiers in the healthcare system.
 *
 * <p>
 * Key responsibilities:
 * - Identifier assignment and validation
 * - Preferred identifier management
 * - Identifier type coordination
 * - Uniqueness enforcement
 *
 * <p>
 * The service ensures:
 * - Proper identifier formatting
 * - Business rule enforcement
 * - Audit trail maintenance
 * - Data consistency
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 */
public interface PatientIdentifierService {

    /**
     * Creates a new patient identifier.
     * This method associates a new identifier with a patient, including information
     * about the identifier type, location, and preferred status.
     *
     * @param patientIdentifierDto The DTO containing the patient identifier information
     *                            including patient ID, identifier type, and value
     * @return PatientIdentifierDto The created patient identifier with generated ID and metadata
     * @throws ResourceNotFoundException if the patient or identifier type is not found
     * @throws AlreadyExistException if a preferred identifier of the same type already exists
     * @throws IllegalArgumentException if the identifier data is invalid
     */
    PatientIdentifierDto savePatientIdentifier(PatientIdentifierDto patientIdentifierDto);

    /**
     * Updates an existing patient identifier.
     * This method allows modification of an identifier's properties, including its
     * preferred status and location. The identifier value itself cannot be modified
     * for audit trail purposes.
     *
     * @param id The ID of the patient identifier to update
     * @param patientIdentifierDto The DTO containing the updated identifier information
     * @return PatientIdentifierDto The updated patient identifier
     * @throws ResourceNotFoundException if the identifier is not found
     * @throws AlreadyExistException if attempting to set as preferred when another preferred exists
     * @throws IllegalArgumentException if the update data is invalid
     */
    PatientIdentifierDto updatePatientIdentifier(long id, PatientIdentifierDto patientIdentifierDto);

    /**
     * Performs a soft delete (void) of a patient identifier.
     * This method marks an identifier as voided rather than physically deleting it,
     * maintaining an audit trail of identifier history. The void operation includes
     * recording who performed the void, when it was done, and the reason.
     *
     * @param id The ID of the patient identifier to void
     * @param recordVoidRequest The request containing void reason and metadata
     * @throws ResourceNotFoundException if the identifier is not found
     * @throws IllegalStateException if the void operation fails
     */
    void deletePatientIdentifier(long id, RecordVoidRequest recordVoidRequest);
}
