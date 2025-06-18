package com.alienworkspace.cdr.patient.repository;

import com.alienworkspace.cdr.patient.model.Program;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Repository for Program entity.
 *
 * @author Ikenumah (enumahinm@gmail.com)
 */
@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {

    /**
     * Find all programs.
     *
     * @return List of programs
     */
    @Query("SELECT p FROM Program p LEFT JOIN p.programPatients")
    @Override
    List<Program> findAll();
}
