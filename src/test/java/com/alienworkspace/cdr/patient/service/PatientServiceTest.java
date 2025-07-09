package com.alienworkspace.cdr.patient.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import com.alienworkspace.cdr.patient.model.mapper.PatientMapper;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import com.alienworkspace.cdr.patient.service.impl.PatientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientIdentifierRepository patientIdentifierRepository;

    @Mock
    private PatientMapper patientMapper;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient testPatient;
    private PatientDto testPatientDto;
    private PatientIdentifier testPatientIdentifier;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .patientId(1L)
                .allergies("None")
                .build();

        testPatientDto = PatientDto.builder()
                .patientId(1L)
                .allergies("None")
                .build();

        testPatientIdentifier = PatientIdentifier.builder()
                .patient(testPatient)
                .identifier("12345")
                .build();
    }

    @Test
    @DisplayName("Test Create Patient - Success")
    void testCreatePatient() {
        // Arrange
        when(patientMapper.toPatient(any(PatientDto.class))).thenReturn(testPatient);
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);
        when(patientMapper.toPatientDto(any(Patient.class))).thenReturn(testPatientDto);

        // Act
        PatientDto result = patientService.createPatient(testPatientDto);

        // Assert
        assertNotNull(result);
        assertEquals(testPatientDto.getPatientId(), result.getPatientId());
        assertEquals(testPatientDto.getAllergies(), result.getAllergies());
        verify(patientRepository).save(any(Patient.class));
    }

    @Test
    @DisplayName("Test Update Patient - Success")
    void testUpdatePatient() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);
        when(patientMapper.toPatientDto(any(Patient.class))).thenReturn(testPatientDto);

        PatientDto updateDto = PatientDto.builder()
                .allergies("Penicillin")
                .build();

        // Act
        PatientDto result = patientService.updatePatient(1L, updateDto);

        // Assert
        assertNotNull(result);
        verify(patientRepository).findById(1L);
        verify(patientRepository).save(testPatient);
    }

    @Test
    @DisplayName("Test Update Patient - Not Found")
    void testUpdatePatientNotFound() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientService.updatePatient(1L, testPatientDto));
        verify(patientRepository).findById(1L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Test Delete Patient - Success")
    void testDeletePatient() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(patientRepository.save(any(Patient.class))).thenReturn(testPatient);
        when(patientMapper.toPatientDto(any(Patient.class))).thenReturn(testPatientDto);

        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test reason")
                .build();

        // Act
        patientService.deletePatient(1L, voidRequest);

        // Assert
        verify(patientRepository).findById(1L);
        verify(patientRepository).save(argThat(patient -> {
            assertTrue(patient.isVoided());
            assertEquals("Test reason", patient.getVoidReason());
            assertNotNull(patient.getVoidedAt());
            assertEquals(1L, patient.getVoidedBy());
            return true;
        }));
    }

    @Test
    @DisplayName("Test Delete Patient - Not Found")
    void testDeletePatientNotFound() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test reason")
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientService.deletePatient(1L, voidRequest));
        verify(patientRepository).findById(1L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    @DisplayName("Test Get Patient - Success")
    void testGetPatient() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(patientMapper.toPatientDto(any(Patient.class))).thenReturn(testPatientDto);

        // Act
        PatientDto result = patientService.getPatient(1L);

        // Assert
        assertNotNull(result);
        assertEquals(testPatientDto.getPatientId(), result.getPatientId());
        verify(patientRepository).findById(1L);
    }

    @Test
    @DisplayName("Test Get Patient - Not Found")
    void testGetPatientNotFound() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientService.getPatient(1L));
        verify(patientRepository).findById(1L);
    }

    @Test
    @DisplayName("Test Get Patient By Identifier - Success")
    void testGetPatientByIdentifier() {
        // Arrange
        when(patientIdentifierRepository.findPatientIdentifierByIdentifier(anyString()))
                .thenReturn(Optional.of(testPatientIdentifier));
        when(patientMapper.toPatientDto(any(Patient.class))).thenReturn(testPatientDto);

        // Act
        PatientDto result = patientService.getPatientByIdentifier("12345");

        // Assert
        assertNotNull(result);
        assertEquals(testPatientDto.getPatientId(), result.getPatientId());
        verify(patientIdentifierRepository).findPatientIdentifierByIdentifier("12345");
    }

    @Test
    @DisplayName("Test Get Patient By Identifier - Not Found")
    void testGetPatientByIdentifierNotFound() {
        // Arrange
        when(patientIdentifierRepository.findPatientIdentifierByIdentifier(anyString()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientService.getPatientByIdentifier("12345"));
        verify(patientIdentifierRepository).findPatientIdentifierByIdentifier("12345");
    }

    @Test
    @DisplayName("Test Get All Patients")
    void testGetAllPatients() {
        // Arrange
        List<Patient> patients = Arrays.asList(
                testPatient,
                Patient.builder().patientId(2L).allergies("Peanuts").build()
        );
        List<PatientDto> patientDtos = Arrays.asList(
                testPatientDto,
                PatientDto.builder().patientId(2L).allergies("Peanuts").build()
        );

        when(patientRepository.findAll()).thenReturn(patients);
        when(patientMapper.toPatientDto(any(Patient.class)))
                .thenReturn(patientDtos.get(0), patientDtos.get(1));

        // Act
        List<PatientDto> results = patientService.getAllPatients();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(patientRepository).findAll();
        verify(patientMapper, times(2)).toPatientDto(any(Patient.class));
    }

    @Test
    @DisplayName("Test Get All Patients Both Voided")
    void testGetAllPatientsBothVoided() {
        // Arrange
        Patient voidedPatient = Patient.builder()
                .patientId(2L)
                .allergies("Peanuts")
                .build();
        voidedPatient.setVoided(true);

        List<Patient> patients = Arrays.asList(testPatient, voidedPatient);

        PatientDto voidedPatientDto = PatientDto.builder()
                .patientId(2L)
                .allergies("Peanuts")
                .build();
        voidedPatientDto.setVoided(true);

        List<PatientDto> patientDtos = Arrays.asList(testPatientDto, voidedPatientDto);

        when(patientRepository.findAllByVoided(true)).thenReturn(patients);
        when(patientMapper.toPatientDto(any(Patient.class)))
                .thenReturn(patientDtos.get(0), patientDtos.get(1));

        // Act
        List<PatientDto> results = patientService.getAllPatientsBothVoided();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(patientRepository).findAllByVoided(true);
        verify(patientMapper, times(2)).toPatientDto(any(Patient.class));
    }

    @Test
    @DisplayName("Test Get Patients By Program")
    void testGetPatientsByProgram() {
        // Arrange
        List<Patient> patients = Arrays.asList(testPatient);
        List<PatientDto> patientDtos = Arrays.asList(testPatientDto);

        when(patientRepository.findByProgram(anyInt())).thenReturn(patients);
        when(patientMapper.toPatientDto(any(Patient.class))).thenReturn(patientDtos.get(0));

        // Act
        List<PatientDto> results = patientService.getPatientsByProgram(1);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(patientRepository).findByProgram(1);
        verify(patientMapper).toPatientDto(any(Patient.class));
    }

    @Test
    @DisplayName("Test Get Patients By Identifier Type")
    void testGetPatientsByIdentifierType() {
        // Arrange
        List<Patient> patients = Arrays.asList(testPatient);
        List<PatientDto> patientDtos = Arrays.asList(testPatientDto);

        when(patientRepository.findByIdentifierType(anyInt())).thenReturn(patients);
        when(patientMapper.toPatientDto(any(Patient.class))).thenReturn(patientDtos.get(0));

        // Act
        List<PatientDto> results = patientService.getPatientsByIdentifierType(1);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(patientRepository).findByIdentifierType(1);
        verify(patientMapper).toPatientDto(any(Patient.class));
    }

    @Test
    @DisplayName("Test Get Patients By Program And Status")
    void testGetPatientsByProgramAndStatus() {
        // Arrange
        List<Patient> patients = Collections.singletonList(testPatient);
        List<PatientDto> patientDtos = Collections.singletonList(testPatientDto);

        when(patientRepository.findByProgramAndStatus(anyInt(), anyBoolean())).thenReturn(patients);
        when(patientMapper.toPatientDto(any(Patient.class))).thenReturn(patientDtos.get(0));

        // Act
        List<PatientDto> results = patientService.getPatientsByProgramAndStatus(1, true);

        // Assert
        assertNotNull(results);
        assertEquals(1, results.size());
        verify(patientRepository).findByProgramAndStatus(1, true);
        verify(patientMapper).toPatientDto(any(Patient.class));
    }
} 