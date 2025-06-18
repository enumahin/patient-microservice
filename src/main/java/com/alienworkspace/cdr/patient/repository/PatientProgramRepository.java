package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.model.PatientProgram;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link PatientProgram} entities.
 * This repository handles the persistence and retrieval of patient program enrollments,
 * managing the relationship between patients and healthcare programs they are enrolled in.
 *
 * <p>
 * Key features:
 * - Standard CRUD operations inherited from JpaRepository
 * - Custom queries for enrollment lookups
 * - Support for program enrollment management
 * - Tracking of enrollment dates and outcomes
 *
 * <p>
 * The repository manages:
 * - Program enrollment records
 * - Enrollment dates and locations
 * - Program completion information
 * - Outcome tracking
 * - Patient-program relationships
 *
 * <p>
 * Each enrollment record contains:
 * - Patient reference
 * - Program reference
 * - Enrollment date
 * - Location information
 * - Completion details (when applicable)
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientProgram
 * @see JpaRepository
 */
@Repository
public interface PatientProgramRepository extends JpaRepository<PatientProgram, Long> {

    /**
     * Finds a program enrollment by patient ID and program ID.
     * This method is used to retrieve specific program enrollments for a patient,
     * typically used for:
     * - Checking existing enrollments
     * - Retrieving enrollment details
     * - Managing program transitions
     * - Updating enrollment status
     *
     * <p>
     * The query uses JPQL to join the patient and program tables, retrieving
     * the complete enrollment information including related entities.
     *
     * @param patientId The ID of the patient
     * @param programId The ID of the program
     * @return {@link Optional}&lt;{@link PatientProgram}&gt; The found program enrollment, or empty if not found
     */
    @Query("select p from PatientProgram p where p.patient.patientId =:patientId and p.program.programId =:programId")
    Optional<PatientProgram> findByPatientIdAndProgramId(long patientId, int programId);
}
