package com.ifclass.ifclass.turma.controller;

import com.ifclass.ifclass.turma.model.Turma;
import com.ifclass.ifclass.turma.model.TurmaDTO;
import com.ifclass.ifclass.turma.service.TurmaService;
import com.ifclass.ifclass.curso.model.Curso;
import com.ifclass.ifclass.curso.repository.CursoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
public class TurmaController {

    @Autowired
    private TurmaService service;

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public ResponseEntity<List<Turma>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @PostMapping
    public ResponseEntity<Turma> salvar(@RequestBody TurmaDTO turmaDTO) {
        Curso curso = cursoRepository.findById(turmaDTO.getCursoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        Turma turma = new Turma();
        turma.setCurso(curso);
        turma.setAno(turmaDTO.getAno());
        turma.setSemestre(turmaDTO.getSemestre());
        return ResponseEntity.ok(service.salvar(turma));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Turma> atualizar(@PathVariable Long id, @RequestBody TurmaDTO turmaDTO) {
        Curso curso = cursoRepository.findById(turmaDTO.getCursoId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        Turma turma = new Turma();
        turma.setId(id);
        turma.setCurso(curso);
        turma.setAno(turmaDTO.getAno());
        turma.setSemestre(turmaDTO.getSemestre());
        return ResponseEntity.ok(service.atualizar(turma));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}