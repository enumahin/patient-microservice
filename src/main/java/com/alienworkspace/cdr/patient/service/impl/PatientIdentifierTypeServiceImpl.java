package com.alienworkspace.cdr.patient.service.impl;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierTypeDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.helpers.CurrentUser;
import com.alienworkspace.cdr.patient.model.mapper.PatientIdentifierTypeMapper;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierTypeRepository;
import com.alienworkspace.cdr.patient.service.PatientIdentifierTypeService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link PatientIdentifierTypeService} interface.
 * This class provides the core implementation of patient identifier type operations,
 * handling type data persistence and business logic.
 *
 * <p>
 * Key features:
 * - Identifier type registration
 * - Format validation rules
 * - Type metadata management
 * - Status tracking
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
 * - Type uniqueness validation
 * - Format consistency
 * - Status transitions
 * - Data integrity checks
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientIdentifierTypeService
 * @see PatientIdentifierTypeRepository
 * @see PatientIdentifierTypeMapper
 */
@Service
@AllArgsConstructor
public class PatientIdentifierTypeServiceImpl implements PatientIdentifierTypeService {

    private final PatientIdentifierTypeRepository patientIdentifierTypeRepository;
    private final PatientIdentifierTypeMapper patientIdentifierTypeMapper;

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation performs the following steps:
     * 1. Converts the DTO to a domain entity
     * 2. Saves the entity to the database
     * 3. Converts the saved entity back to a DTO
     *
     * @throws IllegalArgumentException if the DTO contains invalid data
     */
    @Override
    public PatientIdentifierTypeDto createPatientIdentifierType(PatientIdentifierTypeDto patientIdentifierDto) {
        return patientIdentifierTypeMapper.toPatientIdentifierTypeDto(
                patientIdentifierTypeRepository.save(patientIdentifierTypeMapper
                        .toPatientIdentifierType(patientIdentifierDto))
        );
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation:
     * 1. Retrieves the existing entity
     * 2. Updates only the modifiable fields
     * 3. Preserves the audit trail
     * 4. Saves the updated entity
     *
     * <p>
     * The following fields are updated:
     * - Description
     * - Format
     * - Required status
     * - Unique status
     * - Format hint
     * - Validator
     *
     * @throws IllegalArgumentException if the update operation fails due to invalid data
     */
    @Override
    public PatientIdentifierTypeDto updatePatientIdentifierType(int id, PatientIdentifierTypeDto patientIdentifierDto) {
        return patientIdentifierTypeRepository.findById(id)
                .map(patientIdentifierType -> {
                    try {
                        patientIdentifierType.setDescription(patientIdentifierDto.getDescription());
                        patientIdentifierType.setFormat(patientIdentifierDto.getFormat());
                        patientIdentifierType.setRequired(patientIdentifierDto.isRequired());
                        patientIdentifierType.setUnique(patientIdentifierDto.isUnique());
                        patientIdentifierType.setFormatHint(patientIdentifierDto.getFormatHint());
                        patientIdentifierType.setValidator(patientIdentifierDto.getValidator());
                        return patientIdentifierTypeMapper.toPatientIdentifierTypeDto(
                                patientIdentifierTypeRepository.save(patientIdentifierType)
                        );
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error updating Patient Identifier Type with id: " + id, e);
                    }
                }).orElseThrow(() -> new ResourceNotFoundException("Patient Identifier Type", "Id",
                        String.valueOf(id)));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation performs a soft delete by:
     * 1. Setting the voided flag to true
     * 2. Recording the user who performed the void
     * 3. Recording the void timestamp
     * 4. Storing the reason for voiding
     *
     * @throws IllegalStateException if the void operation fails
     */
    @Override
    public void deletePatientIdentifierType(int id, RecordVoidRequest recordVoidRequest) {
        patientIdentifierTypeRepository.findById(id)
                .map(patientIdentifierType -> {
                    try {
                        patientIdentifierType.setVoided(true);
                        patientIdentifierType.setVoidedBy(CurrentUser.getCurrentUser().getPersonId());
                        patientIdentifierType.setVoidedAt(LocalDateTime.now());
                        patientIdentifierType.setVoidReason(recordVoidRequest.getVoidReason());
                        return patientIdentifierTypeMapper.toPatientIdentifierTypeDto(
                                patientIdentifierTypeRepository.save(patientIdentifierType)
                        );
                    } catch (Exception e) {
                        throw new IllegalStateException("Error deleting Patient Identifier Type with id: " + id, e);
                    }
                }).orElseThrow(() ->
                        new ResourceNotFoundException("Patient Identifier Type", "Id", String.valueOf(id)));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation retrieves the entity and converts it to a DTO.
     * Only non-voided identifier types are returned.
     */
    @Override
    public PatientIdentifierTypeDto getPatientIdentifierType(int id) {
        return patientIdentifierTypeRepository.findById(id)
                .map(patientIdentifierTypeMapper::toPatientIdentifierTypeDto)
                .orElseThrow(() -> new ResourceNotFoundException("Patient Identifier Type", "Id", String.valueOf(id)));
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation:
     * 1. Retrieves all non-voided identifier types
     * 2. Converts each entity to a DTO
     * 3. Returns them as a list
     *
     * <p>
     * The results are filtered to exclude voided records.
     */
    @Override
    public List<PatientIdentifierTypeDto> getAllPatientIdentifierTypes() {
        return patientIdentifierTypeRepository.findAllByVoided(false).stream()
                .map(patientIdentifierTypeMapper::toPatientIdentifierTypeDto).collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * This implementation:
     * 1. Retrieves all identifier types regardless of void status
     * 2. Converts each entity to a DTO
     * 3. Returns them as a list
     *
     * <p>
     * Both voided and non-voided records are included in the results.
     */
    @Override
    public List<PatientIdentifierTypeDto> getAllPatientIdentifierTypesBothVoided() {
        return patientIdentifierTypeRepository.findAll().stream()
                .map(patientIdentifierTypeMapper::toPatientIdentifierTypeDto).collect(Collectors.toList());
    }
}
