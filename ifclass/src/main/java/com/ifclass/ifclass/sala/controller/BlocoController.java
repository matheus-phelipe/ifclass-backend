package com.ifclass.ifclass.sala.controller;

import com.ifclass.ifclass.sala.model.Bloco;
import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.service.BlocoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/blocos")
public class BlocoController {

    @Autowired
    private BlocoService blocoService;

    @GetMapping
    public ResponseEntity<List<Bloco>> getAllBlocos() {
        List<Bloco> blocos = blocoService.findAll();
        return ResponseEntity.ok(blocos);
    }

    @PostMapping
    public ResponseEntity<Bloco> createBloco(@RequestBody Bloco bloco) {
        Bloco novoBloco = blocoService.createBloco(bloco);
        return new ResponseEntity<>(novoBloco, HttpStatus.CREATED);
    }

    @PostMapping("/{blocoId}/salas")
    public ResponseEntity<Bloco> addSalaToBloco(@PathVariable Long blocoId, @RequestBody Sala sala) {
        try {
            Bloco blocoAtualizado = blocoService.addSalaToBloco(blocoId, sala);
            return ResponseEntity.ok(blocoAtualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{blocoId}/salas/{salaId}")
    public ResponseEntity<Sala> updateSala(
            @PathVariable Long blocoId,
            @PathVariable Long salaId,
            @RequestBody Sala salaDetails) {
        try {
            Sala salaAtualizada = blocoService.updateSala(blocoId, salaId, salaDetails);
            return ResponseEntity.ok(salaAtualizada);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{blocoId}")
    public ResponseEntity<Void> deleteBloco(@PathVariable Long blocoId) {
        try {
            blocoService.deleteBloco(blocoId);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{blocoId}/salas/{salaId}")
    public ResponseEntity<Bloco> deleteSalaFromBloco(@PathVariable Long blocoId, @PathVariable Long salaId) {
        try {
            Bloco blocoAtualizado = blocoService.deleteSalaFromBloco(blocoId, salaId);
            return ResponseEntity.ok(blocoAtualizado);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
