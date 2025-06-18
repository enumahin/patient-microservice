package com.alienworkspace.cdr.patient.model.mapper;

import static org.mapstruct.factory.Mappers.getMapper;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierTypeDto;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import com.alienworkspace.cdr.patient.model.audit.AuditTrailMapper;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between {@link PatientIdentifierType} entities and
 * {@link PatientIdentifierTypeDto} DTOs.
 * This mapper handles the bidirectional conversion of patient identifier type data, managing
 * the transformation of all identifier type attributes and metadata.
 *
 * <p>
 * Key features:
 * - Bidirectional mapping between entities and DTOs
 * - Audit trail information preservation
 * - Validation rules handling
 * - Format specifications management
 * - Required/Unique flag handling
 *
 * <p>
 * The mapper supports the following identifier type attributes:
 * - Basic metadata (name, description)
 * - Format specifications and hints
 * - Validation rules
 * - Required/Unique constraints
 * - Audit information
 *
 * <p>
 * The mapper uses MapStruct for the basic mapping infrastructure and includes
 * custom mapping methods for complex transformations. It also integrates with
 * {@link AuditTrailMapper} for handling audit-related fields.
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientIdentifierType
 * @see PatientIdentifierTypeDto
 * @see AuditTrailMapper
 */
@Mapper(componentModel = "spring")
public interface PatientIdentifierTypeMapper {

    /**
     * Singleton instance of the mapper.
     */
    PatientIdentifierTypeMapper INSTANCE = getMapper(PatientIdentifierTypeMapper.class);

    /**
     * Converts an {@link PatientIdentifierType} entity to a {@link PatientIdentifierTypeDto}.
     * This method performs a complete mapping of the identifier type, including:
     * - Basic metadata (ID, name, description)
     * - Format specifications and hints
     * - Validation rules
     * - Required/Unique flags
     * - Audit trail information
     *
     * <p>
     * The method ensures that all business rules and constraints are preserved
     * during the conversion process.
     *
     * @param patientIdentifierType The identifier type entity to convert
     * @return {@link PatientIdentifierTypeDto} The converted DTO containing all relevant information
     * @throws IllegalArgumentException if the input entity is invalid or contains inconsistent data
     */
    default PatientIdentifierTypeDto toPatientIdentifierTypeDto(PatientIdentifierType patientIdentifierType) {
        PatientIdentifierTypeDto build = PatientIdentifierTypeDto.builder()
                .patientIdentifierTypeId(patientIdentifierType.getPatientIdentifierTypeId())
                .name(patientIdentifierType.getName())
                .description(patientIdentifierType.getDescription())
                .format(patientIdentifierType.getFormat())
                .required(patientIdentifierType.isRequired())
                .unique(patientIdentifierType.isUnique())
                .formatHint(patientIdentifierType.getFormatHint())
                .validator(patientIdentifierType.getValidator())
                .build();
        AuditTrailMapper.mapToDto(patientIdentifierType, build);
        return build;
    }

    /**
     * Converts a {@link PatientIdentifierTypeDto} to an {@link PatientIdentifierType} entity.
     * This method creates a new identifier type entity with:
     * - Basic metadata (ID, name, description)
     * - Format specifications and hints
     * - Validation rules
     * - Required/Unique flags
     * - Audit trail information
     *
     * <p>
     * The method ensures that all validation rules and constraints are properly
     * transferred to the created entity.
     *
     * @param patientIdentifierTypeDto The DTO to convert into an entity
     * @return {@link PatientIdentifierType} The converted entity with all information set
     * @throws IllegalArgumentException if the input DTO is invalid or contains inconsistent data
     */
    default PatientIdentifierType toPatientIdentifierType(PatientIdentifierTypeDto patientIdentifierTypeDto) {
        PatientIdentifierType build = PatientIdentifierType.builder()
                .patientIdentifierTypeId(patientIdentifierTypeDto.getPatientIdentifierTypeId())
                .name(patientIdentifierTypeDto.getName())
                .description(patientIdentifierTypeDto.getDescription())
                .format(patientIdentifierTypeDto.getFormat())
                .required(patientIdentifierTypeDto.isRequired())
                .isUnique(patientIdentifierTypeDto.isUnique())
                .formatHint(patientIdentifierTypeDto.getFormatHint())
                .validator(patientIdentifierTypeDto.getValidator())
                .build();
        AuditTrailMapper.mapFromDto(patientIdentifierTypeDto, build);
        return build;
    }
}
