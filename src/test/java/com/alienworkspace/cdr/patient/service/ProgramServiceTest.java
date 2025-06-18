package com.alienworkspace.cdr.patient.service;

import com.alienworkspace.cdr.patient.model.Program;
import com.alienworkspace.cdr.patient.model.mapper.ProgramMapper;
import com.alienworkspace.cdr.patient.repository.ProgramRepository;
import com.alienworkspace.cdr.patient.service.impl.ProgramServiceImpl;
import com.alienworkspace.cdr.model.dto.patient.ProgramDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProgramServiceTest {

    @Mock
    ProgramRepository programRepository;
    @Mock
    ProgramMapper programMapper;

    @InjectMocks
    ProgramServiceImpl programService;

    private final ProgramDto.ProgramDtoBuilder programDtoBuilder = ProgramDto.builder();
    private final Program.ProgramBuilder programBuilder = Program.builder();

    @BeforeEach
    public void setUp() {
        programDtoBuilder
                .programId(1)
                .name("Test Program")
                .description("Test Description")
                .active(true);
        programBuilder
                .programId(1)
                .name("Test Program")
                .description("Test Description")
                .active(true);
    }

    @DisplayName("Test Create Program")
    @Test
    public void testCreateProgram() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();
        ProgramDto programDto = programDtoBuilder.build();

        Program program = programBuilder.build();

        Program savedProgram = programBuilder.build();
        program.setCreatedBy(1L);
        program.setCreatedAt(createdAt);

        ProgramDto expectedProgramDto = programDtoBuilder.build();
        expectedProgramDto.setCreatedBy(1L);
        expectedProgramDto.setCreatedAt(createdAt);

        when(programMapper.toProgram(programDto)).thenReturn(program);
        when(programMapper.toProgramDto(savedProgram)).thenReturn(expectedProgramDto);
        when(programRepository.save(program))
                .thenReturn(savedProgram);

        // Act
        ProgramDto actualProgramDto = programService.createProgram(programDto);

        // Assert
        assertNotNull(actualProgramDto);
        assertEquals(programDto.getProgramId(), actualProgramDto.getProgramId());
        assertNotNull(actualProgramDto.getCreatedAt());
        assertNotNull(actualProgramDto.getCreatedBy());
        verify(programRepository).save(programMapper.toProgram(programDto));
        verifyNoMoreInteractions(programRepository);
    }

    @DisplayName("Test Update Program")
    @Test
    public void testUpdateProgram() {
        // Arrange
        LocalDateTime lastModifiedAt = LocalDateTime.now();
        ProgramDto programDto = programDtoBuilder.build();
        programDto.setLastModifiedBy(2L);
        programDto.setLastModifiedAt(lastModifiedAt);

        Program existingProgram = programBuilder.build();
        Program updatedProgram = programBuilder.build();
        updatedProgram.setLastModifiedBy(2L);
        updatedProgram.setLastModifiedAt(lastModifiedAt);

        ProgramDto expectedProgramDto = programDtoBuilder.build();
        expectedProgramDto.setLastModifiedBy(2L);
        expectedProgramDto.setLastModifiedAt(lastModifiedAt);

        when(programRepository.findById(programDto.getProgramId())).thenReturn(Optional.of(existingProgram));
        when(programRepository.save(existingProgram)).thenReturn(updatedProgram);
        when(programMapper.toProgramDto(updatedProgram)).thenReturn(expectedProgramDto);

        // Act
        ProgramDto actualProgramDto = programService.updateProgram(programDto.getProgramId(), programDto);

        // Assert
        assertNotNull(actualProgramDto);
        assertEquals(programDto.getProgramId(), actualProgramDto.getProgramId());
        assertEquals(expectedProgramDto.getLastModifiedBy(), actualProgramDto.getLastModifiedBy());
        assertEquals(expectedProgramDto.getLastModifiedAt(), actualProgramDto.getLastModifiedAt());
        verify(programRepository).save(existingProgram);
        verifyNoMoreInteractions(programRepository);
    }

    @DisplayName("Test Delete Program")
    @Test
    public void testDeleteProgram() {
        // Arrange
        RecordVoidRequest request = new RecordVoidRequest();
        request.setVoidReason("Test");

        Program existingProgram = programBuilder.build();

        Program voidedProgram = programBuilder.build();
        voidedProgram.setVoided(true);
        voidedProgram.setVoidedBy(3L);
        voidedProgram.setVoidedAt(LocalDateTime.now());

        ProgramDto programDto = programDtoBuilder.build();
        programDto.setCreatedBy(1L);
        programDto.setCreatedAt(LocalDateTime.now());

        when(programRepository.findById(1))
                .thenReturn(Optional.of(existingProgram));

        when(programRepository.findById(1))
                .thenReturn(Optional.of(existingProgram));
        when(programRepository.save(existingProgram)).thenReturn(voidedProgram);
        when(programMapper.toProgramDto(voidedProgram)).thenReturn(programDto);

        // Act
        programService.deleteProgram(1, request);

        // Assert
        verify(programRepository).save(existingProgram);
        verifyNoMoreInteractions(programRepository);
        assertTrue(existingProgram.isVoided());
        assertNotNull(existingProgram.getVoidedAt());
    }

    @DisplayName("Test Get Program")
    @Test
    public void testGetProgram() {
        // Arrange
        int programId = 1;
        Program existingProgram = programBuilder.build();
        ProgramDto expectedProgramDto = programDtoBuilder.build();

        when(programRepository.findById(programId)).thenReturn(Optional.of(existingProgram));
        when(programMapper.toProgramDto(existingProgram)).thenReturn(expectedProgramDto);

        // Act
        ProgramDto actualProgramDto = programService.getProgram(programId);

        // Assert
        assertNotNull(actualProgramDto);
        assertEquals(expectedProgramDto, actualProgramDto);
        verify(programRepository).findById(programId);
        verifyNoMoreInteractions(programRepository);
    }

    @DisplayName("Test Get All Programs")
    @Test
    public void testGetAllPrograms() {
        // Arrange
        List<Program> programs = List.of(programBuilder.build());
        List<ProgramDto> expectedProgramDtos = List.of(programDtoBuilder.build());

        when(programRepository.findAll()).thenReturn(programs);
        when(programMapper.toProgramDto(any(Program.class))).thenReturn(programDtoBuilder.build());

        // Act
        List<ProgramDto> actualProgramDtos = programService.getAllPrograms();

        // Assert
        assertNotNull(actualProgramDtos);
        assertEquals(expectedProgramDtos.size(), actualProgramDtos.size());
        verify(programRepository).findAll();
        verifyNoMoreInteractions(programRepository);
    }
}
