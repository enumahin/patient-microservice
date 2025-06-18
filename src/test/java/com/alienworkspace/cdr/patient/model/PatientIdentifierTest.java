package com.alienworkspace.cdr.patient.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PatientIdentifier model class.
 */
class PatientIdentifierTest {

    @Test
    void testPatientIdentifierBuilder() {
        // Arrange
        Patient patient = Patient.builder().patientId(1L).build();
        PatientIdentifierType identifierType = PatientIdentifierType.builder()
                .patientIdentifierTypeId(1)
                .name("National ID")
                .build();

        // Act
        PatientIdentifier identifier = PatientIdentifier.builder()
                .patientIdentifierId(1L)
                .patient(patient)
                .patientIdentifierType(identifierType)
                .identifier("12345")
                .preferred(true)
                .locationId(1)
                .build();

        // Assert
        assertEquals(1L, identifier.getPatientIdentifierId());
        assertEquals(patient, identifier.getPatient());
        assertEquals(identifierType, identifier.getPatientIdentifierType());
        assertEquals("12345", identifier.getIdentifier());
        assertTrue(identifier.isPreferred());
        assertEquals(1, identifier.getLocationId());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        PatientIdentifier identifier = new PatientIdentifier();

        // Assert
        assertNotNull(identifier);
        assertTrue(identifier.isPreferred()); // Default value should be true
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        PatientIdentifier identifier = new PatientIdentifier();
        Patient patient = Patient.builder().patientId(1L).build();
        PatientIdentifierType identifierType = PatientIdentifierType.builder()
                .patientIdentifierTypeId(1)
                .name("National ID")
                .build();

        // Act
        identifier.setPatientIdentifierId(1L);
        identifier.setPatient(patient);
        identifier.setPatientIdentifierType(identifierType);
        identifier.setIdentifier("12345");
        identifier.setPreferred(false);
        identifier.setLocationId(1);

        // Assert
        assertEquals(1L, identifier.getPatientIdentifierId());
        assertEquals(patient, identifier.getPatient());
        assertEquals(identifierType, identifier.getPatientIdentifierType());
        assertEquals("12345", identifier.getIdentifier());
        assertFalse(identifier.isPreferred());
        assertEquals(1, identifier.getLocationId());
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Patient patient = Patient.builder().patientId(1L).build();
        PatientIdentifierType identifierType = PatientIdentifierType.builder()
                .patientIdentifierTypeId(1)
                .name("National ID")
                .build();

        // Act
        PatientIdentifier identifier = new PatientIdentifier(
                1L, patient, identifierType, "12345", true, 1);

        // Assert
        assertEquals(1L, identifier.getPatientIdentifierId());
        assertEquals(patient, identifier.getPatient());
        assertEquals(identifierType, identifier.getPatientIdentifierType());
        assertEquals("12345", identifier.getIdentifier());
        assertTrue(identifier.isPreferred());
        assertEquals(1, identifier.getLocationId());
    }
} 