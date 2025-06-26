package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.PatientDto;
import com.alienworkspace.cdr.model.dto.person.PersonDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import java.util.List;

/**
 * Service interface for managing patient records.
 * This service provides business logic for patient management operations,
 * including CRUD operations and specialized patient queries.
 *
 * <p>
 * Key responsibilities:
 * - Patient registration and updates
 * - Patient search operations
 * - Patient status management
 * - Program enrollment coordination
 * - Identifier management
 *
 * <p>
 * The service ensures:
 * - Data validation and consistency
 * - Business rule enforcement
 * - Audit trail maintenance
 * - Proper transaction handling
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 */
public interface PatientService {

    /**
     * Creates a new Patient.
     *
     * @param patientDto Patient DTO
     * @return PatientDto PatientDto
     */
    PatientDto createPatient(PatientDto patientDto);

    /**
     * Updates a Patient.
     *
     * @param id The id of the Patient
     * @param patientDto Patient DTO
     * @return PatientDto
     */
    PatientDto updatePatient(long id, PatientDto patientDto);

    /**
     * Deletes a Patient.
     *
     * @param id The id of the Patient
     * @param recordVoidRequest RecordVoidRequest
     */
    void deletePatient(long id, RecordVoidRequest recordVoidRequest);

    /**
     * Gets a Patient.
     *
     * @param id The id of the Patient
     * @return PatientDto
     */
    PatientDto getPatient(long id);

    /**
     * Gets a Patient.
     *
     * @param value The value of the Patient
     * @return PatientDto
     */
    PatientDto getPatientByIdentifier(String value);

    /**
     * Gets a Person.
     *
     * @param id The id of the Person
     * @return PersonDto
     */
    PersonDto getPerson(long id, boolean includeVoided);

    /**
     * Gets all Patients.
     *
     * @return List of PatientDto
     */
    List<PatientDto> getAllPatients();

    /**
     * Gets all Patients both voided.
     *
     * @return List of PatientDto
     */
    List<PatientDto> getAllPatientsBothVoided();

    /**
     * Gets Patients by Program.
     *
     * @param programId The id of the Program
     * @return List of PatientDto
     */
    List<PatientDto> getPatientsByProgram(int programId);

    /**
     * Gets Patients by Program.
     *
     * @param identifierTypeId The id of the Program
     * @return List of PatientDto
     */
    List<PatientDto> getPatientsByIdentifierType(int identifierTypeId);

    /**
     * Gets Patients by Program.
     *
     * @param programId The id of the Program
     * @param active The status of the Program
     * @return List of PatientDto
     */
    List<PatientDto> getPatientsByProgramAndStatus(int programId, boolean active);

}
