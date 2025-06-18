package com.alienworkspace.cdr.patient.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Program model class.
 */
class ProgramTest {

    @Test
    void testProgramBuilder() {
        // Act
        Program program = Program.builder()
                .programId(1)
                .name("HIV Care")
                .description("HIV Care and Treatment Program")
                .active(true)
                .build();

        // Assert
        assertEquals(1, program.getProgramId());
        assertEquals("HIV Care", program.getName());
        assertEquals("HIV Care and Treatment Program", program.getDescription());
        assertTrue(program.isActive());
        assertNotNull(program.getProgramPatients());
        assertTrue(program.getProgramPatients().isEmpty());
    }

    @Test
    void testNoArgsConstructor() {
        // Act
        Program program = new Program();

        // Assert
        assertNotNull(program);
        assertNotNull(program.getProgramPatients());
    }

    @Test
    void testSettersAndGetters() {
        // Arrange
        Program program = new Program();

        // Act
        program.setName("HIV Care");
        program.setDescription("HIV Care and Treatment Program");
        program.setActive(true);

        // Assert
        assertEquals("HIV Care", program.getName());
        assertEquals("HIV Care and Treatment Program", program.getDescription());
        assertTrue(program.isActive());
    }

    @Test
    void testProgramPatientsImmutability() {
        // Arrange
        Program program = new Program();
        Set<PatientProgram> patients = program.getProgramPatients();

        // Act & Assert
        assertThrows(UnsupportedOperationException.class, () -> 
            patients.add(PatientProgram.builder().build())
        );
    }

    @Test
    void testEquals() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        String uuid = UUID.randomUUID().toString();
        
        Program program1 = Program.builder()
                .programId(1)
                .name("HIV Care")
                .description("HIV Care and Treatment Program")
                .active(true)
                .build();
        program1.setCreatedAt(now);
        program1.setCreatedBy(1L);
        program1.setUuid(uuid);

        Program program2 = Program.builder()
                .programId(1)
                .name("HIV Care")
                .description("HIV Care and Treatment Program")
                .active(true)
                .build();
        program2.setCreatedAt(now);
        program2.setCreatedBy(1L);
        program2.setUuid(uuid);

        Program differentProgram = Program.builder()
                .programId(2)
                .name("TB Care")
                .description("TB Care and Treatment Program")
                .active(true)
                .build();

        // Assert
        assertEquals(program1, program2);
        assertNotEquals(program1, differentProgram);
        assertNotEquals(program1, null);
        assertNotEquals(program1, new Object());
    }

    @Test
    void testHashCode() {
        // Arrange
        LocalDateTime now = LocalDateTime.now();
        String uuid = UUID.randomUUID().toString();
        
        Program program1 = Program.builder()
                .programId(1)
                .name("HIV Care")
                .description("HIV Care and Treatment Program")
                .active(true)
                .build();
        program1.setCreatedAt(now);
        program1.setCreatedBy(1L);
        program1.setUuid(uuid);

        Program program2 = Program.builder()
                .programId(1)
                .name("HIV Care")
                .description("HIV Care and Treatment Program")
                .active(true)
                .build();
        program2.setCreatedAt(now);
        program2.setCreatedBy(1L);
        program2.setUuid(uuid);

        // Assert
        assertEquals(program1.hashCode(), program2.hashCode());
    }

    @Test
    void testToString() {
        // Arrange
        Program program = Program.builder()
                .programId(1)
                .name("HIV Care")
                .description("HIV Care and Treatment Program")
                .active(true)
                .build();
        program.setCreatedBy(1L);

        // Act
        String toString = program.toString();

        // Assert
        assertTrue(toString.contains("programId=1"));
        assertTrue(toString.contains("name=HIV Care"));
        assertTrue(toString.contains("description=HIV Care and Treatment Program"));
        assertTrue(toString.contains("active=true"));
        assertTrue(toString.contains("createdBy=1"));
    }

    @Test
    void testAllArgsConstructor() {
        // Arrange
        Set<PatientProgram> patients = Set.of();

        // Act
        Program program = new Program(1, "HIV Care", "HIV-CARE", "HIV Care and Treatment Program", true, patients);

        // Assert
        assertEquals(1, program.getProgramId());
        assertEquals("HIV Care", program.getName());
        assertEquals("HIV Care and Treatment Program", program.getDescription());
        assertTrue(program.isActive());
        assertNotNull(program.getProgramPatients());
    }
} 