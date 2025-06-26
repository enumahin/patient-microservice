package com.alienworkspace.cdr.patient.model;

import com.alienworkspace.cdr.model.dto.person.PersonDto;
import com.alienworkspace.cdr.patient.model.audit.AuditTrail;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Patient entity class.
 *
 * @author Ikenumah (enumahinm@gmail.com)
 */
@AllArgsConstructor
@Builder
@Entity
@NoArgsConstructor
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"},
        justification = "Dependency injection by Spring; safe to store")
public class Patient extends AuditTrail {

    @Getter
    @Setter
    @Id
    @NotNull
    @Column(name = "patient_id", nullable = false)
    private long patientId;

    @Setter
    @Getter
    private String allergies;

    @Builder.Default
    @OneToMany(mappedBy = "patient", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PatientIdentifier> patientIdentifiers = new HashSet<>();

    @Builder.Default
    @OneToMany(mappedBy = "patient", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<PatientProgram> patientPrograms = new HashSet<>();

    @Setter
    @Getter
    @Transient
    private PersonDto person;

    /**
     * Get patient identifiers.
     *
     * @return Set of PatientIdentifier
     */
    public Set<PatientIdentifier> getPatientIdentifiers() {
        return Collections.unmodifiableSet(patientIdentifiers);
    }

    /**
     * Get patient programs.
     *
     * @return Set of PatientProgram
     */
    public Set<PatientProgram> getPatientPrograms() {
        return Collections.unmodifiableSet(patientPrograms);
    }
}
