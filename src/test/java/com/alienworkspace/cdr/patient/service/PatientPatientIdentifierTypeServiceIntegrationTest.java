package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierTypeDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierTypeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientPatientIdentifierTypeServiceIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private PatientIdentifierTypeService patientIdentifierTypeService;

    @Autowired
    private PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    private PatientIdentifierTypeDto.PatientIdentifierTypeDtoBuilder identifierTypeDtoBuilder;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        patientIdentifierTypeRepository.deleteAll();

        // Initialize identifier type DTO builder
        identifierTypeDtoBuilder = PatientIdentifierTypeDto.builder()
                .name("National ID")
                .description("National Identity Number")
                .format("^[0-9]{10}$")
                .required(true)
                .unique(true)
                .formatHint("10 digit number")
                .validator("org.openmrs.patient.impl.LuhnIdentifierValidator");
    }

    @Test
    @DisplayName("Test Create Patient Identifier Type")
    void testCreatePatientIdentifierType() {
        // Arrange
        PatientIdentifierTypeDto identifierTypeDto = identifierTypeDtoBuilder.build();

        // Act
        PatientIdentifierTypeDto createdType = patientIdentifierTypeService.createPatientIdentifierType(identifierTypeDto);

        // Assert
        assertNotNull(createdType);
        assertNotNull(createdType.getPatientIdentifierTypeId());
        assertEquals(identifierTypeDto.getName(), createdType.getName());
        assertEquals(identifierTypeDto.getDescription(), createdType.getDescription());
        assertEquals(identifierTypeDto.getFormat(), createdType.getFormat());
        assertTrue(createdType.isRequired());
        assertTrue(createdType.isUnique());
        assertEquals(identifierTypeDto.getFormatHint(), createdType.getFormatHint());
        assertEquals(identifierTypeDto.getValidator(), createdType.getValidator());
    }

    @Test
    @DisplayName("Test Update Patient Identifier Type")
    void testUpdatePatientIdentifierType() {
        // Arrange
        PatientIdentifierTypeDto identifierTypeDto = patientIdentifierTypeService
                .createPatientIdentifierType(identifierTypeDtoBuilder.build());

        PatientIdentifierTypeDto updateDto = PatientIdentifierTypeDto.builder()
                .description("Updated Description")
                .format("^[A-Z]{2}[0-9]{8}$")
                .required(false)
                .unique(true)
                .formatHint("2 letters followed by 8 numbers")
                .validator("org.openmrs.patient.impl.CustomValidator")
                .build();

        // Act
        PatientIdentifierTypeDto updatedType = patientIdentifierTypeService
                .updatePatientIdentifierType(identifierTypeDto.getPatientIdentifierTypeId(), updateDto);

        // Assert
        assertEquals(identifierTypeDto.getPatientIdentifierTypeId(), updatedType.getPatientIdentifierTypeId());
        assertEquals(identifierTypeDto.getName(), updatedType.getName()); // Name shouldn't change
        assertEquals(updateDto.getDescription(), updatedType.getDescription());
        assertEquals(updateDto.getFormat(), updatedType.getFormat());
        assertFalse(updatedType.isRequired());
        assertTrue(updatedType.isUnique());
        assertEquals(updateDto.getFormatHint(), updatedType.getFormatHint());
        assertEquals(updateDto.getValidator(), updatedType.getValidator());
    }

    @Test
    @DisplayName("Test Delete (Void) Patient Identifier Type")
    void testDeletePatientIdentifierType() {
        // Arrange
        PatientIdentifierTypeDto identifierTypeDto = patientIdentifierTypeService
                .createPatientIdentifierType(identifierTypeDtoBuilder.build());

        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test void reason")
                .build();

        // Act
        patientIdentifierTypeService.deletePatientIdentifierType(
                identifierTypeDto.getPatientIdentifierTypeId(),
                voidRequest
        );

        // Get all types including voided ones
        List<PatientIdentifierTypeDto> allTypes = patientIdentifierTypeService.getAllPatientIdentifierTypesBothVoided();
        List<PatientIdentifierTypeDto> activeTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        // Assert
        assertEquals(1, allTypes.size());
        assertTrue(allTypes.get(0).getVoided());
        assertEquals("Test void reason", allTypes.get(0).getVoidReason());
        assertTrue(activeTypes.isEmpty());
    }

    @Test
    @DisplayName("Test Get Patient Identifier Type")
    void testGetPatientIdentifierType() {
        // Arrange
        PatientIdentifierTypeDto identifierTypeDto = patientIdentifierTypeService
                .createPatientIdentifierType(identifierTypeDtoBuilder.build());

        // Act
        PatientIdentifierTypeDto foundType = patientIdentifierTypeService
                .getPatientIdentifierType(identifierTypeDto.getPatientIdentifierTypeId());

        // Assert
        assertNotNull(foundType);
        assertEquals(identifierTypeDto.getPatientIdentifierTypeId(), foundType.getPatientIdentifierTypeId());
        assertEquals(identifierTypeDto.getName(), foundType.getName());
        assertEquals(identifierTypeDto.getDescription(), foundType.getDescription());
    }

    @Test
    @DisplayName("Test Get All Patient Identifier Types")
    void testGetAllPatientIdentifierTypes() {
        // Arrange
        PatientIdentifierTypeDto type1 = patientIdentifierTypeService
                .createPatientIdentifierType(identifierTypeDtoBuilder.name("Type 1").build());
        PatientIdentifierTypeDto type2 = patientIdentifierTypeService
                .createPatientIdentifierType(identifierTypeDtoBuilder.name("Type 2").build());

        // Act
        List<PatientIdentifierTypeDto> allTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        // Assert
        assertEquals(2, allTypes.size());
        assertTrue(allTypes.stream().anyMatch(type -> type.getName().equals("Type 1")));
        assertTrue(allTypes.stream().anyMatch(type -> type.getName().equals("Type 2")));
    }

    @Test
    @DisplayName("Test Get All Patient Identifier Types Including Voided")
    void testGetAllPatientIdentifierTypesBothVoided() {
        // Arrange
        PatientIdentifierTypeDto activeType = patientIdentifierTypeService
                .createPatientIdentifierType(identifierTypeDtoBuilder.name("Active Type").build());
        
        PatientIdentifierTypeDto typeToVoid = patientIdentifierTypeService
                .createPatientIdentifierType(identifierTypeDtoBuilder.name("Voided Type").build());
        
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test void reason")
                .build();
        
        patientIdentifierTypeService.deletePatientIdentifierType(
                typeToVoid.getPatientIdentifierTypeId(),
                voidRequest
        );

        // Act
        List<PatientIdentifierTypeDto> allTypes = patientIdentifierTypeService.getAllPatientIdentifierTypesBothVoided();
        List<PatientIdentifierTypeDto> activeTypes = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        // Assert
        assertEquals(2, allTypes.size());
        assertEquals(1, activeTypes.size());
        assertTrue(allTypes.stream().anyMatch(type -> type.getName().equals("Active Type") && !type.getVoided()));
        assertTrue(allTypes.stream().anyMatch(type -> type.getName().equals("Voided Type") && type.getVoided()));
    }

    @Test
    @DisplayName("Test Patient Identifier Type Not Found")
    void testPatientIdentifierTypeNotFound() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierTypeService.getPatientIdentifierType(999));
        
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierTypeService.updatePatientIdentifierType(999, identifierTypeDtoBuilder.build()));
        
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierTypeService.deletePatientIdentifierType(999,
                        RecordVoidRequest.builder().voidReason("Test").build()));
    }
} 