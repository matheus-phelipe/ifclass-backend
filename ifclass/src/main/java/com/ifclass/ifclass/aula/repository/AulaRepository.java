package com.ifclass.ifclass.aula.repository;

import com.ifclass.ifclass.aula.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByTurmaIdAndData(Long turmaId, LocalDate data);
    List<Aula> findByProfessorIdAndData(Long professorId, LocalDate data);
    List<Aula> findByProfessorId(Long professorId);
} 