package com.ifclass.ifclass.sala.service;

import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {

    @Autowired
    private SalaRepository repository;

    @Cacheable(value = "salas", key = "'all'")
    public List<Sala> listar() {
        return repository.findAll();
    }

    @CacheEvict(value = "salas", allEntries = true)
    public Sala salvar(Sala sala) {
        return repository.save(sala);
    }

    @CacheEvict(value = "salas", allEntries = true)
    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
