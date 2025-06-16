package com.ifclass.ifclass.curso.controller;

import com.ifclass.ifclass.curso.model.Curso;
import com.ifclass.ifclass.curso.service.CursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/cursos")
@CrossOrigin(origins = "*")
public class CursoController {
    @Autowired
    private CursoService service;

    @GetMapping
    public ResponseEntity<List<Curso>> listar() {
        List<Curso> cursos = service.listar();
        return ResponseEntity.ok(cursos);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED) // Retorna 201 Created para sucesso
    public Curso salvar(@RequestBody Curso curso) {
        // A exceção ResourceConflictException (se houver duplicidade) será lançada aqui
        // e capturada pelo GlobalExceptionHandler.
        return service.salvar(curso);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Curso> atualizar(@PathVariable Long id, @RequestBody Curso curso) {
        // As exceções ResourceNotFoundException ou ResourceConflictException
        // serão lançadas aqui e capturadas pelo GlobalExceptionHandler.
        Curso cursoAtualizado = service.atualizar(id, curso);
        return ResponseEntity.ok(cursoAtualizado); // Retorna 200 OK com o curso atualizado
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // Retorna 204 No Content para deleção bem-sucedida
    public void excluir(@PathVariable Long id) {
        // A exceção ResourceNotFoundException (se não encontrar) será lançada aqui
        // e capturada pelo GlobalExceptionHandler.
        service.excluir(id);
    }
}