package com.alienworkspace.cdr.patient.model.mapper;

import com.alienworkspace.cdr.patient.model.Program;
import com.alienworkspace.cdr.model.dto.patient.ProgramDto;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProgramMapperTest {

    @Test
    void toProgram_ShouldMapAllFields_WhenGivenValidProgramDto() {
        // Arrange
        LocalDateTime createdAt = LocalDateTime.now();

        ProgramDto programDto = ProgramDto.builder()
                .programId(123)
                .programCode("TEST")
                .name("Test Program")
                .description("Test Description")
                .active(true)
                .build();

        programDto.setCreatedBy(1L);
        programDto.setCreatedAt(createdAt);

        Program program = Program.builder()
                .programId(123)
                .name("Test Program")
                .programCode("TEST")
                .description("Test Description")
                .active(true)
                .build();
        program.setCreatedBy(1L);
        program.setCreatedAt(createdAt);

        // Act
        Program actualProgram = ProgramMapper.INSTANCE.toProgram(programDto);

        // Assert
        assertNotNull(actualProgram);
        assertEquals(actualProgram.getProgramId(), program.getProgramId());
        assertEquals(actualProgram.getName(), program.getName());
        assertEquals(actualProgram.getDescription(), program.getDescription());
        assertEquals(actualProgram.isActive(), program.isActive());
        assertEquals(actualProgram.getCreatedBy(), program.getCreatedBy());
        assertEquals(actualProgram.getCreatedAt(), program.getCreatedAt());

    }
}