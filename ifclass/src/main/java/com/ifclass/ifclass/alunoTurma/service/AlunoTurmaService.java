package com.ifclass.ifclass.alunoTurma.service;

import com.ifclass.ifclass.alunoTurma.model.AlunoTurma;
import com.ifclass.ifclass.alunoTurma.repository.AlunoTurmaRepository;
import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import com.ifclass.ifclass.turma.model.Turma;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class AlunoTurmaService {
    @Autowired
    private AlunoTurmaRepository alunoTurmaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private TurmaRepository turmaRepository;

    public void adicionarAlunoNaTurma(Long alunoId, Long turmaId) {
        Usuario aluno = usuarioRepository.findById(alunoId).orElseThrow();
        // Verifica se o aluno já está em alguma turma
        boolean jaVinculado = !alunoTurmaRepository.findByAluno(aluno).isEmpty();
        if (jaVinculado) return;
        Turma turma = turmaRepository.findById(turmaId).orElseThrow();
        AlunoTurma alunoTurma = new AlunoTurma();
        alunoTurma.setAluno(aluno);
        alunoTurma.setTurma(turma);
        alunoTurma.setDataMatricula(LocalDate.now());
        alunoTurmaRepository.save(alunoTurma);
    }

    public void adicionarAlunosNaTurmaEmLote(java.util.List<Long> alunosIds, Long turmaId) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow();
        LocalDate dataMatricula = LocalDate.now();
        for (Long alunoId : alunosIds) {
            Usuario aluno = usuarioRepository.findById(alunoId).orElseThrow();
            boolean jaVinculado = !alunoTurmaRepository.findByAluno(aluno).isEmpty();
            if (jaVinculado) continue;
            AlunoTurma alunoTurma = new AlunoTurma();
            alunoTurma.setAluno(aluno);
            alunoTurma.setTurma(turma);
            alunoTurma.setDataMatricula(dataMatricula);
            alunoTurmaRepository.save(alunoTurma);
        }
    }

    public java.util.List<Usuario> listarAlunosPorTurma(Long turmaId) {
        Turma turma = turmaRepository.findById(turmaId).orElseThrow();
        java.util.List<AlunoTurma> vinculos = alunoTurmaRepository.findByTurma(turma);
        java.util.List<Usuario> alunos = new java.util.ArrayList<>();
        for (AlunoTurma at : vinculos) {
            alunos.add(at.getAluno());
        }
        return alunos;
    }

    public java.util.List<Long> listarIdsAlunosVinculados() {
        return alunoTurmaRepository.findAllAlunosVinculadosIds();
    }

    public void desvincularAlunoDaTurma(Long alunoId) {
        Usuario aluno = usuarioRepository.findById(alunoId).orElseThrow();
        java.util.List<AlunoTurma> vinculos = alunoTurmaRepository.findByAluno(aluno);
        for (AlunoTurma at : vinculos) {
            alunoTurmaRepository.delete(at);
        }
    }
} 