package com.ifclass.ifclass.disciplina.service;

import com.ifclass.ifclass.disciplina.model.Disciplina;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DisciplinaService {

    @Autowired
    private DisciplinaRepository repository;

    @Cacheable(value = "disciplinas", key = "'all'")
    public List<Disciplina> listar() {
        return repository.findAll();
    }

    @CacheEvict(value = "disciplinas", allEntries = true)
    public Disciplina salvar(Disciplina disciplina) {
        return repository.save(disciplina);
    }

    @CacheEvict(value = "disciplinas", allEntries = true)
    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
