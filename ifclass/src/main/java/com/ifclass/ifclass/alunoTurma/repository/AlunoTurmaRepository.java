package com.ifclass.ifclass.alunoTurma.repository;

import com.ifclass.ifclass.alunoTurma.model.AlunoTurma;
import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.turma.model.Turma;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface AlunoTurmaRepository extends JpaRepository<AlunoTurma, Long> {
    List<AlunoTurma> findByTurma(Turma turma);
    List<AlunoTurma> findByAluno(Usuario aluno);
    @Query("SELECT a.aluno.id FROM AlunoTurma a")
    java.util.List<Long> findAllAlunosVinculadosIds();
    Optional<AlunoTurma> findByAlunoId(Long alunoId);
    void deleteAllByTurmaId(Long turmaId);
} 