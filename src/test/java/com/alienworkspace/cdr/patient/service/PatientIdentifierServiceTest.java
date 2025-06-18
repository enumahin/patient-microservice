package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.AlreadyExistException;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.helpers.CurrentUser;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import com.alienworkspace.cdr.patient.model.mapper.PatientIdentifierMapper;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierRepository;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierTypeRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import com.alienworkspace.cdr.patient.service.impl.PatientIdentifierServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientIdentifierServiceTest {

    @Mock
    private PatientIdentifierRepository patientIdentifierRepository;

    @Mock
    private PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private PatientIdentifierMapper patientIdentifierMapper;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private PatientIdentifierServiceImpl patientIdentifierService;

    @Captor
    private ArgumentCaptor<PatientIdentifier> patientIdentifierCaptor;

    private Patient testPatient;
    private PatientIdentifierType testPatientIdentifierType;
    private PatientIdentifier testPatientIdentifier;
    private PatientIdentifier testNotPreferredPatientIdentifier;
    private PatientIdentifierDto testPatientIdentifierDto;
    private PatientIdentifierDto testNotPreferredPatientIdentifierDto;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .patientId(1L)
                .allergies("None")
                .build();

        testPatientIdentifierType = PatientIdentifierType.builder()
                .patientIdentifierTypeId(1)
                .name("National ID")
                .description("National Identity Number")
                .format("^[0-9]{10}$")
                .required(true)
                .isUnique(true)
                .build();


        testPatientIdentifier = PatientIdentifier.builder()
                .identifier("1234567890")
                .preferred(true)
                .patient(testPatient)
                .patientIdentifierType(testPatientIdentifierType)
                .build();


        testNotPreferredPatientIdentifier = PatientIdentifier.builder()
                .identifier("1234567890")
                .preferred(false)
                .patient(testPatient)
                .patientIdentifierType(testPatientIdentifierType)
                .build();

        testPatientIdentifierDto = PatientIdentifierDto.builder()
                .identifier("1234567890")
                .preferred(true)
                .patientId(1L)
                .identifierTypeId(1)
                .build();

        testNotPreferredPatientIdentifierDto = PatientIdentifierDto.builder()
                .identifier("1234567890")
                .preferred(false)
                .patientId(1L)
                .identifierTypeId(1)
                .build();
    }

    @Test
    @DisplayName("Test Save Patient Identifier - Success")
    void testSavePatientIdentifier() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(patientIdentifierTypeRepository.findById(anyInt())).thenReturn(Optional.of(testPatientIdentifierType));
        when(patientIdentifierMapper.toPatientIdentifier(any(PatientIdentifierDto.class)))
                .thenReturn(testPatientIdentifier);
        when(patientIdentifierRepository.save(any(PatientIdentifier.class)))
                .thenReturn(testPatientIdentifier);
        when(patientIdentifierMapper.toPatientIdentifierDto(any(PatientIdentifier.class)))
                .thenReturn(testPatientIdentifierDto);

        // Act
        PatientIdentifierDto result = patientIdentifierService.savePatientIdentifier(testPatientIdentifierDto);

        // Assert
        assertNotNull(result);
        assertEquals(testPatientIdentifierDto.getIdentifier(), result.getIdentifier());
        assertEquals(testPatientIdentifierDto.isPreferred(), result.isPreferred());
        verify(patientIdentifierRepository).save(any(PatientIdentifier.class));
    }

    @Test
    @DisplayName("Test Save Patient Identifier - Patient Not Found")
    void testSavePatientIdentifierPatientNotFound() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierService.savePatientIdentifier(testPatientIdentifierDto));
        verify(patientIdentifierRepository, never()).save(any(PatientIdentifier.class));
    }

    @Test
    @DisplayName("Test Save Patient Identifier - Identifier Type Not Found")
    void testSavePatientIdentifierIdentifierTypeNotFound() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(patientIdentifierTypeRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierService.savePatientIdentifier(testPatientIdentifierDto));
        verify(patientIdentifierRepository, never()).save(any(PatientIdentifier.class));
    }

    @Test
    @DisplayName("Test Update Patient Identifier - Success")
    void testUpdatePatientIdentifier() {
        // Arrange
        PatientIdentifierDto updateDto = PatientIdentifierDto.builder()
                .patientId(1L)
                .identifierTypeId(1)
                .preferred(true)
                .build();

        when(patientIdentifierRepository.finedByPatientIdAndIdentifierTypeId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(testNotPreferredPatientIdentifier));
        when(patientIdentifierRepository.save(any(PatientIdentifier.class)))
                .thenReturn(testPatientIdentifier);
        when(patientIdentifierMapper.toPatientIdentifierDto(any(PatientIdentifier.class)))
                .thenReturn(testPatientIdentifierDto);

        // Act
        patientIdentifierService.updatePatientIdentifier(1L, updateDto);

        // Assert
        verify(patientIdentifierRepository).save(patientIdentifierCaptor.capture());
        PatientIdentifier capturedIdentifier = patientIdentifierCaptor.getValue();
        assertEquals(updateDto.isPreferred(), capturedIdentifier.isPreferred());
    }

    @Test
    @DisplayName("Test Update Preferred Patient Identifier - Failed")
    void testUpdatePreferredPatientIdentifierFailed() {
        // Arrange
        when(patientIdentifierRepository.findPatientIdentifierByIdentifierTypeAndPreferred(anyLong(), anyLong()))
                .thenReturn(Optional.of(testPatientIdentifier));

        // Act & Assert

        assertThrows(IllegalArgumentException.class,
                () -> patientIdentifierService.updatePatientIdentifier(1L,
                        testNotPreferredPatientIdentifierDto));
        verify(patientIdentifierRepository, never()).save(any(PatientIdentifier.class));
    }

    @Test
    @DisplayName("Test Update Patient Identifier - Not Found")
    void testUpdatePatientIdentifierNotFound() {
        // Arrange
        when(patientIdentifierRepository.finedByPatientIdAndIdentifierTypeId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierService.updatePatientIdentifier(1L, testPatientIdentifierDto));
        verify(patientIdentifierRepository, never()).save(any(PatientIdentifier.class));
    }

    @Test
    @DisplayName("Test Delete Patient Identifier - Success")
    void testDeletePatientIdentifier() {
        // Arrange
        when(patientIdentifierRepository.findById(anyLong()))
                .thenReturn(Optional.of(testPatientIdentifier));
        when(patientIdentifierRepository.save(any(PatientIdentifier.class)))
                .thenReturn(testPatientIdentifier);
        when(patientIdentifierMapper.toPatientIdentifierDto(any(PatientIdentifier.class)))
                .thenReturn(testPatientIdentifierDto);

        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test reason")
                .build();

        // Act
        patientIdentifierService.deletePatientIdentifier(1L, voidRequest);

        // Assert
        verify(patientIdentifierRepository).save(patientIdentifierCaptor.capture());
        PatientIdentifier capturedIdentifier = patientIdentifierCaptor.getValue();
        assertTrue(capturedIdentifier.isVoided());
        assertEquals("Test reason", capturedIdentifier.getVoidReason());
        assertNotNull(capturedIdentifier.getVoidedAt());
        assertEquals(1L, capturedIdentifier.getVoidedBy());
    }

    @Test
    @DisplayName("Test Delete Patient Identifier - Not Found")
    void testDeletePatientIdentifierNotFound() {
        // Arrange
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test reason")
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierService.deletePatientIdentifier(1L, voidRequest));
        verify(patientIdentifierRepository, never()).save(any(PatientIdentifier.class));
    }
} 