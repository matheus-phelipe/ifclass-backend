package com.ifclass.ifclass.disciplina.service;

import com.ifclass.ifclass.disciplina.model.Disciplina;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.util.log.AppLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository repository;

    @Autowired
    private AppLogger appLogger;

    @Cacheable(value = "disciplinas", key = "'all'")
    public List<Disciplina> listar() {
        return repository.findAll();
    }

    @CacheEvict(value = "disciplinas", allEntries = true)
    public Disciplina salvar(Disciplina disciplina) {
        String operacao = (disciplina.getId() == null) ? "CRIACAO" : "ATUALIZACAO";
    
        Disciplina disciplinaSalva = repository.save(disciplina);
        
        appLogger.logCrudSuccess("Disciplina", operacao, "ID: " + disciplinaSalva.getId() + ", Nome: " + disciplinaSalva.getNome());
        
        return disciplinaSalva;
    }

    public Disciplina obterPorId(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @CacheEvict(value = "disciplinas", allEntries = true)
    @Transactional
    public void excluir(Long id) {
        Disciplina disciplina = repository.findById(id).orElseThrow(() -> {
        String motivo = "Tentativa de excluir disciplina não encontrada com ID: " + id;
        appLogger.logCrudWarning("Disciplina", "EXCLUSAO", motivo);

        return new ResponseStatusException(HttpStatus.NOT_FOUND, "Disciplina não encontrada.");
    });

        // Desvincular de todos os professores
        for (Usuario professor : disciplina.getProfessores()) {
            professor.getDisciplinas().remove(disciplina);
        }
        
        repository.delete(disciplina);

        appLogger.logCrudSuccess("Disciplina", "EXCLUSAO", "ID: " + id);
    }

    @CacheEvict(value = "disciplinas", allEntries = true)
    public Disciplina atualizar(Long id, Disciplina disciplinaAtualizada) {
        Disciplina disciplina = repository.findById(id).orElseThrow(() -> {
        String motivo = "Tentativa de atualizar disciplina não encontrada com ID: " + id;
        appLogger.logCrudWarning("Disciplina", "ATUALIZACAO", motivo);
        return new ResponseStatusException(HttpStatus.NOT_FOUND);
        });

        disciplina.setNome(disciplinaAtualizada.getNome());
        disciplina.setCodigo(disciplinaAtualizada.getCodigo());
        disciplina.setDepartamento(disciplinaAtualizada.getDepartamento());
        disciplina.setDescricao(disciplinaAtualizada.getDescricao());
        disciplina.setCurso(disciplinaAtualizada.getCurso());
        disciplina.setCargaHoraria(disciplinaAtualizada.getCargaHoraria());

        Disciplina disciplinaSalva = repository.save(disciplina);
        
        appLogger.logCrudSuccess("Disciplina", "ATUALIZACAO", "ID: " + disciplinaSalva.getId());

        return disciplinaSalva;
    }
}
