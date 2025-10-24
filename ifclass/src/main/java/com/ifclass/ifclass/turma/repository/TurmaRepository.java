package com.ifclass.ifclass.turma.repository;

import com.ifclass.ifclass.turma.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TurmaRepository extends JpaRepository<Turma, Long> {
    
    /**
     * Busca turmas relacionadas a um curso específico
     * Retorna: [id, ano, semestre]
     */
    @Query("SELECT t.id, t.ano, t.semestre FROM Turma t WHERE t.curso.id = :cursoId")
    List<Object[]> findTurmasByCursoId(@Param("cursoId") Long cursoId);
}

