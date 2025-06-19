package com.ifclass.ifclass.alunoTurma.controller;

import com.ifclass.ifclass.alunoTurma.service.AlunoTurmaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aluno-turma")
public class AlunoTurmaController {
    @Autowired
    private AlunoTurmaService alunoTurmaService;

    @PostMapping("/{alunoId}/{turmaId}")
    public ResponseEntity<?> vincularAlunoTurma(@PathVariable Long alunoId, @PathVariable Long turmaId) {
        alunoTurmaService.adicionarAlunoNaTurma(alunoId, turmaId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lote/{turmaId}")
    public ResponseEntity<?> vincularAlunosEmLote(@PathVariable Long turmaId, @RequestBody java.util.List<Long> alunosIds) {
        alunoTurmaService.adicionarAlunosNaTurmaEmLote(alunosIds, turmaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<java.util.List<com.ifclass.ifclass.usuario.model.Usuario>> listarAlunosPorTurma(@PathVariable Long turmaId) {
        java.util.List<com.ifclass.ifclass.usuario.model.Usuario> alunos = alunoTurmaService.listarAlunosPorTurma(turmaId);
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/alunos-vinculados")
    public ResponseEntity<java.util.List<Long>> listarIdsAlunosVinculados() {
        return ResponseEntity.ok(alunoTurmaService.listarIdsAlunosVinculados());
    }

    @DeleteMapping("/{alunoId}")
    public ResponseEntity<?> desvincularAlunoDaTurma(@PathVariable Long alunoId) {
        alunoTurmaService.desvincularAlunoDaTurma(alunoId);
        return ResponseEntity.ok().build();
    }
} 