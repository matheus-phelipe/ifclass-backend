package com.ifclass.ifclass.aula.repository;

import com.ifclass.ifclass.aula.model.Aula;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;

@Repository
public interface AulaRepository extends JpaRepository<Aula, Long> {
    List<Aula> findByTurmaIdAndDiaSemana(Long turmaId, DayOfWeek diaSemana);

    List<Aula> findByProfessorIdAndDiaSemana(Long professorId, DayOfWeek diaSemana);

    List<Aula> findByProfessorId(Long professorId);

    List<Aula> findByDiaSemana(DayOfWeek diaSemana);
    
    /**
     * Busca aulas relacionadas a uma turma específica
     * Retorna: [id, disciplina.nome, diaSemana]
     */
    @Query("SELECT a.id, d.nome, a.diaSemana FROM Aula a JOIN a.disciplina d WHERE a.turma.id = :turmaId")
    List<Object[]> findAulasByTurmaId(@Param("turmaId") Long turmaId);
}