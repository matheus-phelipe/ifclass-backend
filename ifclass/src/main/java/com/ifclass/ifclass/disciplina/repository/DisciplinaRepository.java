package com.ifclass.ifclass.disciplina.repository;

import com.ifclass.ifclass.disciplina.model.Disciplina;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DisciplinaRepository extends JpaRepository<Disciplina, Long> {
    
    /**
     * Busca disciplinas relacionadas a um curso específico
     * Retorna: [nome, codigo]
     */
    @Query("SELECT d.nome, d.codigo FROM Disciplina d WHERE d.curso.id = :cursoId")
    List<Object[]> findDisciplinasByCursoId(@Param("cursoId") Long cursoId);
}

