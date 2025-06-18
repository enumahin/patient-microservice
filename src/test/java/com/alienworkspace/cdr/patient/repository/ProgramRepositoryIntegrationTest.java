package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.integration.AbstractionContainerBaseTest;
import com.alienworkspace.cdr.patient.model.Program;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.Assert.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ProgramRepositoryIntegrationTest extends AbstractionContainerBaseTest {

    @Autowired
    private ProgramRepository programRepository;

    private Program.ProgramBuilder programBuilder = Program.builder();

    @BeforeEach
    public void setUp() {
        programRepository.deleteAll();

        programBuilder
                .name("Test Program")
                .programCode( "TEST")
                .description("Test Description")
                .active(true);
    }

    @Test
    @DisplayName("Test Create Program")
    public void testCreateProgram() {
        // Arrange
        Program program = programBuilder.build();

        program.setCreatedBy(1L);
        program.setCreatedAt(LocalDateTime.now());

        // Act
        Program actualProgram = programRepository.save(program);

        // Assert
        assertNotNull(actualProgram);

    }
}
