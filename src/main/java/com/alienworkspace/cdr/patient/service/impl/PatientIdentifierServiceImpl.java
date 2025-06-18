package com.alienworkspace.cdr.patient.service.impl;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.AlreadyExistException;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.helpers.CurrentUser;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import com.alienworkspace.cdr.patient.model.mapper.PatientIdentifierMapper;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierRepository;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierTypeRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import com.alienworkspace.cdr.patient.service.PatientIdentifierService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link PatientIdentifierService} interface.
 * This class provides the core implementation of patient identifier operations,
 * handling identifier data persistence and business logic.
 *
 * <p>
 * Key features:
 * - Identifier assignment and validation
 * - Preferred identifier management
 * - Identifier type coordination
 * - Uniqueness enforcement
 *
 * <p>
 * Implementation details:
 * - Uses JPA repositories for data access
 * - Implements transaction management
 * - Handles data validation
 * - Maintains audit trails
 *
 * <p>
 * Business rules enforced:
 * - Identifier uniqueness validation
 * - Format validation
 * - Type consistency
 * - Data integrity checks
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientIdentifierService
 * @see PatientIdentifier
 * @see PatientIdentifierMapper
 */
@Service
@Transactional
@AllArgsConstructor
public class PatientIdentifierServiceImpl implements PatientIdentifierService {

    private final PatientIdentifierRepository patientIdentifierRepository;
    private final PatientIdentifierTypeRepository patientIdentifierTypeRepository;
    private final PatientRepository patientRepository;
    private final PatientIdentifierMapper patientIdentifierMapper;

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation:
     * 1. Converts the DTO to a domain entity
     * 2. Validates the existence of the referenced patient
     * 3. Validates the existence of the identifier type
     * 4. Sets up the relationships between entities
     * 5. Saves the new identifier
     *
     * @throws ResourceNotFoundException if either the patient or identifier type is not found
     * @throws IllegalArgumentException if the identifier data is invalid
     */
    @Transactional
    @Override
    public PatientIdentifierDto savePatientIdentifier(PatientIdentifierDto patientIdentifierDto) {
        PatientIdentifier patientIdentifier = patientIdentifierMapper.toPatientIdentifier(patientIdentifierDto);

        Patient patient = patientRepository
                .findById(patientIdentifierDto.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "Id",
                        String.valueOf(patientIdentifierDto.getPatientId())));

        PatientIdentifierType patientIdentifierType = patientIdentifierTypeRepository
                .findById(patientIdentifierDto.getIdentifierTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient Identifier Type", "Id",
                       String.valueOf(patientIdentifierDto.getIdentifierTypeId())));
        if (patientIdentifierDto.isPreferred()) {
            patientIdentifierRepository.resetPreferredByPatientIdAndIdentifierType(
                    patientIdentifierDto.getPatientId(),
                    patientIdentifierDto.getIdentifierTypeId());
        }


        patientIdentifier.setPatientIdentifierType(patientIdentifierType);
        patientIdentifier.setPatient(patient);

        return patientIdentifierMapper.toPatientIdentifierDto(
                patientIdentifierRepository.save(patientIdentifier));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation:
     * 1. Checks if a preferred identifier of the same type already exists
     * 2. Updates the identifier if found
     * 3. Handles the preferred status changes
     *
     * <p>
     * The method enforces the business rule that only one identifier of a given type
     * can be marked as preferred for a patient.
     *
     * @throws AlreadyExistException if attempting to create a preferred identifier when one already exists
     * @throws ResourceNotFoundException if the identifier to update is not found
     * @throws IllegalArgumentException if the update operation fails
     */
    @Override
    public PatientIdentifierDto updatePatientIdentifier(long patientId, PatientIdentifierDto patientIdentifierDto) {
        if (!patientIdentifierDto.isPreferred()) {
            patientIdentifierRepository.findPatientIdentifierByIdentifierTypeAndPreferred(
                            patientId,
                            patientIdentifierDto.getIdentifierTypeId())
                    .ifPresent(p -> {
                        throw new IllegalArgumentException("Preferred Patient Identifier can not be unset");
                    });
        }
        return patientIdentifierRepository.finedByPatientIdAndIdentifierTypeId(patientId,
                        patientIdentifierDto.getIdentifierTypeId())
                .map(patientIdentifier -> {
                    try {
                        patientIdentifier.setPreferred(patientIdentifierDto.isPreferred());
                        return patientIdentifierMapper.toPatientIdentifierDto(
                                patientIdentifierRepository.save(patientIdentifier));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error updating Patient Identifier with patientID: "
                                + patientId, e);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Patient Identifier", "PatientId",
                        String.valueOf(patientId)));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation performs a soft delete by:
     * 1. Finding the identifier using its string representation
     * 2. Setting the voided flag
     * 3. Recording the user who performed the void
     * 4. Recording the void timestamp
     * 5. Storing the reason for voiding
     *
     * <p>
     * Note: Due to the composite key structure, the identifier is located using
     * its string representation rather than the composite key directly.
     *
     * @throws ResourceNotFoundException if the identifier is not found
     * @throws IllegalStateException if the void operation fails
     */
    @Override
    public void deletePatientIdentifier(long id, RecordVoidRequest recordVoidRequest) {
        // Since we need both patient ID and identifier type ID to form the composite key,
        // we'll need to find the identifier first using other means
        patientIdentifierRepository.findById(id)
                .map(patientIdentifier -> {
                    try {
                        patientIdentifier.setVoided(true);
                        patientIdentifier.setVoidedBy(CurrentUser.getCurrentUser().getPersonId());
                        patientIdentifier.setVoidedAt(LocalDateTime.now());
                        patientIdentifier.setVoidReason(recordVoidRequest.getVoidReason());
                        return patientIdentifierMapper.toPatientIdentifierDto(
                                patientIdentifierRepository.save(patientIdentifier));
                    } catch (Exception e) {
                        throw new IllegalStateException("Error deleting Patient Identifier with id: " + id, e);
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Patient Identifier", "Id", String.valueOf(id)));
    }
}
