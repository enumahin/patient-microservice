package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientProgramDto;
import com.alienworkspace.cdr.patient.exception.AlreadyExistException;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientProgram;
import com.alienworkspace.cdr.patient.model.Program;
import com.alienworkspace.cdr.patient.repository.PatientProgramRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import com.alienworkspace.cdr.patient.repository.ProgramRepository;
import com.alienworkspace.cdr.patient.service.impl.PatientProgramServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientProgramServiceTest {

    @Mock
    private PatientProgramRepository patientProgramRepository;

    @Mock
    private PatientRepository patientRepository;

    @Mock
    private ProgramRepository programRepository;

    @InjectMocks
    private PatientProgramServiceImpl patientProgramService;

    @Captor
    private ArgumentCaptor<PatientProgram> patientProgramCaptor;

    private Patient testPatient;
    private Program testProgram;
    private PatientProgram testPatientProgram;
    private PatientProgramDto testPatientProgramDto;

    @BeforeEach
    void setUp() {
        testPatient = Patient.builder()
                .patientId(1L)
                .allergies("None")
                .build();

        testProgram = Program.builder()
                .programId(1)
                .name("Test Program")
                .description("Test Description")
                .active(true)
                .build();

        testPatientProgram = PatientProgram.builder()
                .patient(testPatient)
                .program(testProgram)
                .dateEnrolled(LocalDate.now())
                .locationId(1)
                .build();

        testPatientProgramDto = PatientProgramDto.builder()
                .dateEnrolled(LocalDate.now())
                .locationId(1)
                .build();
    }

    @Test
    @DisplayName("Test Enroll Patient in Program - Success")
    void testEnrollPatientInProgram() {
        // Arrange
        when(patientProgramRepository.findByPatientIdAndProgramId(anyLong(), anyInt()))
                .thenReturn(Optional.empty());
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(programRepository.findById(anyInt())).thenReturn(Optional.of(testProgram));
        when(patientProgramRepository.save(any(PatientProgram.class))).thenReturn(testPatientProgram);

        // Act
        patientProgramService.enrollPatientInProgram(1L, 1, testPatientProgramDto);

        // Assert
        verify(patientProgramRepository).save(patientProgramCaptor.capture());
        PatientProgram capturedProgram = patientProgramCaptor.getValue();
        assertEquals(testPatient, capturedProgram.getPatient());
        assertEquals(testProgram, capturedProgram.getProgram());
        assertEquals(testPatientProgramDto.getDateEnrolled(), capturedProgram.getDateEnrolled());
        assertEquals(testPatientProgramDto.getLocationId(), capturedProgram.getLocationId());
    }

    @Test
    @DisplayName("Test Enroll Patient in Program - Already Enrolled")
    void testEnrollPatientInProgramAlreadyEnrolled() {
        // Arrange
        when(patientProgramRepository.findByPatientIdAndProgramId(anyLong(), anyInt()))
                .thenReturn(Optional.of(testPatientProgram));

        // Act & Assert
        assertThrows(AlreadyExistException.class,
                () -> patientProgramService.enrollPatientInProgram(1L, 1, testPatientProgramDto));
        verify(patientProgramRepository, never()).save(any(PatientProgram.class));
    }

    @Test
    @DisplayName("Test Enroll Patient in Program - Patient Not Found")
    void testEnrollPatientInProgramPatientNotFound() {
        // Arrange
        when(patientProgramRepository.findByPatientIdAndProgramId(anyLong(), anyInt()))
                .thenReturn(Optional.empty());
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientProgramService.enrollPatientInProgram(1L, 1, testPatientProgramDto));
        verify(patientProgramRepository, never()).save(any(PatientProgram.class));
    }

    @Test
    @DisplayName("Test Enroll Patient in Program - Program Not Found")
    void testEnrollPatientInProgramProgramNotFound() {
        // Arrange
        when(patientProgramRepository.findByPatientIdAndProgramId(anyLong(), anyInt()))
                .thenReturn(Optional.empty());
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(programRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientProgramService.enrollPatientInProgram(1L, 1, testPatientProgramDto));
        verify(patientProgramRepository, never()).save(any(PatientProgram.class));
    }

    @Test
    @DisplayName("Test Update Program Enrollment - Success")
    void testUpdateProgramEnrollment() {
        // Arrange
        LocalDate completionDate = LocalDate.now();
        PatientProgramDto updateDto = PatientProgramDto.builder()
                .dateCompleted(completionDate)
                .outcomeConceptId(1)
                .outcomeComment("Successfully completed")
                .build();

        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(patientProgramRepository.findByPatientIdAndProgramId(anyLong(), anyInt()))
                .thenReturn(Optional.of(testPatientProgram));
        when(patientProgramRepository.save(any(PatientProgram.class))).thenReturn(testPatientProgram);

        // Act
        patientProgramService.updateProgramEnrollment(1L, 1, updateDto);

        // Assert
        verify(patientProgramRepository).save(patientProgramCaptor.capture());
        PatientProgram capturedProgram = patientProgramCaptor.getValue();
        assertEquals(completionDate, capturedProgram.getDateCompleted());
        assertEquals(1, capturedProgram.getOutcomeConceptId());
        assertEquals("Successfully completed", capturedProgram.getOutcomeComment());
    }

    @Test
    @DisplayName("Test Update Program Enrollment - Patient Not Found")
    void testUpdateProgramEnrollmentPatientNotFound() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientProgramService.updateProgramEnrollment(1L, 1, testPatientProgramDto));
        verify(patientProgramRepository, never()).save(any(PatientProgram.class));
    }

    @Test
    @DisplayName("Test Update Program Enrollment - Enrollment Not Found")
    void testUpdateProgramEnrollmentNotFound() {
        // Arrange
        when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
        when(patientProgramRepository.findByPatientIdAndProgramId(anyLong(), anyInt()))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> patientProgramService.updateProgramEnrollment(1L, 1, testPatientProgramDto));
        verify(patientProgramRepository, never()).save(any(PatientProgram.class));
    }
} 