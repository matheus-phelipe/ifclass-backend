package com.ifclass.ifclass.aula.controller;

import com.ifclass.ifclass.aula.model.Aula;
import com.ifclass.ifclass.aula.service.AulaService;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/aulas")
@CrossOrigin(origins = "*")
public class AulaController {
    @Autowired
    private AulaService aulaService;
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private DisciplinaRepository disciplinaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;

    @PostMapping
    public ResponseEntity<Aula> criarAula(@RequestBody Aula aula) {
        Aula novaAula = aulaService.salvar(aula);
        return ResponseEntity.ok(novaAula);
    }

    @GetMapping("/turma/{turmaId}/data/{data}")
    public List<Aula> buscarPorTurmaEData(@PathVariable Long turmaId, @PathVariable String data) {
        return aulaService.buscarPorTurmaEData(turmaId, LocalDate.parse(data));
    }

    @GetMapping("/professor/{professorId}/data/{data}")
    public List<Aula> buscarPorProfessorEData(@PathVariable Long professorId, @PathVariable String data) {
        return aulaService.buscarPorProfessorEData(professorId, LocalDate.parse(data));
    }

    @GetMapping("/professor/{professorId}")
    public List<Aula> buscarPorProfessor(@PathVariable Long professorId) {
        return aulaService.buscarPorProfessor(professorId);
    }

    @GetMapping
    public List<Aula> listarTodas() {
        return aulaService.listarTodas();
    }

    @GetMapping("/professor/{professorId}/proxima")
    public ResponseEntity<Aula> buscarProximaAula(@PathVariable Long professorId) {
        return aulaService.buscarProximaAula(professorId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/hoje")
    public List<Aula> buscarAulasDeHoje() {
        return aulaService.buscarAulasDeHoje();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removerAula(@PathVariable Long id) {
        try {
            aulaService.remover(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 