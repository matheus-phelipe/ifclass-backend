package com.ifclass.ifclass.aula.service;

import com.ifclass.ifclass.aula.model.Aula;
import com.ifclass.ifclass.aula.repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class AulaService {
    @Autowired
    private AulaRepository repository;

    public Aula salvar(Aula aula) {
        return repository.save(aula);
    }

    public List<Aula> buscarPorTurmaEData(Long turmaId, LocalDate data) {
        return repository.findByTurmaIdAndData(turmaId, data);
    }

    public List<Aula> buscarPorProfessorEData(Long professorId, LocalDate data) {
        return repository.findByProfessorIdAndData(professorId, data);
    }

    public List<Aula> listarTodas() {
        return repository.findAll();
    }

    public List<Aula> buscarPorProfessor(Long professorId) {
        return repository.findByProfessorId(professorId);
    }
} 