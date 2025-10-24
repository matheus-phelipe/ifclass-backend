package com.ifclass.ifclass.turma.service;

import com.ifclass.ifclass.alunoTurma.repository.AlunoTurmaRepository;
import com.ifclass.ifclass.turma.model.Turma;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import com.ifclass.ifclass.util.log.AppLogger;

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

    @Autowired
    private AppLogger appLogger;

    public List<Turma> listar() {
        return repository.findAll();
    }

    public Turma salvar(Turma turma) {
        Turma turmaSalva = repository.save(turma);
        appLogger.logCrudSuccess("Turma", "CRIACAO", "ID: " + turmaSalva.getId());
        return turmaSalva;
    }

    public Turma atualizar(Turma turma) {
        if (!repository.existsById(turma.getId())) {
            String motivo = "Tentativa de atualizar turma não encontrada com ID: " + turma.getId();
            appLogger.logCrudWarning("Turma", "ATUALIZACAO", motivo);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada");
        }
        Turma turmaAtualizada = repository.save(turma);
    
        appLogger.logCrudSuccess("Turma", "ATUALIZACAO", "ID: " + turmaAtualizada.getId());
    
        return turmaAtualizada;
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            String motivo = "Tentativa de excluir turma não encontrada com ID: " + id;
            appLogger.logCrudWarning("Turma", "EXCLUSAO", motivo);

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada");
        }
        // 1. Desvincula todos os alunos da turma
        alunoTurmaRepository.deleteAllByTurmaId(id);
        
        // 2. Exclui a turma, agora sem vínculos
        repository.deleteById(id);

        appLogger.logCrudSuccess("Turma", "EXCLUSAO", "ID: " + id);
    }
} 