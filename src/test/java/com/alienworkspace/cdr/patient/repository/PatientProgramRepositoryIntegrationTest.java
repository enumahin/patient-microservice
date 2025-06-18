package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientProgram;
import com.alienworkspace.cdr.patient.model.Program;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientProgramRepositoryIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private PatientProgramRepository patientProgramRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ProgramRepository programRepository;

    private Patient testPatient;
    private Program testProgram;
    private PatientProgram.PatientProgramBuilder patientProgramBuilder;

    @BeforeEach
    void setUp() {
        // Clean up existing data
        patientProgramRepository.deleteAll();
        patientRepository.deleteAll();
        programRepository.deleteAll();

        // Create test patient
        testPatient = Patient.builder()
                .allergies("None")
                .build();
        testPatient = patientRepository.save(testPatient);

        // Create test program
        testProgram = Program.builder()
                .name("Test Program")
                .programCode("TP")
                .description("Test Description")
                .active(true)
                .build();
        testProgram = programRepository.save(testProgram);

        // Initialize the patient program builder
        patientProgramBuilder = PatientProgram.builder()
                .patient(testPatient)
                .program(testProgram)
                .locationId(1)
                .dateEnrolled(LocalDate.now());
    }

    @Test
    @DisplayName("Test Enroll Patient in Program")
    void testEnrollPatientInProgram() {
        // Arrange
        PatientProgram patientProgram = patientProgramBuilder.build();

        // Act
        PatientProgram savedPatientProgram = patientProgramRepository.save(patientProgram);

        // Assert
        assertNotNull(savedPatientProgram);
        assertEquals(testPatient.getPatientId(), savedPatientProgram.getPatient().getPatientId());
        assertEquals(testProgram.getProgramId(), savedPatientProgram.getProgram().getProgramId());
        assertEquals(1, savedPatientProgram.getLocationId());
        assertNotNull(savedPatientProgram.getDateEnrolled());
    }

    @Test
    @DisplayName("Test Find Patient Program by Patient ID and Program ID")
    void testFindByPatientIdAndProgramId() {
        // Arrange
        PatientProgram patientProgram = patientProgramBuilder.build();
        patientProgramRepository.save(patientProgram);

        // Act
        var foundPatientProgram = patientProgramRepository.findByPatientIdAndProgramId(
                testPatient.getPatientId(),
                testProgram.getProgramId()
        );

        // Assert
        assertTrue(foundPatientProgram.isPresent());
        assertEquals(testPatient.getPatientId(), foundPatientProgram.get().getPatient().getPatientId());
        assertEquals(testProgram.getProgramId(), foundPatientProgram.get().getProgram().getProgramId());
    }

    @Test
    @DisplayName("Test Complete Patient Program")
    void testCompletePatientProgram() {
        // Arrange
        LocalDate completionDate = LocalDate.now();
        PatientProgram patientProgram = patientProgramBuilder
                .dateCompleted(completionDate)
                .outcomeConceptId(1)
                .outcomeComment("Completed successfully")
                .build();

        // Act
        PatientProgram savedPatientProgram = patientProgramRepository.save(patientProgram);

        // Assert
        assertNotNull(savedPatientProgram);
        assertEquals(completionDate, savedPatientProgram.getDateCompleted());
        assertEquals(1, savedPatientProgram.getOutcomeConceptId());
        assertEquals("Completed successfully", savedPatientProgram.getOutcomeComment());
    }

    @Test
    @DisplayName("Test Patient Program Not Found")
    void testPatientProgramNotFound() {
        // Act
        var result = patientProgramRepository.findByPatientIdAndProgramId(999L, 999);

        // Assert
        assertTrue(result.isEmpty());
    }
} 