package com.ifclass.ifclass.aviso.controller;

import com.ifclass.ifclass.aviso.model.Aviso;
import com.ifclass.ifclass.aviso.service.AvisoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/avisos")
@CrossOrigin(origins = "http://localhost:4200")
public class AvisoController {

    @Autowired
    private AvisoService avisoService;

    @GetMapping
    public ResponseEntity<List<Aviso>> listarAvisos() {
        return ResponseEntity.ok(avisoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Aviso> buscarAvisoPorId(@PathVariable Long id) {
        return avisoService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Aviso> criarAviso(@Valid @RequestBody Aviso aviso) {
        Aviso novoAviso = avisoService.criar(aviso);
        return ResponseEntity
                .created(URI.create("/api/avisos/" + novoAviso.getId()))
                .body(novoAviso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Aviso> atualizarAviso(@PathVariable Long id, @Valid @RequestBody Aviso aviso) {
        return avisoService.atualizar(id, aviso)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletarAviso(@PathVariable Long id) {
        return avisoService.deletar(id)
                ? ResponseEntity.ok().build()
                : ResponseEntity.notFound().build();
    }
}
