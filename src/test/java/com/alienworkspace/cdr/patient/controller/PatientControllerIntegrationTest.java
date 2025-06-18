package com.alienworkspace.cdr.patient.controller;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierTypeRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.alienworkspace.cdr.patient.helpers.Constants.PATIENT_BASE_URL;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class PatientControllerIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    @BeforeEach
    void setUp() {
        patientRepository.deleteAll();
        patientIdentifierTypeRepository.deleteAll();
    }

    @DisplayName("Test Create Patient")
    @Test
    void testCreatePatient() throws Exception {
        // Arrange
        PatientDto patientDto = PatientDto.builder()
                .patientId(1L)
                .allergies("None")
                .build();

        // Act
        ResultActions result = mockMvc.perform(
                post(PATIENT_BASE_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(patientDto))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.patientId").value(greaterThan(0)))
                .andExpect(jsonPath("$.allergies").value(patientDto.getAllergies()))
                .andExpect(jsonPath("$.createdAt").value(CoreMatchers.notNullValue()))
                .andExpect(jsonPath("$.createdBy").value(CoreMatchers.notNullValue()));
    }

    @DisplayName("Test Update Patient")
    @Test
    void testUpdatePatient() throws Exception {
        // Arrange
        Patient patient = Patient.builder()
                .allergies("None")
                .build();

        Patient savedPatient = patientRepository.save(patient);

        PatientDto updateDto = PatientDto.builder()
                .patientId(savedPatient.getPatientId())
                .allergies("Penicillin")
                .build();

        // Act
        ResultActions result = mockMvc.perform(
                put(PATIENT_BASE_URL + "/{patientId}", savedPatient.getPatientId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(savedPatient.getPatientId()))
                .andExpect(jsonPath("$.allergies").value(updateDto.getAllergies()))
                .andExpect(jsonPath("$.lastModifiedAt").value(notNullValue()))
                .andExpect(jsonPath("$.lastModifiedBy").value(notNullValue()));
    }

    @DisplayName("Test Update Non-existing Patient")
    @Test
    void testUpdateNonExistingPatient() throws Exception {
        // Arrange
        PatientDto updateDto = PatientDto.builder()
                .patientId(999L)
                .allergies("Penicillin")
                .build();

        // Act
        ResultActions result = mockMvc.perform(
                put(PATIENT_BASE_URL + "/{patientId}", 999)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updateDto))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.errorMessage")
                        .value(CoreMatchers.containsString("Patient with Id of '999' not found")));
    }

    @DisplayName("Test Get Patient")
    @Test
    void testGetPatient() throws Exception {
        // Arrange
        Patient patient = Patient.builder()
                .allergies("None")
                .build();

        Patient savedPatient = patientRepository.save(patient);

        // Act
        ResultActions result = mockMvc.perform(
                get(PATIENT_BASE_URL + "/{patientId}", savedPatient.getPatientId())
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(savedPatient.getPatientId()))
                .andExpect(jsonPath("$.allergies").value(patient.getAllergies()));
    }

    @DisplayName("Test Get All Patients")
    @Test
    void testGetAllPatients() throws Exception {
        // Arrange
        List<Patient> patients = List.of(
                Patient.builder()
                        .allergies("None")
                        .build(),
                Patient.builder()
                        .allergies("Penicillin")
                        .build()
        );

        patientRepository.saveAll(patients);

        // Act
        ResultActions result = mockMvc.perform(get(PATIENT_BASE_URL));

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(patients.size()));
    }

    @DisplayName("Test Delete Patient")
    @Test
    void testDeletePatient() throws Exception {
        // Arrange
        Patient patient = Patient.builder()
                .allergies("None")
                .build();

        Patient savedPatient = patientRepository.save(patient);

        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test deletion")
                .build();

        // Act
        ResultActions result = mockMvc.perform(
                delete(PATIENT_BASE_URL + "/{patientId}", savedPatient.getPatientId())
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voidRequest))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isNoContent());

        // Verify patient is voided
        ResultActions getResult = mockMvc.perform(
                get(PATIENT_BASE_URL + "/{patientId}", savedPatient.getPatientId())
        );

        getResult.andExpect(status().isOk())
                .andExpect(jsonPath("$.voided").value(true));
    }

    @DisplayName("Test Delete Non-existing Patient")
    @Test
    void testDeleteNonExistingPatient() throws Exception {
        // Arrange
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test deletion")
                .build();

        // Act
        ResultActions result = mockMvc.perform(
                delete(PATIENT_BASE_URL + "/{patientId}", 999)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(voidRequest))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.errorMessage")
                        .value(CoreMatchers.containsString("Patient with Id of '999' not found")));
    }

    @DisplayName("Test Get Patient by Identifier")
    @Test
    void testGetPatientByIdentifier() throws Exception {
        // Arrange
        Patient patient = Patient.builder()
                .allergies("None")
                .build();

        Patient savedPatient = patientRepository.save(patient);
        PatientIdentifierType savedPatientIdentifierType = patientIdentifierTypeRepository.save(
                PatientIdentifierType.builder()
                        .name("MRN")
                        .description("Medical Record Number")
                        .build()
        );

        // Add identifier to patient
        PatientIdentifierDto identifierDto = PatientIdentifierDto.builder()
                .identifier("12345")
                .patientId(savedPatient.getPatientId())
                .locationId(1)
                .identifierTypeId(savedPatientIdentifierType.getPatientIdentifierTypeId())
                .preferred(true)
                .build();


        mockMvc.perform(
                post(PATIENT_BASE_URL + "/identifier")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(identifierDto))
        );

        // Act
        ResultActions result = mockMvc.perform(
                get(PATIENT_BASE_URL + "/identifier/{identifier}", "12345")
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.patientId").value(savedPatient.getPatientId()))
                .andExpect(jsonPath("$.allergies").value(patient.getAllergies()));
    }

    @DisplayName("Test Get Patients by Program")
    @Test
    void testGetPatientsByProgram() throws Exception {
        // Arrange
        Patient patient = Patient.builder()
                .allergies("None")
                .build();

        Patient savedPatient = patientRepository.save(patient);

        // Act
        ResultActions result = mockMvc.perform(
                get(PATIENT_BASE_URL + "/program/{programId}", 1)
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
} 