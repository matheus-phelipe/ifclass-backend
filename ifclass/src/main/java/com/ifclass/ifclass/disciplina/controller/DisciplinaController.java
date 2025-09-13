package com.ifclass.ifclass.disciplina.controller;

import com.ifclass.ifclass.disciplina.model.Disciplina;
import com.ifclass.ifclass.disciplina.service.DisciplinaService;
import com.ifclass.ifclass.curso.model.Curso;
import com.ifclass.ifclass.curso.repository.CursoRepository;
import com.ifclass.ifclass.disciplina.model.DisciplinaDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/disciplinas")
@CrossOrigin(origins = "*")
public class DisciplinaController {

    @Autowired
    private DisciplinaService service;

    @Autowired
    private CursoRepository cursoRepository;

    @GetMapping
    public List<Disciplina> listar() {
        return service.listar();
    }

    @PostMapping
    public Disciplina salvar(@RequestBody DisciplinaDTO dto) {
        Curso curso = cursoRepository.findById(dto.getCursoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        Disciplina disciplina = new Disciplina();
        disciplina.setNome(dto.getNome());
        disciplina.setCodigo(dto.getCodigo());
        disciplina.setCargaHoraria(dto.getCargaHoraria());
        disciplina.setDepartamento(dto.getDepartamento());
        disciplina.setDescricao(dto.getDescricao());
        disciplina.setCurso(curso);
        return service.salvar(disciplina);
    }

    @DeleteMapping("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }

    @PutMapping("/{id}")
    public Disciplina atualizar(@PathVariable Long id, @RequestBody DisciplinaDTO dto) {
        Curso curso = cursoRepository.findById(dto.getCursoId())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Curso não encontrado"));
        Disciplina disciplina = new Disciplina();
        disciplina.setId(id);
        disciplina.setNome(dto.getNome());
        disciplina.setCodigo(dto.getCodigo());
        disciplina.setCargaHoraria(dto.getCargaHoraria());
        disciplina.setDepartamento(dto.getDepartamento());
        disciplina.setDescricao(dto.getDescricao());
        disciplina.setCurso(curso);
        return service.salvar(disciplina);
    }
}