package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.model.dto.patient.ProgramDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import java.util.List;

/**
 * Service interface for managing healthcare programs.
 * This service provides business logic for managing the various programs
 * that patients can be enrolled in.
 *
 * <p>
 * Key responsibilities:
 * - Program registration and updates
 * - Program status management
 * - Program metadata handling
 * - Enrollment rule management
 *
 * <p>
 * The service ensures:
 * - Proper program configuration
 * - Business rule enforcement
 * - Audit trail maintenance
 * - Data consistency
 *
 * @author Firstname Lastname
 * @version 1.0
 * @since 1.0
 */
public interface ProgramService {

    /**
     * Creates a new program.
     *
     * @param programDto the program to create
     * @return the created program
     */
    ProgramDto createProgram(ProgramDto programDto);

    /**
     * Updates an existing program.
     *
     * @param programId the ID of the program to update
     * @param programDto the program to update
     * @return the updated program
     */
    ProgramDto updateProgram(int programId, ProgramDto programDto);

    /**
     * Deletes a program.
     *
     * @param id the ID of the program to delete
     * @param request the request object containing the voided by user
     */
    void deleteProgram(int id, RecordVoidRequest request);

    /**
     * Retrieves a program by its ID.
     *
     * @param id the ID of the program to retrieve
     * @return the program
     */
    ProgramDto getProgram(int id);

    /**
     * Retrieves all programs.
     *
     * @return a list of programs
     */
    List<ProgramDto> getAllPrograms();
}
