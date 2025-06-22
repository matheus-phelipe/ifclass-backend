package com.ifclass.ifclass.turma.service;

import com.ifclass.ifclass.alunoTurma.repository.AlunoTurmaRepository;
import com.ifclass.ifclass.turma.model.Turma;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    @Autowired
    private AlunoTurmaRepository alunoTurmaRepository;

    public List<Turma> listar() {
        return repository.findAll();
    }

    public Turma salvar(Turma turma) {
        return repository.save(turma);
    }

    public Turma atualizar(Turma turma) {
        if (!repository.existsById(turma.getId())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada");
        }
        return repository.save(turma);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada");
        }
        // 1. Desvincula todos os alunos da turma
        alunoTurmaRepository.deleteAllByTurmaId(id);
        
        // 2. Exclui a turma, agora sem vínculos
        repository.deleteById(id);
    }
} 