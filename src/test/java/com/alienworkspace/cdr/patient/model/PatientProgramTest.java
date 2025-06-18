package com.alienworkspace.cdr.patient.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PatientProgram model class.
 */
class PatientProgramTest {

    @Test
    void testPatientProgramBuilder() {
        // Arrange
        Patient patient = Patient.builder().patientId(1L).build();
        Program program = Program.builder().programId(1).build();
        LocalDate enrollDate = LocalDate.now();
        LocalDate completedDate = enrollDate.plusMonths(6);

        // Act
        PatientProgram patientProgram = PatientProgram.builder()
                .patientProgramId(1L)
                .patient(patient)
                .program(program)
                .locationId(1)
                .dateEnrolled(enrollDate)
                .dateCompleted(completedDate)
                .outcomeConceptId(100)
                .outcomeComment("Completed successfully")
                .build();

        // Assert
        assertEquals(1L, patientProgram.getPatientProgramId());
        assertEquals(patient, patientProgram.getPatient());
        assertEquals(program, patientProgram.getProgram());
        assertEquals(1, patientProgram.getLocationId());
        assertEquals(enrollDate, patientProgram.getDateEnrolled());
        assertEquals(completedDate, patientProgram.getDateCompleted());
        assertEquals(100, patientProgram.getOutcomeConceptId());
        assertEquals("Completed successfully", patientProgram.getOutcomeComment());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        PatientProgram patientProgram = new PatientProgram();

        // Assert
        assertNotNull(patientProgram);
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        PatientProgram patientProgram = new PatientProgram();
        Patient patient = Patient.builder().patientId(1L).build();
        Program program = Program.builder().programId(1).build();
        LocalDate enrollDate = LocalDate.now();
        LocalDate completedDate = enrollDate.plusMonths(6);

        // Act
        patientProgram.setPatientProgramId(1L);
        patientProgram.setPatient(patient);
        patientProgram.setProgram(program);
        patientProgram.setLocationId(1);
        patientProgram.setDateEnrolled(enrollDate);
        patientProgram.setDateCompleted(completedDate);
        patientProgram.setOutcomeConceptId(100);
        patientProgram.setOutcomeComment("Completed successfully");

        // Assert
        assertEquals(1L, patientProgram.getPatientProgramId());
        assertEquals(patient, patientProgram.getPatient());
        assertEquals(program, patientProgram.getProgram());
        assertEquals(1, patientProgram.getLocationId());
        assertEquals(enrollDate, patientProgram.getDateEnrolled());
        assertEquals(completedDate, patientProgram.getDateCompleted());
        assertEquals(100, patientProgram.getOutcomeConceptId());
        assertEquals("Completed successfully", patientProgram.getOutcomeComment());
    }

    @Test
    void testEquals() {
        // Arrange
        Patient patient = Patient.builder().patientId(1L).build();
        Program program = Program.builder().programId(1).build();
        LocalDate enrollDate = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        String uuid = UUID.randomUUID().toString();

        PatientProgram program1 = PatientProgram.builder()
                .patientProgramId(1L)
                .patient(patient)
                .program(program)
                .locationId(1)
                .dateEnrolled(enrollDate)
                .build();
        program1.setCreatedAt(now);
        program1.setCreatedBy(1L);
        program1.setUuid(uuid);

        PatientProgram program2 = PatientProgram.builder()
                .patientProgramId(1L)
                .patient(patient)
                .program(program)
                .locationId(1)
                .dateEnrolled(enrollDate)
                .build();
        program2.setCreatedAt(now);
        program2.setCreatedBy(1L);
        program2.setUuid(uuid);

        PatientProgram differentProgram = PatientProgram.builder()
                .patientProgramId(2L)
                .patient(patient)
                .program(program)
                .locationId(2)
                .dateEnrolled(enrollDate.plusDays(1))
                .build();

        // Assert
        assertEquals(program1, program2);
        assertNotEquals(program1, differentProgram);
        assertNotEquals(program1, null);
        assertNotEquals(program1, new Object());
    }

    @Test
    void testHashCode() {
        // Arrange
        Patient patient = Patient.builder().patientId(1L).build();
        Program program = Program.builder().programId(1).build();
        LocalDate enrollDate = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();
        String uuid = UUID.randomUUID().toString();

        PatientProgram program1 = PatientProgram.builder()
                .patientProgramId(1L)
                .patient(patient)
                .program(program)
                .locationId(1)
                .dateEnrolled(enrollDate)
                .build();
        program1.setCreatedAt(now);
        program1.setCreatedBy(1L);
        program1.setUuid(uuid);

        PatientProgram program2 = PatientProgram.builder()
                .patientProgramId(1L)
                .patient(patient)
                .program(program)
                .locationId(1)
                .dateEnrolled(enrollDate)
                .build();
        program2.setCreatedAt(now);
        program2.setCreatedBy(1L);
        program2.setUuid(uuid);

        // Assert
        assertEquals(program1.hashCode(), program2.hashCode());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Patient patient = Patient.builder().patientId(1L).build();
        Program program = Program.builder().programId(1).build();
        LocalDate enrollDate = LocalDate.now();
        LocalDate completedDate = enrollDate.plusMonths(6);

        // Act
        PatientProgram patientProgram = new PatientProgram(1L, program, patient, 1,
                enrollDate, completedDate, 100, "Completed successfully");

        // Assert
        assertEquals(1L, patientProgram.getPatientProgramId());
        assertEquals(patient, patientProgram.getPatient());
        assertEquals(program, patientProgram.getProgram());
        assertEquals(1, patientProgram.getLocationId());
        assertEquals(enrollDate, patientProgram.getDateEnrolled());
        assertEquals(completedDate, patientProgram.getDateCompleted());
        assertEquals(100, patientProgram.getOutcomeConceptId());
        assertEquals("Completed successfully", patientProgram.getOutcomeComment());
    }
} 