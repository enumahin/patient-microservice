package com.alienworkspace.cdr.patient.model;

import com.alienworkspace.cdr.patient.model.audit.AuditTrail;
import com.google.common.base.MoreObjects;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Program entity class.
 *
 * @author Ikenumah (enumahinm@gmail.com)
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@SuppressFBWarnings(value = {"EI_EXPOSE_REP", "EI_EXPOSE_REP2"}, justification = "Composition; safe to store")
public class Program extends AuditTrail {

    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "program_id")
    private Integer programId;

    @Getter
    @Setter
    @Column(unique = true, nullable = false)
    private String name;

    @Getter
    @Setter
    @Column(name = "program_code", unique = true, nullable = false)
    private String programCode;

    @Getter
    @Setter
    private String description;

    @Getter
    @Setter
    private boolean active;

    @Builder.Default
    @OneToMany(mappedBy = "program", fetch = FetchType.LAZY)
    private Set<PatientProgram> programPatients = new HashSet<>();

    /**
     * Get program patients.
     *
     * @return Set of PatientProgram
     */
    public Set<PatientProgram> getProgramPatients() {
        return Collections.unmodifiableSet(programPatients);
    }

    /**
     * Equals.
     *
     * @param o Object
     */
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Program program)) {
            return false;
        }
        if (this == o) {
            return true;
        }
        if (getClass() != o.getClass()) {
            return false;
        }
        return active == program.active && Objects.equals(programId, program.programId)
                && Objects.equals(name, program.name)
                && Objects.equals(description, program.description)
                && Objects.equals(getLastModifiedBy(), program.getLastModifiedBy())
                && Objects.equals(getLastModifiedAt(), program.getLastModifiedAt())
                && Objects.equals(isVoided(), program.isVoided())
                && Objects.equals(getVoidedBy(), program.getVoidedBy())
                && Objects.equals(getVoidedAt(), program.getVoidedAt())
                && Objects.equals(getVoidReason(), program.getVoidReason())
                && Objects.equals(getUuid(), program.getUuid())
                && Objects.equals(getCreatedBy(), program.getCreatedBy())
                && Objects.equals(getCreatedAt(), program.getCreatedAt());
    }

    /**
     * Hash code.
     */
    @Override
    public int hashCode() {
        return Objects.hash(getProgramId(), getName(), getDescription(), isActive(),
                getLastModifiedBy(), getLastModifiedAt(), isVoided(), getVoidedBy(), getVoidedAt(),
                getVoidReason(), getUuid(), getCreatedBy(), getCreatedAt());
    }

    /**
     * To string.
     */
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("programId", programId)
                .add("name", name)
                .add("description", description)
                .add("active", active)
                .add("createdBy", getCreatedBy())
                .add("createdAt", getCreatedAt())
                .add("lastModifiedBy", getLastModifiedBy())
                .add("lastModifiedAt", getLastModifiedAt())
                .add("voided", isVoided())
                .add("voidedBy", getVoidedBy())
                .add("voidedAt", getVoidedAt())
                .add("voidReason", getVoidReason())
                .add("uuid", getUuid())
                .toString();
    }
}
