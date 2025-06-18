package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.model.PatientIdentifier;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Repository interface for managing {@link PatientIdentifier} entities.
 * This repository provides data access operations for patient identifiers,
 * extending JPA functionality with custom queries for specific business needs.
 *
 * <p>
 * Key features:
 * - Standard CRUD operations inherited from JpaRepository
 * - Custom queries for identifier lookups
 * - Support for preferred identifier management
 * - Composite key handling for patient-identifier type combinations
 *
 * <p>
 * The repository uses JPA's query methods and custom JPQL queries to
 * efficiently retrieve and manage patient identifier data.
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientIdentifier
 * @see JpaRepository
 */
@Repository
public interface PatientIdentifierRepository extends JpaRepository<PatientIdentifier, Long> {

    /**
     * Finds a patient identifier by its string value.
     * This method is used to look up patient identifiers based on their actual
     * identifier string (e.g., hospital number, national ID number).
     *
     * @param value The string value of the identifier to search for
     * @return {@link Optional}&lt;{@link PatientIdentifier}&gt; The found patient identifier, or empty if not found
     */
    Optional<PatientIdentifier> findPatientIdentifierByIdentifier(String value);

    /**
     * Finds a preferred patient identifier for a specific patient and identifier type.
     * This method is used to check for existing preferred identifiers when managing
     * patient identifier preferences. It ensures that only one identifier of a given
     * type can be marked as preferred for a patient.
     *
     * <p>
     * The query joins the necessary tables to retrieve the complete identifier information
     * including patient and identifier type details.
     *
     * @param patientId The ID of the patient
     * @param identifierTypeId The ID of the identifier type
     * @return {@link Optional}&lt;{@link PatientIdentifier}&gt; The found preferred identifier, or empty if none exists
     */
    @Query("SELECT p FROM PatientIdentifier p WHERE p.patient.patientId = ?1 AND "
            + "p.patientIdentifierType.patientIdentifierTypeId = ?2 AND p.preferred = true")
    Optional<PatientIdentifier> findPatientIdentifierByIdentifierTypeAndPreferred(long patientId,
                                                                                  long identifierTypeId);

    /**
     * Finds a patient identifier by patient ID and identifier type ID.
     * This method retrieves a specific identifier based on the composite key
     * of patient and identifier type. It's used for operations that need to
     * locate a specific identifier combination.
     *
     * <p>
     * The query uses JPQL to join the necessary tables and filter by both
     * patient ID and identifier type ID.
     *
     * @param id The ID of the patient
     * @param identifierTypeId The ID of the identifier type
     * @return {@link Optional}&lt;{@link PatientIdentifier}&gt; The found identifier, or empty if not found
     */
    @Query("SELECT p FROM PatientIdentifier p WHERE p.patient.patientId = ?1 AND "
            + "p.patientIdentifierType.patientIdentifierTypeId = ?2")
    Optional<PatientIdentifier> finedByPatientIdAndIdentifierTypeId(long id, long identifierTypeId);

    /**
     * Resets the preferred status of all identifiers for a specific patient and identifier type.
     * This method is used to ensure that only one identifier of a given type is marked as preferred
     * for a patient. It sets the preferred status of all other identifiers to false.
     *
     * @param patientId The ID of the patient
     * @param identifierTypeId The ID of the identifier type
     */
    @Modifying
    @Transactional
    @Query("UPDATE PatientIdentifier p SET p.preferred = false WHERE p.patient.patientId = ?1 AND "
            + "p.patientIdentifierType.patientIdentifierTypeId = ?2")
    void resetPreferredByPatientIdAndIdentifierType(long patientId, long identifierTypeId);
}
