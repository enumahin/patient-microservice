package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientPatientIdentifierTypeRepositoryIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    private PatientIdentifierType.PatientIdentifierTypeBuilder identifierTypeBuilder;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        patientIdentifierTypeRepository.deleteAll();

        // Initialize the identifier type builder with common properties
        identifierTypeBuilder = PatientIdentifierType.builder()
                .name("National ID")
                .description("National Identity Number")
                .format("^[0-9]{10}$")
                .required(true)
                .isUnique(true)
                .formatHint("10 digit number")
                .validator("org.openmrs.patient.impl.LuhnIdentifierValidator");
    }

    @Test
    @DisplayName("Test Create Patient Identifier Type")
    void testCreatePatientIdentifierType() {
        // Arrange
        PatientIdentifierType patientIdentifierType = identifierTypeBuilder.build();

        // Act
        PatientIdentifierType savedPatientIdentifierType = patientIdentifierTypeRepository.save(patientIdentifierType);

        // Assert
        assertNotNull(savedPatientIdentifierType);
        assertNotNull(savedPatientIdentifierType.getPatientIdentifierTypeId());
        assertEquals(patientIdentifierType.getName(), savedPatientIdentifierType.getName());
        assertEquals(patientIdentifierType.getDescription(), savedPatientIdentifierType.getDescription());
        assertEquals(patientIdentifierType.getFormat(), savedPatientIdentifierType.getFormat());
        assertTrue(savedPatientIdentifierType.isRequired());
        assertTrue(savedPatientIdentifierType.isUnique());
        assertEquals(patientIdentifierType.getFormatHint(), savedPatientIdentifierType.getFormatHint());
        assertEquals(patientIdentifierType.getValidator(), savedPatientIdentifierType.getValidator());
    }

    @Test
    @DisplayName("Test Find All Non-Voided Identifier Types")
    void testFindAllByVoided() {
        // Arrange
        PatientIdentifierType activeType = identifierTypeBuilder
                .name("Active ID")
                .build();
        
        PatientIdentifierType voidedType = identifierTypeBuilder
                .name("Voided ID")
                .build();
        voidedType.setVoided(true);
        
        patientIdentifierTypeRepository.saveAll(List.of(activeType, voidedType));

        // Act
        List<PatientIdentifierType> activeTypes = patientIdentifierTypeRepository.findAllByVoided(false);
        List<PatientIdentifierType> voidedTypes = patientIdentifierTypeRepository.findAllByVoided(true);

        // Assert
        assertEquals(1, activeTypes.size());
        assertEquals("Active ID", activeTypes.get(0).getName());
        assertEquals(1, voidedTypes.size());
        assertEquals("Voided ID", voidedTypes.get(0).getName());
    }

    @Test
    @DisplayName("Test Update Patient Identifier Type")
    void testUpdatePatientIdentifierType() {
        // Arrange
        PatientIdentifierType patientIdentifierType = patientIdentifierTypeRepository.save(identifierTypeBuilder.build());
        
        // Act
        patientIdentifierType.setName("Updated Name");
        patientIdentifierType.setDescription("Updated Description");
        patientIdentifierType.setRequired(false);
        PatientIdentifierType updatedType = patientIdentifierTypeRepository.save(patientIdentifierType);

        // Assert
        assertEquals("Updated Name", updatedType.getName());
        assertEquals("Updated Description", updatedType.getDescription());
        assertFalse(updatedType.isRequired());
        assertEquals(patientIdentifierType.getPatientIdentifierTypeId(), updatedType.getPatientIdentifierTypeId());
    }

    @Test
    @DisplayName("Test Delete Patient Identifier Type")
    void testDeletePatientIdentifierType() {
        // Arrange
        PatientIdentifierType patientIdentifierType = patientIdentifierTypeRepository.save(identifierTypeBuilder.build());
        assertNotNull(patientIdentifierType.getPatientIdentifierTypeId());

        // Act
        patientIdentifierTypeRepository.deleteById(patientIdentifierType.getPatientIdentifierTypeId());

        // Assert
        assertFalse(patientIdentifierTypeRepository.findById(patientIdentifierType.getPatientIdentifierTypeId()).isPresent());
    }

    @Test
    @DisplayName("Test Find Patient Identifier Type By ID")
    void testFindPatientIdentifierTypeById() {
        // Arrange
        PatientIdentifierType patientIdentifierType = patientIdentifierTypeRepository.save(identifierTypeBuilder.build());

        // Act
        var foundType = patientIdentifierTypeRepository.findById(patientIdentifierType.getPatientIdentifierTypeId());

        // Assert
        assertTrue(foundType.isPresent());
        assertEquals(patientIdentifierType.getName(), foundType.get().getName());
        assertEquals(patientIdentifierType.getDescription(), foundType.get().getDescription());
    }
} 