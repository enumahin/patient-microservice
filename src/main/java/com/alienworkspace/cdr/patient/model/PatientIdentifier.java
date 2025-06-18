package com.alienworkspace.cdr.patient.model;

import com.alienworkspace.cdr.patient.model.audit.AuditTrail;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * PatientIdentifier entity class.
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
@Table(name = "patient_identifier", indexes = {
    @Index(name = "patient_identifier_idx", columnList = "identifier_type_id, patient_id, preferred"),
    @Index(name = "patient_identifier_type_idx", columnList = "identifier_type_id, preferred"),
}, uniqueConstraints = @UniqueConstraint(columnNames = {"identifier_type_id", "patient_id", "preferred"}))
public class PatientIdentifier extends AuditTrail {

    @Id
    @Column(name = "patient_identifier_id", updatable = false, nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long patientIdentifierId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "identifier_type_id")
    private PatientIdentifierType patientIdentifierType;

    @Column(unique = true, nullable = false)
    private String identifier;

    @Builder.Default
    private boolean preferred = true;

    @Column(name = "location_id")
    private int locationId;

}
