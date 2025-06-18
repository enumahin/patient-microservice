package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.Program;
import com.alienworkspace.cdr.patient.repository.PatientProgramRepository;
import com.alienworkspace.cdr.patient.repository.ProgramRepository;
import com.alienworkspace.cdr.model.dto.patient.ProgramDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Transactional
@Rollback
public class ProgramServiceIntegrationTest extends AbstractionContainerBaseTest{

    @Autowired
    private ProgramRepository programRepository;

    @Autowired
    private PatientProgramRepository patientProgramRepository;

    @Autowired
    private ProgramService programService;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    public void setUp() {
        patientProgramRepository.deleteAllInBatch();
        programRepository.deleteAllInBatch();

        entityManager.flush();
        entityManager.clear();
        entityManager.getEntityManagerFactory().getCache().evictAll();
    }

    @DisplayName("Test Create Program")
    @Test
    public void testCreateProgram() {
        // Arrange
        ProgramDto programDto = ProgramDto.builder()
                .name("Test Program")
                .programCode("TEST")
                .description("Test Description")
                .active(true)
                .build();

        // Act
        ProgramDto actualProgramDto = programService.createProgram(programDto);

        // Assert
        assertNotNull(actualProgramDto);
        assertEquals(programDto.getName(), actualProgramDto.getName());
        assertEquals(programDto.getDescription(), actualProgramDto.getDescription());
        assertEquals(programDto.isActive(), actualProgramDto.isActive());
        assertNotNull(actualProgramDto.getCreatedAt());
        assertNotNull(actualProgramDto.getCreatedBy());
    }

    @DisplayName("Test Update Program")
    @Test
    public void testUpdateProgram() {
        // Arrange
        ProgramDto programDto = ProgramDto.builder()
                .name("Test Program")
                .programCode("TEST")
                .description("Test Description")
                .active(true)
                .build();

        // Act
        ProgramDto newProgramDto = programService.createProgram(programDto);
        newProgramDto.setName("Updated Program");
        newProgramDto.setDescription("Updated Description");
        newProgramDto.setActive(false);
        newProgramDto.setLastModifiedBy(1L);
        ProgramDto actualProgramDto = programService.updateProgram(newProgramDto.getProgramId(), newProgramDto);

        // Assert
        assertNotNull(actualProgramDto);
        assertFalse(actualProgramDto.isActive());
        assertNotNull(actualProgramDto.getLastModifiedAt());
        assertNotNull(actualProgramDto.getLastModifiedBy());
    }

    @DisplayName("Test Update Non Existing Program")
    @Test
    public void testUpdateNonExistingProgram() {
        // Arrange
        ProgramDto programDto = ProgramDto.builder()
                .programId(1)
                .name("Test Program")
                .programCode("TEST")
                .description("Test Description")
                .active(true)
                .build();

        // Act

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> programService.updateProgram(programDto.getProgramId(), programDto));
    }

    @DisplayName("Test Get Program")
    @Test
    public void testGetProgram() {
        // Arrange
        ProgramDto programDto = ProgramDto.builder()
                .name("Test Program")
                .programCode("TEST")
                .description("Test Description")
                .active(true)
                .build();

        // Act
        ProgramDto newProgramDto = programService.createProgram(programDto);
        ProgramDto actualProgramDto = programService.getProgram(newProgramDto.getProgramId());

        // Assert
        assertNotNull(actualProgramDto);
        assertEquals(newProgramDto.getName(), actualProgramDto.getName());
        assertEquals(newProgramDto.getDescription(), actualProgramDto.getDescription());
        assertEquals(newProgramDto.isActive(), actualProgramDto.isActive());
    }

    @DisplayName("Test Get All Programs")
    @Test
    @Transactional
    public void testGetAllPrograms() {
        // Arrange
        Program program1 = Program.builder()
                .name("Test Program 1")
                .programCode( "TEST1")
                .description("Test Description 1")
                .active(true)
                .build();

        Program program2 = Program.builder()
                .name("Test Program 2")
                .programCode( "TEST2")
                .description("Test Description 2")
                .active(true)
                .build();

        // Act
        programRepository.save(program1);
        programRepository.save(program2);
        List<ProgramDto> actualProgramDtos = programService.getAllPrograms();

        // Assert
        assertNotNull(actualProgramDtos);
        assertEquals(2, actualProgramDtos.size());
    }

    @DisplayName("Test Delete Program")
    @Test
    public void testDeleteProgram() {
        // Arrange
        ProgramDto programDto = ProgramDto.builder()
                .name("Test Program")
                .programCode("TEST")
                .description("Test Description")
                .active(true)
                .build();

        // Act
        ProgramDto newProgramDto = programService.createProgram(programDto);
        RecordVoidRequest request = RecordVoidRequest.builder()
                .voidReason("Test Void Reason")
                .build();
        programService.deleteProgram(newProgramDto.getProgramId(), request);

        // Assert
        ProgramDto actualProgramDtos = programService.getProgram(newProgramDto.getProgramId());
        assertTrue(actualProgramDtos.getVoided());
    }

    @DisplayName(("Test Void Non Existing Program"))
    @Test
    public void testVoidNonExistingProgram() {
        // Arrange
        RecordVoidRequest request = RecordVoidRequest.builder()
                .voidReason("Test Void Reason")
                .build();

        // Act

        // Assert
        assertThrows(ResourceNotFoundException.class, () -> programService.deleteProgram(1, request));
    }
}
