package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientIdentifierRepositoryIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private PatientIdentifierRepository patientIdentifierRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    private Patient testPatient;
    private PatientIdentifierType testPatientIdentifierType;
    private PatientIdentifier.PatientIdentifierBuilder identifierBuilder;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        patientIdentifierRepository.deleteAll();
        patientRepository.deleteAll();
        patientIdentifierTypeRepository.deleteAll();

        // Create test patient
        testPatient = Patient.builder()
                .allergies("None")
                .build();
        testPatient = patientRepository.save(testPatient);

        // Create test identifier type
        testPatientIdentifierType = PatientIdentifierType.builder()
                .name("National ID")
                .description("National Identity Number")
                .format("^[0-9]{10}$")
                .required(true)
                .isUnique(true)
                .formatHint("10 digit number")
                .validator("org.openmrs.patient.impl.LuhnIdentifierValidator")
                .build();
        testPatientIdentifierType = patientIdentifierTypeRepository.save(testPatientIdentifierType);

        // Initialize the patient identifier builder
        identifierBuilder = PatientIdentifier.builder()
                .patient(testPatient)
                .patientIdentifierType(testPatientIdentifierType)
                .identifier("1234567890")
                .preferred(true)
                .locationId(1);
    }

    @Test
    @DisplayName("Test Create Patient Identifier")
    void testCreatePatientIdentifier() {
        // Arrange
        PatientIdentifier identifier = identifierBuilder.build();

        // Act
        PatientIdentifier savedIdentifier = patientIdentifierRepository.save(identifier);

        // Assert
        assertNotNull(savedIdentifier);
        assertEquals(testPatient.getPatientId(), savedIdentifier.getPatient().getPatientId());
        assertEquals(testPatientIdentifierType.getPatientIdentifierTypeId(), savedIdentifier.getPatientIdentifierType().getPatientIdentifierTypeId());
        assertEquals("1234567890", savedIdentifier.getIdentifier());
        assertTrue(savedIdentifier.isPreferred());
        assertEquals(1, savedIdentifier.getLocationId());
    }

    @Test
    @DisplayName("Test Find Patient Identifier By Identifier Value")
    void testFindPatientIdentifierByIdentifier() {
        // Arrange
        PatientIdentifier identifier = patientIdentifierRepository.save(identifierBuilder.build());

        // Act
        Optional<PatientIdentifier> foundIdentifier = patientIdentifierRepository.findPatientIdentifierByIdentifier("1234567890");

        // Assert
        assertTrue(foundIdentifier.isPresent());
        assertEquals(identifier.getIdentifier(), foundIdentifier.get().getIdentifier());
        assertEquals(identifier.getPatient().getPatientId(), foundIdentifier.get().getPatient().getPatientId());
    }

    @Test
    @DisplayName("Test Find Patient Identifier By Type And Preferred Status")
    void testFindPatientIdentifierByIdentifierTypeAndPreferred() {
        // Arrange
        PatientIdentifier identifier = patientIdentifierRepository.save(identifierBuilder.build());

        // Act
        Optional<PatientIdentifier> foundIdentifier = patientIdentifierRepository
                .findPatientIdentifierByIdentifierTypeAndPreferred(
                        testPatient.getPatientId(),
                        testPatientIdentifierType.getPatientIdentifierTypeId());

        // Assert
        assertTrue(foundIdentifier.isPresent());
        assertEquals(identifier.getIdentifier(), foundIdentifier.get().getIdentifier());
        assertTrue(foundIdentifier.get().isPreferred());
    }

    @Test
    @DisplayName("Test Update Patient Identifier")
    void testUpdatePatientIdentifier() {
        // Arrange
        PatientIdentifier identifier = patientIdentifierRepository.save(identifierBuilder.build());

        // Act
        identifier.setIdentifier("9876543210");
        identifier.setPreferred(false);
        PatientIdentifier updatedIdentifier = patientIdentifierRepository.save(identifier);

        // Assert
        assertEquals("9876543210", updatedIdentifier.getIdentifier());
        assertFalse(updatedIdentifier.isPreferred());
    }

    @Test
    @DisplayName("Test Delete Patient Identifier")
    void testDeletePatientIdentifier() {
        // Arrange
        PatientIdentifier identifier = patientIdentifierRepository.save(identifierBuilder.build());

        // Act
        patientIdentifierRepository.delete(identifier);

        // Assert
        Optional<PatientIdentifier> foundIdentifier = patientIdentifierRepository
                .findPatientIdentifierByIdentifier(identifier.getIdentifier());
        assertTrue(foundIdentifier.isEmpty());
    }

    @Test
    @DisplayName("Test Patient Identifier Not Found")
    void testPatientIdentifierNotFound() {
        // Act
        Optional<PatientIdentifier> result = patientIdentifierRepository
                .findPatientIdentifierByIdentifier("nonexistent");

        // Assert
        assertTrue(result.isEmpty());
    }
} 