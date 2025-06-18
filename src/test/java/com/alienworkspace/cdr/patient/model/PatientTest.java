package com.alienworkspace.cdr.patient.model;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Patient model class.
 */
class PatientTest {

    @Test
    void testPatientBuilder() {
        // Arrange & Act
        Patient patient = Patient.builder()
                .patientId(1L)
                .allergies("Penicillin")
                .build();

        // Assert
        assertEquals(1L, patient.getPatientId());
        assertEquals("Penicillin", patient.getAllergies());
        assertNotNull(patient.getPatientIdentifiers());
        assertNotNull(patient.getPatientPrograms());
        assertTrue(patient.getPatientIdentifiers().isEmpty());
        assertTrue(patient.getPatientPrograms().isEmpty());
    }

    @Test
    void testSetAndGetAllergies() {
        // Arrange
        Patient patient = new Patient();
        String allergies = "Peanuts, Shellfish";

        // Act
        patient.setAllergies(allergies);

        // Assert
        assertEquals(allergies, patient.getAllergies());
    }

    @Test
    void testPatientIdentifiersImmutability() {
        // Arrange
        Patient patient = new Patient();
        Set<PatientIdentifier> identifiers = patient.getPatientIdentifiers();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> 
            identifiers.add(PatientIdentifier.builder().build())
        );
    }

    @Test
    void testPatientProgramsImmutability() {
        // Arrange
        Patient patient = new Patient();
        Set<PatientProgram> programs = patient.getPatientPrograms();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> 
            programs.add(PatientProgram.builder().build())
        );
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Patient patient = new Patient();

        // Assert
        assertNotNull(patient);
        assertNotNull(patient.getPatientIdentifiers());
        assertNotNull(patient.getPatientPrograms());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Set<PatientIdentifier> identifiers = Set.of();
        Set<PatientProgram> programs = Set.of();

        // Act
        Patient patient = new Patient(1L, "None", identifiers, programs, null);

        // Assert
        assertEquals(1L, patient.getPatientId());
        assertEquals("None", patient.getAllergies());
        assertNotNull(patient.getPatientIdentifiers());
        assertNotNull(patient.getPatientPrograms());
    }
} 