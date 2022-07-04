package com.finx.persistence;

import com.finx.domain.programs.Program;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface ProgramRepository extends CrudRepository<Program, Long> {

    Optional<Program> findByName(String name);

    Set<Program> findAll();
}
