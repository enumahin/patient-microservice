package com.alienworkspace.cdr.patient.model;

import org.junit.jupiter.api.Test;
import java.util.Set;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the PatientIdentifierType model class.
 */
class PatientIdentifierTypeTest {

    @Test
    void testPatientIdentifierTypeBuilder() {
        // Act
        PatientIdentifierType identifierType = PatientIdentifierType.builder()
                .patientIdentifierTypeId(1)
                .name("National ID")
                .description("National Identity Number")
                .format("^[0-9]{10}$")
                .required(true)
                .isUnique(true)
                .formatHint("10 digit number")
                .validator("org.openmrs.patient.impl.LuhnIdentifierValidator")
                .build();

        // Assert
        assertEquals(1, identifierType.getPatientIdentifierTypeId());
        assertEquals("National ID", identifierType.getName());
        assertEquals("National Identity Number", identifierType.getDescription());
        assertEquals("^[0-9]{10}$", identifierType.getFormat());
        assertTrue(identifierType.isRequired());
        assertTrue(identifierType.isUnique());
        assertEquals("10 digit number", identifierType.getFormatHint());
        assertEquals("org.openmrs.patient.impl.LuhnIdentifierValidator", identifierType.getValidator());
        assertNotNull(identifierType.getPatientIdentifiers());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        PatientIdentifierType identifierType = new PatientIdentifierType();

        // Assert
        assertNotNull(identifierType);
        assertNotNull(identifierType.getPatientIdentifiers());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        PatientIdentifierType identifierType = new PatientIdentifierType();

        // Act
        identifierType.setPatientIdentifierTypeId(1);
        identifierType.setName("National ID");
        identifierType.setDescription("National Identity Number");
        identifierType.setFormat("^[0-9]{10}$");
        identifierType.setRequired(true);
        identifierType.setUnique(true);
        identifierType.setFormatHint("10 digit number");
        identifierType.setValidator("org.openmrs.patient.impl.LuhnIdentifierValidator");

        // Assert
        assertEquals(1, identifierType.getPatientIdentifierTypeId());
        assertEquals("National ID", identifierType.getName());
        assertEquals("National Identity Number", identifierType.getDescription());
        assertEquals("^[0-9]{10}$", identifierType.getFormat());
        assertTrue(identifierType.isRequired());
        assertTrue(identifierType.isUnique());
        assertEquals("10 digit number", identifierType.getFormatHint());
        assertEquals("org.openmrs.patient.impl.LuhnIdentifierValidator", identifierType.getValidator());
    }

    @Test
    void testPatientIdentifiersImmutability() {
        // Arrange
        PatientIdentifierType identifierType = new PatientIdentifierType();
        Set<PatientIdentifier> identifiers = identifierType.getPatientIdentifiers();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> 
            identifiers.add(PatientIdentifier.builder().build())
        );
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Set<PatientIdentifier> identifiers = Set.of();

        // Act
        PatientIdentifierType identifierType = new PatientIdentifierType(
                1, "National ID", "National Identity Number", "^[0-9]{10}$",
                true, true, "10 digit number",
                "org.openmrs.patient.impl.LuhnIdentifierValidator", identifiers);

        // Assert
        assertEquals(1, identifierType.getPatientIdentifierTypeId());
        assertEquals("National ID", identifierType.getName());
        assertEquals("National Identity Number", identifierType.getDescription());
        assertEquals("^[0-9]{10}$", identifierType.getFormat());
        assertTrue(identifierType.isRequired());
        assertTrue(identifierType.isUnique());
        assertEquals("10 digit number", identifierType.getFormatHint());
        assertEquals("org.openmrs.patient.impl.LuhnIdentifierValidator", identifierType.getValidator());
        assertNotNull(identifierType.getPatientIdentifiers());
    }
} 