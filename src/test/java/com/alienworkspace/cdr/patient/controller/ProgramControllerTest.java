package com.alienworkspace.cdr.patient.controller;

import com.alienworkspace.cdr.patient.exception.ResourceNotFoundException;
import com.alienworkspace.cdr.patient.service.ProgramService;
import com.alienworkspace.cdr.model.dto.patient.ProgramDto;
import com.alienworkspace.cdr.model.helper.RecordVoidRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static com.alienworkspace.cdr.patient.helpers.Constants.PROGRAM_BASE_URL;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProgramController.class)
public class ProgramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProgramService programService;

    private ProgramDto.ProgramDtoBuilder programDtoBuilder = ProgramDto.builder();

    @BeforeEach
    public void setUp() {
        programDtoBuilder
                .name("Test Program")
                .programCode("TEST")
                .description("Test Description")
                .active(true);
    }

    @DisplayName("Test Create Program")
    @Test
    public void testCreateProgram() throws Exception {
        // Arrange
        ProgramDto programDto = programDtoBuilder.build();
        when(programService.createProgram(programDto)).thenReturn(programDtoBuilder.programId(1).build());

        // Act
        ResultActions result = mockMvc.perform(
                post(PROGRAM_BASE_URL)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(programDto))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.programId").value(1))
                .andExpect(jsonPath("$.name").value(programDto.getName()))
                .andExpect(jsonPath("$.description").value(programDto.getDescription()))
                .andExpect(jsonPath("$.active").value(programDto.isActive()));
    }


    @DisplayName("Test Update Program")
    @Test
    public void testUpdateProgram() throws Exception {
        // Arrange
        ProgramDto.ProgramDtoBuilder updatedProgramDto = programDtoBuilder
                .name("Updated Program")
                .description("Updated Description")
                .active(false);
        ProgramDto updatedProgram = updatedProgramDto.programId(1).build();
        when(programService.updateProgram(1, updatedProgramDto.build())).thenReturn(updatedProgram);

        // Act
        ResultActions result = mockMvc.perform(
                put(PROGRAM_BASE_URL + "/{programId}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(updatedProgramDto.build()))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.programId").value(1))
                .andExpect(jsonPath("$.name").value(updatedProgram.getName()))
                .andExpect(jsonPath("$.description").value(updatedProgram.getDescription()))
                .andExpect(jsonPath("$.active").value(updatedProgram.isActive()));
    }


    @DisplayName("Test Update Non Existing Program")
    @Test
    public void testUpdateNonExistingProgram() throws Exception {
        // Arrange
        ProgramDto programDto = programDtoBuilder.build();
        when(programService.updateProgram(1, programDto))
                .thenThrow(new ResourceNotFoundException("Program", "programId", "1"));

        // Act
        ResultActions result = mockMvc.perform(
                put(PROGRAM_BASE_URL + "/{programId}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(programDto))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.errorMessage").value(CoreMatchers.containsString("Program with programId of '1' not found")));
    }


    @DisplayName("Test Get Program")
    @Test
    public void testGetProgram() throws Exception {
        // Arrange
        ProgramDto programDto = programDtoBuilder.build();
        when(programService.getProgram(1)).thenReturn(programDtoBuilder.programId(1).build());

        // Act
        ResultActions result = mockMvc.perform(
                get(PROGRAM_BASE_URL + "/{programId}", 1)
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.programId").value(1))
                .andExpect(jsonPath("$.name").value(programDto.getName()))
                .andExpect(jsonPath("$.description").value(programDto.getDescription()))
                .andExpect(jsonPath("$.active").value(programDto.isActive()));
    }

    @DisplayName("Test Get All Programs")
    @Test
    public void testGetAllPrograms() throws Exception {
        // Arrange
        List<ProgramDto> programs = List.of(programDtoBuilder.programId(1).build());
        when(programService.getAllPrograms()).thenReturn(programs);

        // Act
        ResultActions result = mockMvc.perform(
                get(PROGRAM_BASE_URL)
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].programId").value(1))
                .andExpect(jsonPath("$.[0].name").value(programs.get(0).getName()))
                .andExpect(jsonPath("$.[0].description").value(programs.get(0).getDescription()))
                .andExpect(jsonPath("$.[0].active").value(programs.get(0).isActive()));
    }


    @DisplayName("Test Delete Program")
    @Test
    public void testDeleteProgram() throws Exception {
        // Arrange
        RecordVoidRequest recordVoidRequest = RecordVoidRequest.builder()
                .voidReason("Test Void Reason")
                .build();
        doNothing().when(programService).deleteProgram(1,recordVoidRequest);

        // Act
        ResultActions result = mockMvc.perform(
                delete(PROGRAM_BASE_URL + "/{programId}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(recordVoidRequest))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isOk());
    }


    @DisplayName("Test Delete NonExisting Program")
    @Test
    public void testDeleteNonExistingProgram() throws Exception {
        // Arrange
        RecordVoidRequest recordVoidRequest = RecordVoidRequest.builder()
                .voidReason("Test Void Reason")
                .build();
        doThrow(new ResourceNotFoundException("Program", "programId", "1")).when(programService).deleteProgram(1,recordVoidRequest);

        // Act
        ResultActions result = mockMvc.perform(
                delete(PROGRAM_BASE_URL + "/{programId}", 1)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(recordVoidRequest))
        );

        // Assert
        result.andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.errorCode").value(404))
                .andExpect(jsonPath("$.errorMessage").value("Program with programId of '1' not found"));
    }
}
