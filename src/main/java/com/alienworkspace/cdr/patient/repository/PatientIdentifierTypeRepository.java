package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.model.PatientIdentifierType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link PatientIdentifierType} entities.
 * This repository handles the persistence and retrieval of patient identifier types,
 * which define the various categories of identifiers that can be assigned to patients
 * in the healthcare system (e.g., National ID, Hospital Number, Insurance ID).
 *
 * <p>
 * Key features:
 * - Standard CRUD operations inherited from JpaRepository
 * - Support for soft deletion through void status
 * - Filtering capabilities for active/inactive types
 * - Management of identifier type metadata
 *
 * <p>
 * The repository supports the following identifier type properties:
 * - Name and description
 * - Format specifications and validation rules
 * - Required/Optional status
 * - Uniqueness constraints
 * - Format hints for users
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 * @see PatientIdentifierType
 * @see JpaRepository
 */
@Repository
public interface PatientIdentifierTypeRepository extends JpaRepository<PatientIdentifierType, Integer> {

    /**
     * Retrieves all identifier types based on their void status.
     * This method is used to filter identifier types based on whether they have been
     * soft-deleted (voided) or are still active. It's particularly useful for:
     * - Displaying only active identifier types in user interfaces
     * - Auditing voided identifier types
     * - Managing identifier type lifecycle
     *
     * @param voided The void status to filter by (true for voided types, false for active types)
     * @return {@link List}&lt;{@link PatientIdentifierType}&gt; A list of identifier types matching the void status
     */
    List<PatientIdentifierType> findAllByVoided(boolean voided);
}
