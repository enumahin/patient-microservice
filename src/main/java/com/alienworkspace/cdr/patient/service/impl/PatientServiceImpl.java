package com.alienworkspace.cdr.patient.service.impl;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.alienworkspace.cdr.model.dto.person.PersonDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.helpers.CurrentUser;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import com.alienworkspace.cdr.patient.model.mapper.PatientMapper;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import com.alienworkspace.cdr.patient.service.PatientService;
import com.alienworkspace.cdr.patient.service.client.DemographicFeignClient;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link PatientService} interface.
 * This class provides the core implementation of patient management operations,
 * handling patient data persistence and business logic.
 *
 * <p>
 * Key features:
 * - Patient registration and updates
 * - Patient search operations
 * - Patient status management
 * - Program enrollment coordination
 * - Identifier management
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
 * - Patient uniqueness validation
 * - Identifier consistency
 * - Status transitions
 * - Data integrity checks
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientService
 * @see PatientDto
 * @see PatientMapper
 */
@Service
@AllArgsConstructor
@Transactional
public class PatientServiceImpl implements PatientService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PatientServiceImpl.class);

    private PatientRepository patientRepository;
    private PatientIdentifierRepository patientIdentifierRepository;

    private PatientMapper patientMapper;

    private DemographicFeignClient demographicFeignClient;

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * 1. Converts the DTO to a patient entity
     * 2. Persists the new patient record
     * 3. Returns the created patient as DTO
     *
     * @throws IllegalArgumentException if there are issues with the patient data
     */
    @Override
    @Transactional
    public PatientDto createPatient(PatientDto patientDto) {
        try {
//            PersonDto person = demographicFeignClient.addPerson(patientDto.getPerson()).getBody();
//            if (person == null || person.getPersonId() == null) {
//                throw new IllegalArgumentException("Error creating person: " + person);
//            }
//            patientDto.setPatientId(person.getPersonId());
            return patientMapper.toPatientDto(patientRepository.save(patientMapper.toPatient(patientDto)));
        } catch (Exception e) {
            LOGGER.error("Error creating patient", e);
            throw new IllegalArgumentException("Error creating patient", e);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * 1. Validates patient existence
     * 2. Updates the patient's allergies information
     * 3. Persists the changes
     * 4. Logs any errors during the update process
     *
     * <p>
     * The method ensures atomic updates within a transaction and includes
     * error logging for troubleshooting purposes.
     *
     * @throws ResourceNotFoundException if the patient is not found
     * @throws IllegalArgumentException if there are issues with the update data
     */
    @Override
    public PatientDto updatePatient(long id, PatientDto patientDto) {
        return patientRepository.findById(id)
                .map(patient -> {
                    try {
                        patient.setAllergies(patientDto.getAllergies());
                        patient.setLastModifiedAt(LocalDateTime.now());
                        patient.setLastModifiedBy(CurrentUser.getCurrentUser().getPersonId());
                        return patientMapper.toPatientDto(patientRepository.save(patient));
                    } catch (Exception e) {
                        LOGGER.error("Error updating patient:", e);
                        throw new IllegalArgumentException("Error updating patient: {}", e);
                    }
                }).orElseThrow(() -> {
                    LOGGER.error("Error deleting patient with ID: {}", id);
                    return new ResourceNotFoundException("Patient", "Id", String.valueOf(id));
                });
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * 1. Validates patient existence
     * 2. Performs soft deletion by setting voided flag
     * 3. Records audit information:
     *    - Who performed the void operation
     *    - When it was performed
     *    - Reason for voiding
     * 4. Logs the operation for tracking
     *
     * @throws ResourceNotFoundException if the patient is not found
     * @throws IllegalArgumentException if there are issues with the void operation
     */
    @Override
    @Transactional
    public void deletePatient(long id, RecordVoidRequest recordVoidRequest) {
        try {
            LOGGER.info("Voiding person with ID: {}", id);
            demographicFeignClient.deletePerson(id, recordVoidRequest);
        } catch (Exception e) {
            LOGGER.error("Error voiding person:", e);
            throw new IllegalArgumentException("Error voiding person: {}", e);
        }

        patientRepository.findById(id)
                .map(patient -> {
                    try {
                        patient.setVoided(true);
                        patient.setVoidedBy(CurrentUser.getCurrentUser().getPersonId());
                        patient.setVoidedAt(LocalDateTime.now());
                        patient.setVoidReason(recordVoidRequest.getVoidReason());
                        return patientMapper.toPatientDto(patientRepository.save(patient));
                    } catch (Exception e) {
                        LOGGER.error("Error deleting patient:", e);
                        throw new IllegalArgumentException("Error deleting patient: {}", e);
                    }
                }).orElseThrow(() -> {
                    LOGGER.error("Error deleting patient with ID: {}", id);
                    return new ResourceNotFoundException("Patient", "Id", String.valueOf(id));
                });
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * 1. Retrieves patient by ID
     * 2. Converts to DTO format
     * 3. Includes error logging for not-found cases
     *
     * @throws ResourceNotFoundException if the patient is not found
     */
    @Override
    public PatientDto getPatient(long id) {
        PatientDto patientDto = patientMapper.toPatientDto(patientRepository.findById(id)
                .orElseThrow(() -> {
                    LOGGER.error("Patient With Id: {} not found.", id);
                    return new ResourceNotFoundException("Patient", "Id", String.valueOf(id));
                }));
        patientDto.setPerson(getPerson(patientDto.getPatientId(), false));
        return patientDto;

    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * 1. Searches for patient identifier
     * 2. Retrieves associated patient record
     * 3. Converts to DTO format
     * 4. Includes error logging for not-found cases
     *
     * @throws ResourceNotFoundException if no patient is found with the given identifier
     */
    @Override
    public PatientDto getPatientByIdentifier(String value) {
        PatientIdentifier patientIdentifier = patientIdentifierRepository.findPatientIdentifierByIdentifier(value)
                .orElseThrow(() -> {
                    LOGGER.error("Error fetching patient with Identifier: {}", value);
                    return new ResourceNotFoundException("Patient", "Identifier", value);
                });
        Patient patient = patientIdentifier.getPatient();
        return patientMapper.toPatientDto(patient);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * Returns all active patients in the system, mapped to DTOs.
     * Uses streaming for efficient memory usage with large result sets.
     */
    @Override
    public List<PatientDto> getAllPatients() {
        return patientRepository.findAll().stream()
                .map(patientMapper::toPatientDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * Returns all voided (soft-deleted) patients, mapped to DTOs.
     * Useful for audit and historical record purposes.
     */
    @Override
    public List<PatientDto> getAllPatientsBothVoided() {
        return patientRepository.findAllByVoided(true).stream()
                .map(patientMapper::toPatientDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * Retrieves all patients enrolled in a specific program.
     * Uses a custom repository query for efficient fetching.
     */
    @Override
    public List<PatientDto> getPatientsByProgram(int programId) {
        return patientRepository.findByProgram(programId).stream()
                .map(patientMapper::toPatientDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * Retrieves all patients with a specific identifier type.
     * Uses a custom repository query for efficient fetching.
     */
    @Override
    public List<PatientDto> getPatientsByIdentifierType(int identifierTypeId) {
        return patientRepository.findByIdentifierType(identifierTypeId).stream()
                .map(patientMapper::toPatientDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * Retrieves patients by program and active status.
     * Uses a custom repository query combining program and status filters.
     */
    @Override
    public List<PatientDto> getPatientsByProgramAndStatus(int programId, boolean active) {
        return patientRepository.findByProgramAndStatus(programId, active).stream()
                .map(patientMapper::toPatientDto)
                .toList();
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * Retrieves a person by ID from the Demographic Service.
     */
    @Override
    public PersonDto getPerson(long personId, boolean includeVoided) {
        return demographicFeignClient.getPerson(personId, includeVoided).getBody();
    }
}
