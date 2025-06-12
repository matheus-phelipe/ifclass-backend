package com.ifclass.ifclass.sala.controller;

import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.service.SalaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salas")
@CrossOrigin(origins = "*")
public class SalaController {

    @Autowired
    private SalaService service;

    @GetMapping
    public List<Sala> listar() {
        return service.listar();
    }

    @PostMapping
    public Sala salvar(@RequestBody Sala sala) {
        return service.salvar(sala);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}