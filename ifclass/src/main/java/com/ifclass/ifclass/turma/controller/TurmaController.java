package com.ifclass.ifclass.turma.controller;

import com.ifclass.ifclass.turma.model.Turma;
import com.ifclass.ifclass.turma.service.TurmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
public class TurmaController {

    @Autowired
    private TurmaService service;

    @GetMapping
    public List<Turma> listar() {
        return service.listar();
    }

    @PostMapping
    public Turma salvar(@RequestBody Turma turma) {
        return service.salvar(turma);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}