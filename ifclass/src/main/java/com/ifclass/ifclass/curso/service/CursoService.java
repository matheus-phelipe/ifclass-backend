package com.ifclass.ifclass.curso.service;

import com.ifclass.ifclass.common.exception.ResourceConflictException;
import com.ifclass.ifclass.common.exception.ResourceNotFoundException;
import com.ifclass.ifclass.curso.model.Curso;
import com.ifclass.ifclass.curso.repository.CursoRepository;
import com.ifclass.ifclass.util.log.AppLogger;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CursoService {
    @Autowired
    private CursoRepository repo;

    @Autowired
    private AppLogger appLogger;

    @Cacheable(value = "cursos", key = "'all'")
    public List<Curso> listar() {
        return repo.findAll();
    }

    @Transactional
    @CacheEvict(value = "cursos", allEntries = true)
    public Curso salvar(Curso curso) {
        if (repo.findByNome(curso.getNome()).isPresent()) {
            String motivo = "Tentativa de criar curso com nome já existente: " + curso.getNome();
            appLogger.logCrudWarning("Curso", "CRIACAO", motivo);

            throw new ResourceConflictException("Já existe um curso com o nome: " + curso.getNome());
        }
        if (repo.findByCodigo(curso.getCodigo()).isPresent()) {
            String motivo = "Tentativa de criar curso com código já existente: " + curso.getCodigo();
            appLogger.logCrudWarning("Curso", "CRIACAO", motivo);

            throw new ResourceConflictException("Já existe um curso com o código: " + curso.getCodigo());
        }
        Curso cursoSalvo = repo.save(curso);
    
        appLogger.logCrudSuccess("Curso", "CRIACAO", "ID: " + cursoSalvo.getId() + ", Nome: " + cursoSalvo.getNome());

        return cursoSalvo;
    }

    @Cacheable(value = "cursos", key = "#id")
    public Optional<Curso> buscarPorId(Long id) {
        return repo.findById(id);
    }

    @Transactional
    @CacheEvict(value = "cursos", allEntries = true)
    public Curso atualizar(Long id, Curso cursoAtualizado) {
        Curso cursoExistente = repo.findById(id).orElseThrow(() -> {
        String motivo = "Tentativa de atualizar curso não encontrado com ID: " + id;
        appLogger.logCrudWarning("Curso", "ATUALIZACAO", motivo);

        return new ResourceNotFoundException("Curso não encontrado com ID: " + id);
    });

        if (repo.findByNomeAndIdNot(cursoAtualizado.getNome(), id).isPresent()) {
            String motivo = "Conflito de nome ao tentar atualizar ID: " + id + ". Nome '" + cursoAtualizado.getNome() + "' já em uso.";
            appLogger.logCrudWarning("Curso", "ATUALIZACAO", motivo);
            throw new ResourceConflictException("Já existe outro curso com o nome: " + cursoAtualizado.getNome());
        }
        if (repo.findByCodigoAndIdNot(cursoAtualizado.getCodigo(), id).isPresent()) {
            String motivo = "Conflito de código ao tentar atualizar ID: " + id + ". Código '" + cursoAtualizado.getCodigo() + "' já em uso.";
            appLogger.logCrudWarning("Curso", "ATUALIZACAO", motivo);
            throw new ResourceConflictException("Já existe outro curso com o código: " + cursoAtualizado.getCodigo());
        }

        cursoExistente.setNome(cursoAtualizado.getNome());
        cursoExistente.setCodigo(cursoAtualizado.getCodigo());
        cursoExistente.setCargaHoraria(cursoAtualizado.getCargaHoraria());
        cursoExistente.setDepartamento(cursoAtualizado.getDepartamento());
        cursoExistente.setDescricao(cursoAtualizado.getDescricao());

        Curso cursoSalvo = repo.save(cursoExistente);

        appLogger.logCrudSuccess("Curso", "ATUALIZACAO", "ID: " + cursoSalvo.getId() + ", Nome: " + cursoSalvo.getNome());

        return cursoSalvo;
    }

    @Transactional
    @CacheEvict(value = "cursos", allEntries = true)
    public void excluir(Long id) {
        if (!repo.existsById(id)) {
            String motivo = "Curso não encontrado com ID: " + id + " para exclusão.";
            appLogger.logCrudWarning("Curso", "EXCLUSAO", motivo);
            throw new ResourceNotFoundException("Curso não encontrado com ID: " + id + " para exclusão.");
        }
        repo.deleteById(id);

        appLogger.logCrudSuccess("Curso", "EXCLUSAO", "ID: " + id);
    }
}