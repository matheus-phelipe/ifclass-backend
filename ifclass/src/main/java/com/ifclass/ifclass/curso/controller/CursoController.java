package com.ifclass.ifclass.curso.controller;

import com.ifclass.ifclass.curso.model.Curso;
import com.ifclass.ifclass.curso.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*") // Para permitir chamadas do Angular
public class CursoController {
    @Autowired
    private CursoService service;

    @GetMapping
    public List<Curso> listar() {
        return service.listar();
    }

    @PostMapping
    public Curso salvar(@RequestBody Curso curso) {
        return service.salvar(curso);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}