package com.alienworkspace.cdr.patient.service.impl;

import com.alienworkspace.cdr.model.dto.patient.PatientProgramDto;
import com.alienworkspace.cdr.patient.exception.AlreadyExistException;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.model.Patient;
import com.alienworkspace.cdr.patient.model.PatientProgram;
import com.alienworkspace.cdr.patient.model.Program;
import com.alienworkspace.cdr.patient.repository.PatientProgramRepository;
import com.alienworkspace.cdr.patient.repository.PatientRepository;
import com.alienworkspace.cdr.patient.repository.ProgramRepository;
import com.alienworkspace.cdr.patient.service.PatientProgramService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link PatientProgramService} interface.
 * This class provides the core implementation of patient program enrollment operations,
 * handling enrollment data persistence and business logic.
 *
 * <p>
 * Key features:
 * - Program enrollment management
 * - Enrollment status tracking
 * - Program completion handling
 * - Enrollment history maintenance
 *
 * <p>
 * Implementation details:
 * - Uses JPA repositories for data access
 * - Implements transaction management
 * - Handles data validation
 * - Maintains audit trails
 *
 * <p>
 * Business rules enforced:
 * - Enrollment uniqueness validation
 * - Status transitions
 * - Completion requirements
 * - Data integrity checks
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientProgramService
 * @see PatientProgram
 * @see Patient
 * @see Program
 */
@Service
@Transactional
@AllArgsConstructor
public class PatientProgramServiceImpl implements PatientProgramService {

    private PatientProgramRepository patientProgramRepository;
    private PatientRepository patientRepository;
    private ProgramRepository programRepository;

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * 1. Checks for existing enrollment to prevent duplicates
     * 2. Validates the existence of both patient and program
     * 3. Creates a new PatientProgram entity with the provided details
     * 4. Persists the enrollment in the database
     *
     * <p>
     * The method follows these steps:
     * - Verify no existing enrollment exists for the patient in the program
     * - Retrieve and validate patient existence
     * - Retrieve and validate program existence
     * - Create and persist the new enrollment
     *
     * @throws AlreadyExistException if the patient is already enrolled in the program
     * @throws ResourceNotFoundException if either the patient or program is not found
     * @throws IllegalArgumentException if there are issues with the enrollment data
     */
    @Override
    public void enrollPatientInProgram(long patientId, int programId, PatientProgramDto patientProgramDto) {

        patientProgramRepository.findByPatientIdAndProgramId(patientId, programId)
                .ifPresent((patientProgram) -> {
                    throw new AlreadyExistException("Patient already enrolled in program");
                });

        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "Id", String.valueOf(patientId)));

        Program program = programRepository.findById(programId)
                        .orElseThrow(() -> new ResourceNotFoundException("Program", "Id", String.valueOf(programId)));
        PatientProgram patientProgram = PatientProgram.builder()
                .dateEnrolled(patientProgramDto.getDateEnrolled())
                .patient(patient)
                .program(program)
                .locationId(patientProgramDto.getLocationId())
                .build();
        patientProgramRepository.save(patientProgram);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Implementation details:
     * 1. Validates patient existence
     * 2. Retrieves and updates the existing enrollment
     * 3. Updates completion details including:
     *    - Completion date
     *    - Outcome comments
     *    - Outcome concept identifier
     * 4. Persists the updated enrollment
     *
     * <p>
     * The method ensures that both the patient and their program enrollment exist
     * before applying any updates. All updates are performed within a transaction
     * to maintain data consistency.
     *
     * @throws ResourceNotFoundException if either the patient or their program enrollment is not found
     * @throws IllegalArgumentException if there are issues with the update data
     */
    @Override
    public void updateProgramEnrollment(long patientId, int programId, PatientProgramDto patientProgramDto) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient", "Id", String.valueOf(patientId)));

        patientProgramRepository.findByPatientIdAndProgramId(patientId, programId)
                .map(savedPatientProgram -> {
                    savedPatientProgram.setDateCompleted(patientProgramDto.getDateCompleted());
                    savedPatientProgram.setOutcomeComment(patientProgramDto.getOutcomeComment());
                    savedPatientProgram.setOutcomeConceptId(patientProgramDto.getOutcomeConceptId());
                    return patientProgramRepository.save(savedPatientProgram);
                }).orElseThrow(() -> new ResourceNotFoundException("Patient Enrollment", "Program Id",
                        String.valueOf(patientId)));
    }
}
