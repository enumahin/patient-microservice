package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import com.alienworkspace.cdr.patient.model.Program;
import com.alienworkspace.cdr.patient.model.PatientProgram;
import com.alienworkspace.cdr.patient.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PatientServiceIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private PatientService patientService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientIdentifierRepository patientIdentifierRepository;

    @Autowired
    private PatientIdentifierTypeRepository identifierTypeRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private PatientProgramRepository patientProgramRepository;

    private PatientDto.PatientDtoBuilder patientDtoBuilder;
    private Program testProgram;
    private PatientIdentifierType testPatientIdentifierType;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        patientProgramRepository.deleteAll();
        patientIdentifierRepository.deleteAll();
        patientRepository.deleteAll();
        programRepository.deleteAll();
        identifierTypeRepository.deleteAll();

        // Create test program
        testProgram = Program.builder()
                .name("Test Program")
                .programCode("TP" + System.currentTimeMillis())
                .description("Test Description")
                .active(true)
                .build();
        testProgram = programRepository.save(testProgram);

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
        testPatientIdentifierType = identifierTypeRepository.save(testPatientIdentifierType);

        // Initialize patient DTO builder
        patientDtoBuilder = PatientDto.builder()
                .allergies("Penicillin");
    }

    @Test
    @DisplayName("Test Create Patient")
    void testCreatePatient() {
        // Arrange
        PatientDto patientDto = patientDtoBuilder.build();

        // Act
        PatientDto createdPatient = patientService.createPatient(patientDto, "CORRELATION_ID");

        // Assert
        assertNotNull(createdPatient);
        assertNotNull(createdPatient.getPatientId());
        assertEquals(patientDto.getAllergies(), createdPatient.getAllergies());
    }

    @Test
    @DisplayName("Test Update Patient")
    void testUpdatePatient() {
        // Arrange
        PatientDto patientDto = patientService.createPatient(patientDtoBuilder.build(), "CORRELATION_ID");
        PatientDto updateDto = PatientDto.builder()
                .allergies("Updated Allergies")
                .build();

        // Act
        PatientDto updatedPatient = patientService.updatePatient(patientDto.getPatientId(), updateDto);

        // Assert
        assertEquals("Updated Allergies", updatedPatient.getAllergies());
        assertEquals(patientDto.getPatientId(), updatedPatient.getPatientId());
    }

    @Test
    @DisplayName("Test Delete (Void) Patient")
    void testDeletePatient() {
        // Arrange
        PatientDto patientDto = patientService.createPatient(patientDtoBuilder.build(), "CORRELATION_ID");
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test void reason")
                .build();

        // Act
        patientService.deletePatient(patientDto.getPatientId(), voidRequest);
        List<PatientDto> voidedPatients = patientService.getAllPatientsBothVoided();

        // Assert
        assertEquals(1, voidedPatients.size());
        assertEquals("Test void reason", voidedPatients.get(0).getVoidReason());
    }

    @Test
    @DisplayName("Test Get Patient By ID")
    void testGetPatient() {
        // Arrange
        PatientDto patientDto = patientService.createPatient(patientDtoBuilder.build(), "CORRELATION_ID");

        // Act
        PatientDto foundPatient = patientService.getPatient(patientDto.getPatientId(), "Correlation ID");

        // Assert
        assertNotNull(foundPatient);
        assertEquals(patientDto.getPatientId(), foundPatient.getPatientId());
        assertEquals(patientDto.getAllergies(), foundPatient.getAllergies());
    }

    @Test
    @DisplayName("Test Get Patient By Identifier")
    void testGetPatientByIdentifier() {
        // Arrange
        PatientDto patientDto = patientService.createPatient(patientDtoBuilder.build(), "CORRELATION_ID");
        Patient patient = patientRepository.findById(patientDto.getPatientId()).orElseThrow();
        
        PatientIdentifier identifier = PatientIdentifier.builder()
                .patient(patient)
                .patientIdentifierType(testPatientIdentifierType)
                .identifier("1234567890")
                .preferred(true)
                .locationId(1)
                .build();
        patientIdentifierRepository.save(identifier);

        // Act
        PatientDto foundPatient = patientService.getPatientByIdentifier("1234567890");

        // Assert
        assertNotNull(foundPatient);
        assertEquals(patientDto.getPatientId(), foundPatient.getPatientId());
    }

    @Test
    @DisplayName("Test Get Patients By Program")
    void testGetPatientsByProgram() {
        // Arrange
        PatientDto patientDto = patientService.createPatient(patientDtoBuilder.build(), "CORRELATION_ID");
        Patient patient = patientRepository.findById(patientDto.getPatientId()).orElseThrow();
        
        PatientProgram patientProgram = PatientProgram.builder()
                .patient(patient)
                .program(testProgram)
                .dateEnrolled(LocalDate.now())
                .locationId(1)
                .build();
        patientProgramRepository.save(patientProgram);

        // Act
        List<PatientDto> programPatients = patientService.getPatientsByProgram(testProgram.getProgramId());

        // Assert
        assertEquals(1, programPatients.size());
        assertEquals(patientDto.getPatientId(), programPatients.get(0).getPatientId());
    }

    @Test
    @DisplayName("Test Get Patients By Program And Status")
    void testGetPatientsByProgramAndStatus() {
        // Arrange
        Program activeProgram = Program.builder()
                .name("Active Program")
                .programCode("AP" + System.currentTimeMillis())
                .description("Active Program Description")
                .active(true)
                .build();
        activeProgram = programRepository.save(activeProgram);

        Program inactiveProgram = Program.builder()
                .name("Inactive Program")
                .programCode("IP" + System.currentTimeMillis())
                .description("Inactive Program Description")
                .active(false)
                .build();
        inactiveProgram = programRepository.save(inactiveProgram);

        PatientDto patient1 = patientService.createPatient(patientDtoBuilder.build(), "CORRELATION_ID");
        PatientDto patient2 = patientService.createPatient(patientDtoBuilder.build(), "CORRELATION_ID");
        
        Patient patientInActiveProgram = patientRepository.findById(patient1.getPatientId()).orElseThrow();
        Patient patientInInactiveProgram = patientRepository.findById(patient2.getPatientId()).orElseThrow();
        
        patientProgramRepository.saveAll(List.of(
            PatientProgram.builder()
                    .patient(patientInActiveProgram)
                    .program(activeProgram)
                    .dateEnrolled(LocalDate.now())
                    .locationId(1)
                    .build(),
            PatientProgram.builder()
                    .patient(patientInInactiveProgram)
                    .program(inactiveProgram)
                    .dateEnrolled(LocalDate.now())
                    .locationId(1)
                    .build()
        ));

        // Act
        List<PatientDto> patientsInActiveProgram = patientService.getPatientsByProgramAndStatus(activeProgram.getProgramId(), true);
        List<PatientDto> patientsInInactiveProgram = patientService.getPatientsByProgramAndStatus(inactiveProgram.getProgramId(), false);

        // Assert
        assertEquals(1, patientsInActiveProgram.size());
        assertEquals(1, patientsInInactiveProgram.size());
        assertEquals(patient1.getPatientId(), patientsInActiveProgram.get(0).getPatientId());
        assertEquals(patient2.getPatientId(), patientsInInactiveProgram.get(0).getPatientId());
    }

    @Test
    @DisplayName("Test Get Patients By Identifier Type")
    void testGetPatientsByIdentifierType() {
        // Arrange
        PatientDto patientDto = patientService.createPatient(patientDtoBuilder.build(), "CORRELATION_ID");
        Patient patient = patientRepository.findById(patientDto.getPatientId()).orElseThrow();
        
        PatientIdentifier identifier = PatientIdentifier.builder()
                .patient(patient)
                .patientIdentifierType(testPatientIdentifierType)
                .identifier("1234567890")
                .preferred(true)
                .locationId(1)
                .build();
        patientIdentifierRepository.save(identifier);

        // Act
        List<PatientDto> patients = patientService.getPatientsByIdentifierType(testPatientIdentifierType.getPatientIdentifierTypeId());

        // Assert
        assertEquals(1, patients.size());
        assertEquals(patientDto.getPatientId(), patients.get(0).getPatientId());
    }

    @Test
    @DisplayName("Test Patient Not Found Exception")
    void testPatientNotFoundException() {
        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatient(999L, "Correlation ID"));
        assertThrows(ResourceNotFoundException.class, () -> patientService.getPatientByIdentifier("nonexistent"));
    }
} 