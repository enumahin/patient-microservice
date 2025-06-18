package com.alienworkspace.cdr.patient.model.mapper;

import static org.mapstruct.factory.Mappers.getMapper;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.audit.AuditTrailMapper;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between {@link Patient} entities and {@link PatientDto} DTOs.
 * This mapper handles the bidirectional conversion of patient data, managing the transformation
 * of core patient information and related metadata.
 *
 * <p>
 * Key features:
 * - Bidirectional mapping between entities and DTOs
 * - Audit trail information preservation
 * - Patient medical information handling
 * - Core demographic data management
 *
 * <p>
 * The mapper supports the following patient attributes:
 * - Basic patient identification (ID)
 * - Medical information (allergies)
 * - Audit information (creation, modification, voiding)
 *
 * <p>
 * The mapper uses MapStruct for the basic mapping infrastructure and includes
 * custom mapping methods for complex transformations. It also integrates with
 * {@link AuditTrailMapper} for handling audit-related fields.
 *
 * <p>
 * Note: Related entities such as patient identifiers and program enrollments
 * are handled by their respective mappers and need to be managed separately.
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see Patient
 * @see PatientDto
 * @see AuditTrailMapper
 * @see PatientIdentifierMapper
 * @see PatientProgramMapper
 */
@Mapper(componentModel = "spring")
public interface PatientMapper {

    /**
     * Singleton instance of the mapper.
     */
    PatientMapper INSTANCE = getMapper(PatientMapper.class);

    /**
     * Converts a {@link Patient} entity to a {@link PatientDto}.
     * This method performs a mapping of the patient's core information, including:
     * - Patient identification (ID)
     * - Medical information (allergies)
     * - Audit trail information
     *
     * <p>
     * Note: Related collections (identifiers, program enrollments) must be
     * mapped separately using their respective mappers.
     *
     * @param patient The patient entity to convert
     * @return {@link PatientDto} The converted DTO containing core patient information
     * @throws IllegalArgumentException if the input entity is invalid or contains inconsistent data
     */
    default PatientDto toPatientDto(Patient patient) {
        PatientDto patientDtoBuilder = PatientDto.builder()
                .patientId(patient.getPatientId())
                .person(patient.getPerson() != null ? patient.getPerson() : null)
                .allergies(patient.getAllergies())
                .build();

        AuditTrailMapper.mapToDto(patient, patientDtoBuilder);

        return patientDtoBuilder;
    }

    /**
     * Converts a {@link PatientDto} to a {@link Patient} entity.
     * This method creates a new patient entity with:
     * - Medical information (allergies)
     * - Audit trail information
     *
     * <p>
     * Note: Related collections (identifiers, program enrollments) must be
     * set separately after the conversion, as they require additional context
     * not available in the DTO.
     *
     * @param patientDto The DTO to convert into an entity
     * @return {@link Patient} The converted entity with core information set
     * @throws IllegalArgumentException if the input DTO is invalid or contains inconsistent data
     */
    default Patient toPatient(PatientDto patientDto) {
        Patient patient = Patient.builder()
                .allergies(patientDto.getAllergies())
                .build();
        AuditTrailMapper.mapFromDto(patientDto, patient);
        return patient;
    }
}
