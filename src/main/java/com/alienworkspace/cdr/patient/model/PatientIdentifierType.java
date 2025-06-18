package com.alienworkspace.cdr.patient.model;

import com.alienworkspace.cdr.patient.model.audit.AuditTrail;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity class representing a patient identifier type.
 *
 * <p>
 * This class contains the metadata for different types of identifiers
 * that can be assigned to patients in the healthcare system, such as
 * National ID, Hospital Number, Insurance ID, etc. The class provides
 * the following information about each identifier type:
 * <ul>
 *     <li>Unique identifier</li>
 *     <li>Name and description</li>
 *     <li>Format specifications and validation rules</li>
 *     <li>Required/Optional status</li>
 *     <li>Uniqueness constraints</li>
 *     <li>Format hints for users</li>
 *     <li>Audit trail information</li>
 * </ul>
 *
 * <p>
 * The class also supports the following operations:
 * - Identifier type registration
 * - Format validation rules
 * - Type metadata management
 * - Status tracking
 *
 * <p>
 * This class is used in the {@link PatientIdentifier} entity to associate
 * each patient identifier with a specific type.
 */
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Composition; safe to store")
public class PatientIdentifierType extends AuditTrail {

    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "patient_identifier_type_id")
    private Integer patientIdentifierTypeId;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String name;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private String format;

    @Getter
    @Setter
    private boolean required;

    @Getter
    @Setter
    @Column(name = "is_unique")
    private boolean isUnique;

    @Getter
    @Setter
    @Column(name = "format_hint")
    private String formatHint;

    @Getter
    @Setter
    private String validator;

    @Builder.Default
    @OneToMany(mappedBy = "patientIdentifierType", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<PatientIdentifier> patientIdentifiers = new HashSet<>();

    /**
     * Get patient identifiers.
     *
     * @return Set of PatientIdentifier
     */
    public Set<PatientIdentifier> getPatientIdentifiers() {
        return Collections.unmodifiableSet(patientIdentifiers);
    }
}
