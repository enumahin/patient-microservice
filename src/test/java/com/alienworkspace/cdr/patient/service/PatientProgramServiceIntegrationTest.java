package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientProgramDto;
import com.alienworkspace.cdr.patient.exception.AlreadyExistException;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientProgram;
import com.alienworkspace.cdr.patient.model.Program;
import com.alienworkspace.cdr.patient.repository.PatientProgramRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import com.alienworkspace.cdr.patient.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientProgramServiceIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private PatientProgramService patientProgramService;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private PatientProgramRepository patientProgramRepository;

    private Patient testPatient;
    private Program testProgram;
    private PatientProgramDto.PatientProgramDtoBuilder programDtoBuilder;

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
                .programCode("TP" + System.currentTimeMillis())
                .description("Test Description")
                .active(true)
                .build();
        testProgram = programRepository.save(testProgram);

        // Initialize program DTO builder
        programDtoBuilder = PatientProgramDto.builder()
                .dateEnrolled(LocalDate.now())
                .locationId(1);
    }

    @Test
    @DisplayName("Test Enroll Patient in Program")
    void testEnrollPatientInProgram() {
        // Arrange
        PatientProgramDto programDto = programDtoBuilder.build();

        // Act
        patientProgramService.enrollPatientInProgram(
                testPatient.getPatientId(),
                testProgram.getProgramId(),
                programDto
        );

        // Assert
        Optional<PatientProgram> enrollment = patientProgramRepository
                .findByPatientIdAndProgramId(testPatient.getPatientId(), testProgram.getProgramId());
        
        assertTrue(enrollment.isPresent());
        assertEquals(testPatient.getPatientId(), enrollment.get().getPatient().getPatientId());
        assertEquals(testProgram.getProgramId(), enrollment.get().getProgram().getProgramId());
        assertEquals(programDto.getDateEnrolled(), enrollment.get().getDateEnrolled());
        assertEquals(programDto.getLocationId(), enrollment.get().getLocationId());
    }

    @Test
    @DisplayName("Test Cannot Enroll Patient in Same Program Twice")
    void testCannotEnrollPatientInSameProgramTwice() {
        // Arrange
        PatientProgramDto programDto = programDtoBuilder.build();
        patientProgramService.enrollPatientInProgram(
                testPatient.getPatientId(),
                testProgram.getProgramId(),
                programDto
        );

        // Act & Assert
        assertThrows(AlreadyExistException.class, () ->
                patientProgramService.enrollPatientInProgram(
                        testPatient.getPatientId(),
                        testProgram.getProgramId(),
                        programDto
                )
        );
    }

    @Test
    @DisplayName("Test Update Program Enrollment")
    void testUpdateProgramEnrollment() {
        // Arrange
        PatientProgramDto enrollDto = programDtoBuilder.build();
        patientProgramService.enrollPatientInProgram(
                testPatient.getPatientId(),
                testProgram.getProgramId(),
                enrollDto
        );

        LocalDate completionDate = LocalDate.now();
        PatientProgramDto updateDto = PatientProgramDto.builder()
                .dateCompleted(completionDate)
                .outcomeConceptId(1)
                .outcomeComment("Successfully completed")
                .build();

        // Act
        patientProgramService.updateProgramEnrollment(
                testPatient.getPatientId(),
                testProgram.getProgramId(),
                updateDto
        );

        // Assert
        Optional<PatientProgram> enrollment = patientProgramRepository
                .findByPatientIdAndProgramId(testPatient.getPatientId(), testProgram.getProgramId());
        
        assertTrue(enrollment.isPresent());
        assertEquals(completionDate, enrollment.get().getDateCompleted());
        assertEquals(1, enrollment.get().getOutcomeConceptId());
        assertEquals("Successfully completed", enrollment.get().getOutcomeComment());
    }

    @Test
    @DisplayName("Test Enroll Patient - Patient Not Found")
    void testEnrollPatientNotFound() {
        // Arrange
        PatientProgramDto programDto = programDtoBuilder.build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                patientProgramService.enrollPatientInProgram(
                        999L,
                        testProgram.getProgramId(),
                        programDto
                )
        );
    }

    @Test
    @DisplayName("Test Enroll Patient - Program Not Found")
    void testEnrollProgramNotFound() {
        // Arrange
        PatientProgramDto programDto = programDtoBuilder.build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                patientProgramService.enrollPatientInProgram(
                        testPatient.getPatientId(),
                        999,
                        programDto
                )
        );
    }

    @Test
    @DisplayName("Test Update Enrollment - Not Found")
    void testUpdateEnrollmentNotFound() {
        // Arrange
        PatientProgramDto updateDto = programDtoBuilder
                .dateCompleted(LocalDate.now())
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () ->
                patientProgramService.updateProgramEnrollment(
                        testPatient.getPatientId(),
                        999,
                        updateDto
                )
        );
    }
} 