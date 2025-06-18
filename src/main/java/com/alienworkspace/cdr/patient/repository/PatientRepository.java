package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.model.Patient;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Patient} entities.
 * This repository provides data access operations for patient records,
 * extending JPA functionality with custom queries for specific business needs.
 *
 * <p>
 * Key features:
 * - Standard CRUD operations inherited from JpaRepository
 * - Custom queries for patient lookups
 * - Support for soft deletion through void status
 * - Complex query operations for patient search
 *
 * <p>
 * The repository manages:
 * - Basic patient demographics
 * - Patient status information
 * - Relationships with other entities
 * - Audit trail data
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see Patient
 * @see JpaRepository
 */
@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    /**
     * Finds a patient by their unique identifier value.
     * This method is used to look up patients using any type of identifier
     * (e.g., hospital number, national ID, etc.).
     *
     * <p>
     * The query joins the patient and identifier tables to retrieve
     * the complete patient information based on the identifier value.
     *
     * @param identifier The identifier value to search for
     * @return {@link Optional}&lt;{@link Patient}&gt; The found patient, or empty if not found
     */
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.patientIdentifiers pi WHERE pi.identifier = ?1")
    Optional<Patient> findByIdentifier(String identifier);

    /**
     * Finds patients by program enrollment and active status.
     * This method retrieves patients based on their enrollment in a specific program
     * and whether that enrollment is currently active.
     *
     * <p>
     * The query joins the necessary tables to filter patients based on:
     * - Program enrollment
     * - Program active status
     * - Patient void status
     *
     * @param programId The ID of the program to search for
     * @param status The active status of the program enrollment
     * @return {@link List}&lt;{@link Patient}&gt; List of patients matching the criteria
     */
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.patientPrograms pp "
            + "WHERE pp.program.programId = ?1 AND pp.program.active = ?2")
    List<Patient> findByProgramAndStatus(int programId, boolean status);

    /**
     * Finds patients by program enrollment.
     * This method retrieves all patients enrolled in a specific program,
     * regardless of the enrollment status.
     *
     * <p>
     * The query joins the patient and program enrollment tables to retrieve
     * patients based on their program enrollment.
     *
     * @param programId The ID of the program to search for
     * @return {@link List}&lt;{@link Patient}&gt; List of patients enrolled in the program
     */
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.patientPrograms pp WHERE pp.program.programId = ?1")
    List<Patient> findByProgram(int programId);

    /**
     * Finds patients by identifier type.
     * This method retrieves all patients who have a specific type of identifier
     * (e.g., all patients with a national ID).
     *
     * <p>
     * The query joins the necessary tables to filter patients based on:
     * - Identifier type
     * - Patient void status
     * - Identifier void status
     *
     * @param identifierTypeId The ID of the identifier type to search for
     * @return {@link List}&lt;{@link Patient}&gt; List of patients with the specified identifier type
     */
    @Query("SELECT DISTINCT p FROM Patient p JOIN p.patientIdentifiers pi "
            + "WHERE pi.patientIdentifierType.patientIdentifierTypeId = ?1")
    List<Patient> findByIdentifierType(int identifierTypeId);

    /**
     * Retrieves all patients based on their void status.
     * This method is used to filter patients based on whether they have been
     * soft-deleted (voided) or are still active.
     *
     * @param voided The void status to filter by (true for voided patients, false for active patients)
     * @return {@link List}&lt;{@link Patient}&gt; List of patients matching the void status
     */
    List<Patient> findAllByVoided(boolean voided);
}
