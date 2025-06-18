package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientProgramDto;
import com.alienworkspace.cdr.patient.exception.AlreadyExistException;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;

/**
 * Service interface for managing patient program enrollments.
 * This service provides business logic for managing patient enrollments in
 * various healthcare programs.
 *
 * <p>
 * Key responsibilities:
 * - Program enrollment management
 * - Enrollment status tracking
 * - Program completion handling
 * - Enrollment history maintenance
 *
 * <p>
 * The service ensures:
 * - Proper enrollment validation
 * - Business rule enforcement
 * - Audit trail maintenance
 * - Data consistency
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 */
public interface PatientProgramService {

    /**
     * Enrolls a patient in a specific program.
     * This method creates a new program enrollment record for a patient, associating them
     * with a specific program and location. It prevents duplicate enrollments by checking
     * if the patient is already enrolled in the specified program.
     *
     * @param patientId The unique identifier of the patient to be enrolled
     * @param programId The unique identifier of the program to enroll the patient in
     * @param patientProgramDto The enrollment details including enrollment date and location
     * @throws ResourceNotFoundException if either patient or program is not found
     * @throws AlreadyExistException if patient is already enrolled in the program
     */
    void enrollPatientInProgram(long patientId, int programId, PatientProgramDto patientProgramDto);

    /**
     * Updates an existing program enrollment for a patient.
     * This method allows updating the completion details of a patient's program enrollment,
     * including completion date, outcome, and any relevant comments. It's typically used
     * when a patient completes or discontinues a program.
     *
     * @param patientId The unique identifier of the enrolled patient
     * @param programId The unique identifier of the program the patient is enrolled in
     * @param patientProgramDto The updated enrollment details including completion date and outcome
     * @throws ResourceNotFoundException if either patient or enrollment is not found
     */
    void updateProgramEnrollment(long patientId, int programId, PatientProgramDto patientProgramDto);
}
