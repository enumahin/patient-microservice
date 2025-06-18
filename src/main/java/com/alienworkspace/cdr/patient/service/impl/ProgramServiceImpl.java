package com.alienworkspace.cdr.patient.service.impl;

import static java.lang.String.format;

import com.alienworkspace.cdr.model.dto.patient.ProgramDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.helpers.CurrentUser;
import com.alienworkspace.cdr.patient.model.mapper.ProgramMapper;
import com.alienworkspace.cdr.patient.repository.ProgramRepository;
import com.alienworkspace.cdr.patient.service.ProgramService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Implementation of the {@link ProgramService} interface.
 * This class provides the core implementation of healthcare program operations,
 * handling program data persistence and business logic.
 *
 * <p>
 * Key features:
 * - Program registration and updates
 * - Program status management
 * - Program metadata handling
 * - Enrollment rule management
 *
 * <p>
 * Implementation details:
 * - Uses JPA repositories for data access
 * - Implements transaction management
 * - Handles data validation
 * - Maintains audit trails
 *
 * <p>
 * Business rules enforced:
 * - Program uniqueness validation
 * - Status transitions
 * - Enrollment requirements
 * - Data integrity checks
 *
 * @author Ikenumah (enumahinm@gmail.com)
 * @version 1.0
 * @since 1.0
 */
@Service
@AllArgsConstructor
@Transactional
public class ProgramServiceImpl implements ProgramService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramServiceImpl.class);

    private final ProgramRepository programRepository;

    private final ProgramMapper programMapper;

    /**
     * Creates a new program.
     *
     * @param programDto The program details.
     * @return The created program.
     */
    @Override
    public ProgramDto createProgram(ProgramDto programDto) {
        try {
            return programMapper.toProgramDto(programRepository.save(programMapper.toProgram(programDto)));
        } catch (Exception e) {
            LOGGER.error("Failed to create program", e);
            throw new IllegalArgumentException("Failed to create program", e);
        }
    }

    /**
     * Updates an existing program.
     *
     * @param programId  The program ID.
     * @param programDto The program details.
     * @return The updated program.
     */
    @Override
    public ProgramDto updateProgram(int programId, ProgramDto programDto) {
        return programRepository.findById(programId)
                .map(program -> {
                    try {
                        program.setName(programDto.getName());
                        program.setDescription(programDto.getDescription());
                        program.setActive(programDto.isActive());
                        program.setLastModifiedBy(CurrentUser.getCurrentUser().getPersonId());
                        program.setLastModifiedAt(LocalDateTime.now());
                        return programMapper.toProgramDto(programRepository.save(program));
                    } catch (Exception e) {
                        throw new IllegalArgumentException(format("Failed to update program: %s", e.getMessage()), e);
                    }
                }).orElseThrow(() ->
                        new ResourceNotFoundException("Program", "Id", String.valueOf(programId)));
    }

    /**
     * Deletes a program.
     *
     * @param id        The program ID.
     * @param recordVoidRequest RecordVoidRequest request object.
     */
    @Override
    public void deleteProgram(int id, RecordVoidRequest recordVoidRequest) {
        programRepository.findById(id)
                .map(program -> {
                    try {
                        program.setVoided(true);
                        program.setVoidedAt(LocalDateTime.now());
                        program.setVoidedBy(CurrentUser.getCurrentUser().getPersonId());
                        program.setVoidReason(recordVoidRequest.getVoidReason());
                        return programMapper.toProgramDto(programRepository.save(program));
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Error deleting program: " + e.getMessage(), e);
                    }
                }).orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Program", "Id", String.valueOf(id)));
    }

    /**
     * Retrieves a program by its ID.
     *
     * @param id The ID of the program to retrieve.
     * @return The program.
     */
    @Override
    public ProgramDto getProgram(int id) {
        return programMapper.toProgramDto(programRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Program", "Id", String.valueOf(id))));
    }

    /**
     * Retrieves all programs.
     *
     * @return A list of programs.
     */
    @Transactional
    @Override
    public List<ProgramDto> getAllPrograms() {
        return programRepository.findAll().stream().map(programMapper::toProgramDto).toList();
    }
}
