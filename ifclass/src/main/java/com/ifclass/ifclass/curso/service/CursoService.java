package com.ifclass.ifclass.curso.service;

import com.ifclass.ifclass.common.exception.ResourceConflictException;
import com.ifclass.ifclass.common.exception.ResourceNotFoundException;
import com.ifclass.ifclass.curso.model.Curso;
import com.ifclass.ifclass.curso.repository.CursoRepository;
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

    @Cacheable(value = "cursos", key = "'all'")
    public List<Curso> listar() {
        return repo.findAll();
    }

    @Transactional
    @CacheEvict(value = "cursos", allEntries = true)
    public Curso salvar(Curso curso) {
        if (repo.findByNome(curso.getNome()).isPresent()) {
            throw new ResourceConflictException("Já existe um curso com o nome: " + curso.getNome());
        }
        if (repo.findByCodigo(curso.getCodigo()).isPresent()) {
            throw new ResourceConflictException("Já existe um curso com o código: " + curso.getCodigo());
        }
        return repo.save(curso);
    }

    @Cacheable(value = "cursos", key = "#id")
    public Optional<Curso> buscarPorId(Long id) {
        return repo.findById(id);
    }

    @Transactional
    @CacheEvict(value = "cursos", allEntries = true)
    public Curso atualizar(Long id, Curso cursoAtualizado) {
        Curso cursoExistente = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso não encontrado com ID: " + id));

        if (repo.findByNomeAndIdNot(cursoAtualizado.getNome(), id).isPresent()) {
            throw new ResourceConflictException("Já existe outro curso com o nome: " + cursoAtualizado.getNome());
        }
        if (repo.findByCodigoAndIdNot(cursoAtualizado.getCodigo(), id).isPresent()) {
            throw new ResourceConflictException("Já existe outro curso com o código: " + cursoAtualizado.getCodigo());
        }

        cursoExistente.setNome(cursoAtualizado.getNome());
        cursoExistente.setCodigo(cursoAtualizado.getCodigo());
        cursoExistente.setCargaHoraria(cursoAtualizado.getCargaHoraria());
        cursoExistente.setDepartamento(cursoAtualizado.getDepartamento());
        cursoExistente.setDescricao(cursoAtualizado.getDescricao());

        return repo.save(cursoExistente);
    }

    @Transactional
    @CacheEvict(value = "cursos", allEntries = true)
    public void excluir(Long id) {
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Curso não encontrado com ID: " + id + " para exclusão.");
        }
        repo.deleteById(id);
    }
}