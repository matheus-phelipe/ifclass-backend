package com.ifclass.ifclass.aula.repository;

import com.ifclass.ifclass.aula.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByTurmaIdAndDiaSemana(Long turmaId, DayOfWeek diaSemana);

    List<Aula> findByProfessorIdAndDiaSemana(Long professorId, DayOfWeek diaSemana);

    List<Aula> findByProfessorId(Long professorId);

    List<Aula> findByDiaSemana(DayOfWeek diaSemana);
}