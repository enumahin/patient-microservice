package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierRepository;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierTypeRepository;
import com.alienworkspace.cdr.patient.repository.PatientProgramRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
class PatientIdentifierServiceIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private PatientIdentifierService patientIdentifierService;

    @Autowired
    private PatientIdentifierRepository patientIdentifierRepository;

    @Autowired
    private PatientProgramRepository patientProgramRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    @PersistenceContext
    EntityManager entityManager;

    private Patient testPatient;
    private PatientIdentifierType testPatientIdentifierType;
    private PatientIdentifierDto.PatientIdentifierDtoBuilder identifierDtoBuilder;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        patientIdentifierRepository.deleteAllInBatch();
        patientProgramRepository.deleteAllInBatch();
        patientRepository.deleteAllInBatch();
        patientIdentifierTypeRepository.deleteAllInBatch();
        entityManager.flush();
        entityManager.clear();
        entityManager.getEntityManagerFactory().getCache().evictAll();

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

        // Initialize identifier DTO builder
        identifierDtoBuilder = PatientIdentifierDto.builder()
                .patientId(testPatient.getPatientId())
                .identifierTypeId(testPatientIdentifierType.getPatientIdentifierTypeId())
                .identifier("1234567890")
                .preferred(true)
                .locationId(1);
    }

    @Test
    @DisplayName("Test Save Patient Identifier")
    void testSavePatientIdentifier() {
        // Arrange
        PatientIdentifierDto identifierDto = identifierDtoBuilder.build();

        // Act
        PatientIdentifierDto savedIdentifier = patientIdentifierService.savePatientIdentifier(identifierDto);

        // Assert
        assertNotNull(savedIdentifier);
        assertEquals(identifierDto.getPatientId(), savedIdentifier.getPatientId());
        assertEquals(identifierDto.getIdentifierTypeId(), savedIdentifier.getIdentifierTypeId());
        assertEquals(identifierDto.getIdentifier(), savedIdentifier.getIdentifier());
        assertTrue(savedIdentifier.isPreferred());
        assertEquals(identifierDto.getLocationId(), savedIdentifier.getLocationId());
    }

    @Test
    @DisplayName("Test Update Patient Identifier")
    void testUpdatePatientIdentifier() {
        // Arrange
        PatientIdentifierDto identifierDto = patientIdentifierService
                .savePatientIdentifier(identifierDtoBuilder.preferred(false).build());

        PatientIdentifierDto updateDto = PatientIdentifierDto.builder()
                .patientId(testPatient.getPatientId())
                .identifierTypeId(testPatientIdentifierType.getPatientIdentifierTypeId())
                .preferred(true)
                .build();

        // Act
        PatientIdentifierDto updatedIdentifier = patientIdentifierService
                .updatePatientIdentifier(identifierDto.getPatientIdentifierId(), updateDto);

        // Assert
        assertNotNull(updatedIdentifier);
        assertEquals(identifierDto.getPatientIdentifierId(), updatedIdentifier.getPatientIdentifierId());
        assertEquals(identifierDto.getIdentifier(), updatedIdentifier.getIdentifier());
        assertTrue(updatedIdentifier.isPreferred());
    }


    @Test
    @DisplayName("Test Update Patient Identifier Fail")
    void testUpdatePreferredPatientIdentifierFail() {
        // Arrange
        PatientIdentifierDto identifierDto = patientIdentifierService
                .savePatientIdentifier(identifierDtoBuilder.build());

        PatientIdentifierDto updateDto = PatientIdentifierDto.builder()
                .patientId(testPatient.getPatientId())
                .identifierTypeId(testPatientIdentifierType.getPatientIdentifierTypeId())
                .preferred(false)
                .build();

        // Act

        // Assert
        assertThrows(IllegalArgumentException.class,
                () -> patientIdentifierService.updatePatientIdentifier(
                        identifierDto.getPatientIdentifierId(), updateDto));
    }

    @Test
    @DisplayName("Test Delete (Void) Patient Identifier")
    void testDeletePatientIdentifier() {
        // Arrange
        PatientIdentifier patientIdentifier = PatientIdentifier.builder().patient(testPatient)
                .patientIdentifierType(testPatientIdentifierType)
                .identifier("1234567890")
                .preferred(true)
                .locationId(1)
                .build();
        PatientIdentifier identifierSaved = patientIdentifierRepository.save(patientIdentifier);

        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test void reason")
                .build();

        // Act
        patientIdentifierService.deletePatientIdentifier(
                identifierSaved.getPatientIdentifierId(),
                voidRequest
        );

        // Assert
        assertTrue(patientIdentifierRepository.findById(identifierSaved.getPatientIdentifierId())
                .map(identifier -> identifier.isVoided())
                .orElse(false));
    }

    @Test
    @DisplayName("Test Save Patient Identifier - Patient Not Found")
    void testSavePatientIdentifierPatientNotFound() {
        // Arrange
        PatientIdentifierDto identifierDto = identifierDtoBuilder
                .patientId(999L)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierService.savePatientIdentifier(identifierDto));
    }

    @Test
    @DisplayName("Test Save Patient Identifier - Identifier Type Not Found")
    void testSavePatientIdentifierTypeNotFound() {
        // Arrange
        PatientIdentifierDto identifierDto = identifierDtoBuilder
                .identifierTypeId(999)
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierService.savePatientIdentifier(identifierDto));
    }

    @Test
    @DisplayName("Test Update Patient Identifier - Not Found")
    void testUpdatePatientIdentifierNotFound() {
        // Arrange
        PatientIdentifierDto updateDto = identifierDtoBuilder.build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierService.updatePatientIdentifier(999L, updateDto));
    }

    @Test
    @DisplayName("Test Delete Patient Identifier - Not Found")
    void testDeletePatientIdentifierNotFound() {
        // Arrange
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test void reason")
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierService.deletePatientIdentifier(999L, voidRequest));
    }
} 