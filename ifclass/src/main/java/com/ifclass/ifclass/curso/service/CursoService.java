package com.ifclass.ifclass.curso.service;

import com.ifclass.ifclass.curso.model.Curso;
import com.ifclass.ifclass.curso.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CursoService {
    @Autowired
    private CursoRepository repo;

    public List<Curso> listar() {
        return repo.findAll();
    }

    public Curso salvar(Curso curso) {
        return repo.save(curso);
    }

    public void excluir(Long id) {
        repo.deleteById(id);
    }
}
