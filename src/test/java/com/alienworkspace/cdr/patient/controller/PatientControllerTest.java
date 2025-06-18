package com.alienworkspace.cdr.patient.controller;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.helpers.Constants;
import com.alienworkspace.cdr.patient.service.PatientIdentifierService;
import com.alienworkspace.cdr.patient.service.PatientProgramService;
import com.alienworkspace.cdr.patient.service.PatientService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PatientControllerTest {

    @Mock
    private PatientService patientService;

    @Mock
    private PatientIdentifierService patientIdentifierService;

    @Mock
    private PatientProgramService patientProgramService;

    @InjectMocks
    private PatientController patientController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;
    private PatientDto testPatientDto;
    private PatientIdentifierDto testPatientIdentifierDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(patientController).build();
        objectMapper = new ObjectMapper();

        testPatientDto = new PatientDto();
        testPatientIdentifierDto = PatientIdentifierDto.builder()
                .identifier("1234567890")
                .preferred(true)
                .patientId(1L)
                .identifierTypeId(1)
                .build();
    }

    @Test
    @DisplayName("GET " + Constants.PATIENT_BASE_URL + " - Get All Patients")
    void testGetAllPatients() throws Exception {
        List<PatientDto> patients = Arrays.asList(testPatientDto);
        when(patientService.getAllPatients()).thenReturn(patients);

        mockMvc.perform(get(Constants.PATIENT_BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET " + Constants.PATIENT_BASE_URL + "/{id} - Get Patient by ID")
    void testGetPatient() throws Exception {
        when(patientService.getPatient(anyLong())).thenReturn(testPatientDto);

        mockMvc.perform(get(Constants.PATIENT_BASE_URL + "/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST " + Constants.PATIENT_BASE_URL + " - Create Patient")
    void testCreatePatient() throws Exception {
        when(patientService.createPatient(any(PatientDto.class))).thenReturn(testPatientDto);

        mockMvc.perform(post(Constants.PATIENT_BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatientDto))).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("PUT " + Constants.PATIENT_BASE_URL + "/{id} - Update Patient")
    void testUpdatePatient() throws Exception {
        when(patientService.updatePatient(anyLong(), any(PatientDto.class))).thenReturn(testPatientDto);

        mockMvc.perform(put(Constants.PATIENT_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatientDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE " + Constants.PATIENT_BASE_URL + "/{id} - Delete Patient")
    void testDeletePatient() throws Exception {
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test reason")
                .build();

        doNothing().when(patientService).deletePatient(anyLong(), any(RecordVoidRequest.class));

        mockMvc.perform(delete(Constants.PATIENT_BASE_URL + "/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voidRequest)))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET " + Constants.PATIENT_BASE_URL + "/identifier/{identifier} - Get Patient by Identifier")
    void testGetPatientByIdentifier() throws Exception {
        when(patientService.getPatientByIdentifier(anyString())).thenReturn(testPatientDto);

        mockMvc.perform(get(Constants.PATIENT_BASE_URL + "/identifier/1234567890"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET " + Constants.PATIENT_BASE_URL + "/identifier/type/{identifierType} - Get Patients by Identifier Type")
    void testGetPatientByIdentifierType() throws Exception {
        List<PatientDto> patients = Arrays.asList(testPatientDto);
        when(patientService.getPatientsByIdentifierType(anyInt())).thenReturn(patients);

        mockMvc.perform(get(Constants.PATIENT_BASE_URL + "/identifier/type/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET " + Constants.PATIENT_BASE_URL + "/program/{programId} - Get Patients by Program")
    void testGetPatientsByProgram() throws Exception {
        List<PatientDto> patients = Collections.singletonList(testPatientDto);
        when(patientService.getPatientsByProgram(anyInt())).thenReturn(patients);

        mockMvc.perform(get(Constants.PATIENT_BASE_URL + "/program/{id}",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].size()").value(greaterThan(0)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET " + Constants.PATIENT_BASE_URL + "/both-voided - Get All Patients Both Voided")
    void testGetAllPatientsBothVoided() throws Exception {
        List<PatientDto> patients = Collections.singletonList(testPatientDto);
        when(patientService.getAllPatientsBothVoided()).thenReturn(patients);

        mockMvc.perform(get(Constants.PATIENT_BASE_URL + "/both-voided"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("POST " + Constants.PATIENT_BASE_URL + "/identifier - Add Patient Identifier")
    void testAddPatientIdentifier() throws Exception {
        when(patientIdentifierService.savePatientIdentifier(any(PatientIdentifierDto.class)))
                .thenReturn(testPatientIdentifierDto);

        mockMvc.perform(post(Constants.PATIENT_BASE_URL + "/identifier")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatientIdentifierDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("PUT " + Constants.PATIENT_BASE_URL + "/identifier/{id} - Update Patient Identifier")
    void testUpdatePatientIdentifier() throws Exception {
        when(patientIdentifierService.updatePatientIdentifier(anyLong(), any(PatientIdentifierDto.class)))
                .thenReturn(testPatientIdentifierDto);

        mockMvc.perform(put(Constants.PATIENT_BASE_URL + "/identifier/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPatientIdentifierDto)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE " + Constants.PATIENT_BASE_URL + "/identifier/{id} - Delete Patient Identifier")
    void testDeletePatientIdentifier() throws Exception {
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test reason")
                .build();

        doNothing().when(patientIdentifierService).deletePatientIdentifier(anyLong(), any(RecordVoidRequest.class));

        mockMvc.perform(delete(Constants.PATIENT_BASE_URL + "/identifier/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(voidRequest)))
                .andExpect(status().isOk());
    }
} 