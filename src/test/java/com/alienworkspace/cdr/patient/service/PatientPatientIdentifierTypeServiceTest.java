package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientIdentifierTypeDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.helpers.CurrentUser;
import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import com.alienworkspace.cdr.patient.model.mapper.PatientIdentifierTypeMapper;
import com.alienworkspace.cdr.patient.repository.PatientIdentifierTypeRepository;
import com.alienworkspace.cdr.patient.service.impl.PatientIdentifierTypeServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientPatientIdentifierTypeServiceTest {

    @Mock
    private PatientIdentifierTypeRepository patientIdentifierTypeRepository;

    @Mock
    private PatientIdentifierTypeMapper patientIdentifierTypeMapper;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private PatientIdentifierTypeServiceImpl patientIdentifierTypeService;

    @Captor
    private ArgumentCaptor<PatientIdentifierType> identifierTypeCaptor;

    private PatientIdentifierType testPatientIdentifierType;
    private PatientIdentifierTypeDto testIdentifierTypeDto;

    @BeforeEach
    void setUp() {
        testPatientIdentifierType = PatientIdentifierType.builder()
                .patientIdentifierTypeId(1)
                .name("National ID")
                .description("National Identity Number")
                .format("^[0-9]{10}$")
                .required(true)
                .isUnique(true)
                .formatHint("10 digit number")
                .validator("org.openmrs.patient.impl.LuhnIdentifierValidator")
                .build();

        testIdentifierTypeDto = PatientIdentifierTypeDto.builder()
                .patientIdentifierTypeId(1)
                .name("National ID")
                .description("National Identity Number")
                .format("^[0-9]{10}$")
                .required(true)
                .unique(true)
                .formatHint("10 digit number")
                .validator("org.openmrs.patient.impl.LuhnIdentifierValidator")
                .build();
    }

    @Test
    @DisplayName("Test Create Patient Identifier Type - Success")
    void testCreatePatientIdentifierType() {
        // Arrange
        when(patientIdentifierTypeMapper.toPatientIdentifierType(any(PatientIdentifierTypeDto.class)))
                .thenReturn(testPatientIdentifierType);
        when(patientIdentifierTypeRepository.save(any(PatientIdentifierType.class)))
                .thenReturn(testPatientIdentifierType);
        when(patientIdentifierTypeMapper.toPatientIdentifierTypeDto(any(PatientIdentifierType.class)))
                .thenReturn(testIdentifierTypeDto);

        // Act
        PatientIdentifierTypeDto result = patientIdentifierTypeService
                .createPatientIdentifierType(testIdentifierTypeDto);

        // Assert
        assertNotNull(result);
        assertEquals(testIdentifierTypeDto.getPatientIdentifierTypeId(), result.getPatientIdentifierTypeId());
        assertEquals(testIdentifierTypeDto.getName(), result.getName());
        assertEquals(testIdentifierTypeDto.getDescription(), result.getDescription());
        assertEquals(testIdentifierTypeDto.getFormat(), result.getFormat());
        assertTrue(result.isRequired());
        assertTrue(result.isUnique());
        assertEquals(testIdentifierTypeDto.getFormatHint(), result.getFormatHint());
        assertEquals(testIdentifierTypeDto.getValidator(), result.getValidator());
        verify(patientIdentifierTypeRepository).save(any(PatientIdentifierType.class));
    }

    @Test
    @DisplayName("Test Update Patient Identifier Type - Success")
    void testUpdatePatientIdentifierType() {
        // Arrange
        PatientIdentifierTypeDto updateDto = PatientIdentifierTypeDto.builder()
                .description("Updated Description")
                .format("^[A-Z]{2}[0-9]{8}$")
                .required(false)
                .unique(true)
                .formatHint("2 letters followed by 8 numbers")
                .validator("org.openmrs.patient.impl.CustomValidator")
                .build();

        when(patientIdentifierTypeRepository.findById(anyInt())).thenReturn(Optional.of(testPatientIdentifierType));
        when(patientIdentifierTypeRepository.save(any(PatientIdentifierType.class))).thenReturn(testPatientIdentifierType);
        when(patientIdentifierTypeMapper.toPatientIdentifierTypeDto(any(PatientIdentifierType.class)))
                .thenReturn(testIdentifierTypeDto);

        // Act
        PatientIdentifierTypeDto result = patientIdentifierTypeService.updatePatientIdentifierType(1, updateDto);

        // Assert
        verify(patientIdentifierTypeRepository).save(identifierTypeCaptor.capture());
        PatientIdentifierType capturedType = identifierTypeCaptor.getValue();
        assertEquals(updateDto.getDescription(), capturedType.getDescription());
        assertEquals(updateDto.getFormat(), capturedType.getFormat());
        assertEquals(updateDto.isRequired(), capturedType.isRequired());
        assertEquals(updateDto.isUnique(), capturedType.isUnique());
        assertEquals(updateDto.getFormatHint(), capturedType.getFormatHint());
        assertEquals(updateDto.getValidator(), capturedType.getValidator());
    }

    @Test
    @DisplayName("Test Update Patient Identifier Type - Not Found")
    void testUpdatePatientIdentifierTypeNotFound() {
        // Arrange
        when(patientIdentifierTypeRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierTypeService.updatePatientIdentifierType(1, testIdentifierTypeDto));
        verify(patientIdentifierTypeRepository, never()).save(any(PatientIdentifierType.class));
    }

    @Test
    @DisplayName("Test Delete Patient Identifier Type - Success")
    void testDeletePatientIdentifierType() {
        // Arrange
        when(patientIdentifierTypeRepository.findById(anyInt())).thenReturn(Optional.of(testPatientIdentifierType));
        when(patientIdentifierTypeRepository.save(any(PatientIdentifierType.class))).thenReturn(testPatientIdentifierType);
        when(patientIdentifierTypeMapper.toPatientIdentifierTypeDto(any(PatientIdentifierType.class)))
                .thenReturn(testIdentifierTypeDto);

        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test reason")
                .build();

        // Act
        patientIdentifierTypeService.deletePatientIdentifierType(1, voidRequest);

        // Assert
        verify(patientIdentifierTypeRepository).save(identifierTypeCaptor.capture());
        PatientIdentifierType capturedType = identifierTypeCaptor.getValue();
        assertTrue(capturedType.isVoided());
        assertEquals("Test reason", capturedType.getVoidReason());
        assertNotNull(capturedType.getVoidedAt());
        assertEquals(1L, capturedType.getVoidedBy());
    }

    @Test
    @DisplayName("Test Delete Patient Identifier Type - Not Found")
    void testDeletePatientIdentifierTypeNotFound() {
        // Arrange
        when(patientIdentifierTypeRepository.findById(anyInt())).thenReturn(Optional.empty());
        RecordVoidRequest voidRequest = RecordVoidRequest.builder()
                .voidReason("Test reason")
                .build();

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierTypeService.deletePatientIdentifierType(1, voidRequest));
        verify(patientIdentifierTypeRepository, never()).save(any(PatientIdentifierType.class));
    }

    @Test
    @DisplayName("Test Get Patient Identifier Type - Success")
    void testGetPatientIdentifierType() {
        // Arrange
        when(patientIdentifierTypeRepository.findById(anyInt())).thenReturn(Optional.of(testPatientIdentifierType));
        when(patientIdentifierTypeMapper.toPatientIdentifierTypeDto(any(PatientIdentifierType.class)))
                .thenReturn(testIdentifierTypeDto);

        // Act
        PatientIdentifierTypeDto result = patientIdentifierTypeService.getPatientIdentifierType(1);

        // Assert
        assertNotNull(result);
        assertEquals(testIdentifierTypeDto.getPatientIdentifierTypeId(), result.getPatientIdentifierTypeId());
        assertEquals(testIdentifierTypeDto.getName(), result.getName());
        assertEquals(testIdentifierTypeDto.getDescription(), result.getDescription());
    }

    @Test
    @DisplayName("Test Get Patient Identifier Type - Not Found")
    void testGetPatientIdentifierTypeNotFound() {
        // Arrange
        when(patientIdentifierTypeRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientIdentifierTypeService.getPatientIdentifierType(1));
    }

    @Test
    @DisplayName("Test Get All Patient Identifier Types")
    void testGetAllPatientIdentifierTypes() {
        // Arrange
        List<PatientIdentifierType> types = Arrays.asList(
                testPatientIdentifierType,
                PatientIdentifierType.builder()
                        .patientIdentifierTypeId(2)
                        .name("Passport")
                        .description("Passport Number")
                        .build()
        );

        List<PatientIdentifierTypeDto> typeDtos = Arrays.asList(
                testIdentifierTypeDto,
                PatientIdentifierTypeDto.builder()
                        .patientIdentifierTypeId(2)
                        .name("Passport")
                        .description("Passport Number")
                        .build()
        );

        when(patientIdentifierTypeRepository.findAllByVoided(false)).thenReturn(types);
        when(patientIdentifierTypeMapper.toPatientIdentifierTypeDto(any(PatientIdentifierType.class)))
                .thenReturn(typeDtos.get(0), typeDtos.get(1));

        // Act
        List<PatientIdentifierTypeDto> results = patientIdentifierTypeService.getAllPatientIdentifierTypes();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(patientIdentifierTypeRepository).findAllByVoided(false);
        verify(patientIdentifierTypeMapper, times(2)).toPatientIdentifierTypeDto(any(PatientIdentifierType.class));
    }

    @Test
    @DisplayName("Test Get All Patient Identifier Types Both Voided")
    void testGetAllPatientIdentifierTypesBothVoided() {
        // Arrange
        PatientIdentifierType voidedType = PatientIdentifierType.builder()
                .patientIdentifierTypeId(2)
                .name("Passport")
                .description("Passport Number")
                .build();
        voidedType.setVoided(true);

        List<PatientIdentifierType> types = Arrays.asList(testPatientIdentifierType, voidedType);

        PatientIdentifierTypeDto voidedTypeDto = PatientIdentifierTypeDto.builder()
                .patientIdentifierTypeId(2)
                .name("Passport")
                .description("Passport Number")
                .build();
        voidedTypeDto.setVoided(true);

        List<PatientIdentifierTypeDto> typeDtos = Arrays.asList(testIdentifierTypeDto, voidedTypeDto);

        when(patientIdentifierTypeRepository.findAll()).thenReturn(types);
        when(patientIdentifierTypeMapper.toPatientIdentifierTypeDto(any(PatientIdentifierType.class)))
                .thenReturn(typeDtos.get(0), typeDtos.get(1));

        // Act
        List<PatientIdentifierTypeDto> results = patientIdentifierTypeService.getAllPatientIdentifierTypesBothVoided();

        // Assert
        assertNotNull(results);
        assertEquals(2, results.size());
        verify(patientIdentifierTypeRepository).findAll();
        verify(patientIdentifierTypeMapper, times(2)).toPatientIdentifierTypeDto(any(PatientIdentifierType.class));
    }
} 