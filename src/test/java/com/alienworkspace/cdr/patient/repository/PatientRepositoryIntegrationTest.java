package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.Patient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PatientRepositoryIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private PatientRepository patientRepository;

    private Patient.PatientBuilder patientBuilder = Patient.builder();

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();

        patientBuilder
                .allergies("Red Wine, Cocoa Nut");
    }

    @DisplayName("Test Add Patient.")
    @Test
    void testAddPatient() {
        // Arrange
        Patient patient = patientBuilder.build();

        // Act
        Patient actualPatient = patientRepository.save(patient);

        // Assert
        System.out.println(actualPatient);
        assertNotNull(actualPatient);
    }
}
