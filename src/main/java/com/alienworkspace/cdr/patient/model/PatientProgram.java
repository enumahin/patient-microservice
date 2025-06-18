package com.alienworkspace.cdr.patient.model;

import com.alienworkspace.cdr.model.helper.AuditTrail;
import com.google.common.base.MoreObjects;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PatientProgram entity class.
 *
 * @author Ikenumah (enumahinm@gmail.com)
 */
@AllArgsConstructor
@Builder
@Entity
@Getter
@NoArgsConstructor
@Setter
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Composition; safe to store")
@Table(name = "patient_program", indexes = {
    @Index(name = "idx_program", columnList = "program_id, patient_id, date_enrolled DESC"),
    @Index(name = "idx_program_date", columnList = "program_id, date_enrolled")},
    uniqueConstraints = @UniqueConstraint(columnNames = {"program_id", "patient_id", "date_enrolled"}))
public class PatientProgram extends AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "patient_program_id")
    private Long patientProgramId;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;

    @ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @Column(name = "location_id")
    private int locationId;

    @Column(name = "date_enrolled")
    private LocalDate dateEnrolled;

    @Column(name = "date_completed")
    private LocalDate dateCompleted;

    @Column(name = "outcome_concept_id")
    private Integer outcomeConceptId;

    @Column(name = "outcome_date")
    private String outcomeComment;

    /**
     * Equals.
     *
     * @param o Object
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof PatientProgram that)) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        return getLocationId() == that.getLocationId() && Objects.equals(getPatientProgramId(),
                that.getPatientProgramId()) && Objects.equals(getProgram(), that.getProgram())
                && Objects.equals(getPatient(), that.getPatient())
                && Objects.equals(getDateEnrolled(), that.getDateEnrolled())
                && Objects.equals(getDateCompleted(), that.getDateCompleted())
                && Objects.equals(getOutcomeConceptId(), that.getOutcomeConceptId())
                && Objects.equals(getOutcomeComment(), that.getOutcomeComment())
                && Objects.equals(getUuid(), that.getUuid())
                && Objects.equals(getCreatedBy(), that.getCreatedBy())
                && Objects.equals(getCreatedAt(), that.getCreatedAt())
                && Objects.equals(getLastModifiedBy(), that.getLastModifiedBy())
                && Objects.equals(getLastModifiedAt(), that.getLastModifiedAt())
                && Objects.equals(getVoided(), that.getVoided())
                && Objects.equals(getVoidedBy(), that.getVoidedBy())
                && Objects.equals(getVoidedAt(), that.getVoidedAt())
                && Objects.equals(getVoidReason(), that.getVoidReason());
    }

    /**
     * Hash code.
     *
     * @return int
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), getPatientProgramId(), getProgram(), getPatient(), getLocationId(),
                getDateEnrolled(), getDateCompleted(), getOutcomeConceptId(), getOutcomeComment(), getUuid(),
                getCreatedBy(), getCreatedAt(), getLastModifiedBy(), getLastModifiedAt(), getVoided(),
                getVoidedBy(), getVoidedAt(), getVoidReason());
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("patientProgramId", patientProgramId)
                .add("program", program)
                .add("patient", patient)
                .add("locationId", locationId)
                .add("dateEnrolled", dateEnrolled)
                .add("dateCompleted", dateCompleted)
                .add("outcomeConceptId", outcomeConceptId)
                .add("outcomeComment", outcomeComment)
                .add("createdBy", getCreatedBy())
                .add("createdAt", getCreatedAt())
                .add("lastModifiedBy", getLastModifiedBy())
                .add("lastModifiedAt", getLastModifiedAt())
                .add("voided", getVoided())
                .add("voidedBy", getVoidedBy())
                .add("voidedAt", getVoidedAt())
                .add("voidReason", getVoidReason())
                .add("uuid", getUuid())
                .toString();
    }
}
