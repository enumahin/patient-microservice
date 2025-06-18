package com.alienworkspace.cdr.patient.model.mapper;

import static org.mapstruct.factory.Mappers.getMapper;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierDto;
import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import com.alienworkspace.cdr.patient.model.audit.AuditTrailMapper;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between {@link PatientIdentifier} entities and {@link PatientIdentifierDto} DTOs.
 * This mapper handles the bidirectional conversion of patient identifier data, ensuring proper
 * transformation of all fields including audit information.
 *
 * <p>
 * Key features:
 * - Bidirectional mapping between entities and DTOs
 * - Audit trail information preservation
 * - Location and identifier type handling
 * - Preferred status management
 *
 * <p>
 * The mapper uses MapStruct for the basic mapping infrastructure and includes
 * custom mapping methods for complex transformations. It also integrates with
 * {@link AuditTrailMapper} for handling audit-related fields.
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientIdentifier
 * @see PatientIdentifierDto
 * @see AuditTrailMapper
 */
@Mapper(componentModel = "spring")
public interface PatientIdentifierMapper {

    /**
     * Singleton instance of the mapper.
     */
    PatientIdentifierMapper INSTANCE = getMapper(PatientIdentifierMapper.class);

    /**
     * Converts a {@link PatientIdentifier} entity to a {@link PatientIdentifierDto}.
     * This method performs a deep mapping of the patient identifier, including:
     * - Basic identifier information
     * - Location details
     * - Identifier type reference
     * - Patient reference
     * - Preferred status
     * - Audit trail information
     *
     * @param patientIdentifier The patient identifier entity to convert
     * @return {@link PatientIdentifierDto} The converted DTO containing all relevant information
     * @throws IllegalArgumentException if the input entity is invalid or contains inconsistent data
     */
    default PatientIdentifierDto toPatientIdentifierDto(PatientIdentifier patientIdentifier) {
        PatientIdentifierDto patientIdentifierDto = PatientIdentifierDto.builder()
                .patientId(patientIdentifier.getPatient().getPatientId())
                .patientIdentifierId(patientIdentifier.getPatientIdentifierId())
                .identifier(patientIdentifier.getIdentifier())
                .locationId(patientIdentifier.getLocationId())
                .identifierTypeId(patientIdentifier.getPatientIdentifierType().getPatientIdentifierTypeId())
                .patientIdentifierId(patientIdentifier.getPatient().getPatientId())
                .preferred(patientIdentifier.isPreferred())
                .build();
        AuditTrailMapper.mapToDto(patientIdentifier, patientIdentifierDto);
        return patientIdentifierDto;
    }

    /**
     * Converts a {@link PatientIdentifierDto} to a {@link PatientIdentifier} entity.
     * This method creates a new patient identifier entity with:
     * - Basic identifier information
     * - Location details
     * - Preferred status
     * - Audit trail information
     *
     * <p>
     * Note: The patient and identifier type relationships must be set separately
     * after the conversion, as they require additional context not available in the DTO.
     *
     * @param patientIdentifierDto The DTO to convert into an entity
     * @return {@link PatientIdentifier} The converted entity with basic information set
     * @throws IllegalArgumentException if the input DTO is invalid or contains inconsistent data
     */
    default PatientIdentifier toPatientIdentifier(PatientIdentifierDto patientIdentifierDto) {
        PatientIdentifier patientIdentifier = PatientIdentifier.builder()
                .patientIdentifierId(patientIdentifierDto.getPatientIdentifierId())
                .identifier(patientIdentifierDto.getIdentifier())
                .locationId(patientIdentifierDto.getLocationId())
                .preferred(patientIdentifierDto.isPreferred())
                .build();
        AuditTrailMapper.mapFromDto(patientIdentifierDto, patientIdentifier);
        return patientIdentifier;
    }
}
