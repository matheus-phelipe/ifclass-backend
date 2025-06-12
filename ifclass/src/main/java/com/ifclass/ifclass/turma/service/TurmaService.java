package com.ifclass.ifclass.turma.service;

import com.ifclass.ifclass.turma.model.Turma;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    public List<Turma> listar() {
        return repository.findAll();
    }

    public Turma salvar(Turma turma) {
        return repository.save(turma);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}
