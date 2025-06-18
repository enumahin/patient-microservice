package com.alienworkspace.cdr.patient.model.mapper;

import com.alienworkspace.cdr.model.dto.patient.ProgramDto;
import com.alienworkspace.cdr.patient.model.Program;
import com.alienworkspace.cdr.patient.model.audit.AuditTrailMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


/**
 * Mapper for Program entity.
 *
 * @author Ikenumah (enumahinm@gmail.com)
 */
@Mapper(componentModel = "spring")
public interface ProgramMapper {

    ProgramMapper INSTANCE = Mappers.getMapper(ProgramMapper.class);

    /**
     * Maps a ProgramDto to a Program entity.
     *
     * @param programDto The ProgramDto to map.
     * @return The mapped Program entity.
     */
    default Program toProgram(ProgramDto programDto) {
        Program program = Program.builder()
                .programId(programDto.getProgramId())
                .name(programDto.getName())
                .programCode(programDto.getProgramCode())
                .description(programDto.getDescription())
                .active(programDto.isActive())
                .build();
        AuditTrailMapper.mapFromDto(programDto, program);
        return program;
    }

    /**
     * Maps a Program entity to a ProgramDto.
     *
     * @param program The Program entity to map.
     * @return The mapped ProgramDto.
     */
    default ProgramDto toProgramDto(Program program) {
        ProgramDto programDto = ProgramDto.builder()
                .programId(program.getProgramId())
                .name(program.getName())
                .programCode(program.getProgramCode())
                .description(program.getDescription())
                .active(program.isActive())
                .build();
        AuditTrailMapper.mapToDto(program, programDto);
        return programDto;
    }
}
