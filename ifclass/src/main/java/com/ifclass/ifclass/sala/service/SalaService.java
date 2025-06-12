package com.ifclass.ifclass.sala.service;

import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalaService {

    @Autowired
    private SalaRepository repository;

    public List<Sala> listar() {
        return repository.findAll();
    }

    public Sala salvar(Sala sala) {
        return repository.save(sala);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
