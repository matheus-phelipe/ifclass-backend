package com.ifclass.ifclass.alunoTurma.model;

import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.turma.model.Turma;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class AlunoTurma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Usuario aluno;

    @ManyToOne(optional = false)
    private Turma turma;

    private LocalDate dataMatricula;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Usuario getAluno() { return aluno; }
    public void setAluno(Usuario aluno) { this.aluno = aluno; }

    public Turma getTurma() { return turma; }
    public void setTurma(Turma turma) { this.turma = turma; }

    public LocalDate getDataMatricula() { return dataMatricula; }
    public void setDataMatricula(LocalDate dataMatricula) { this.dataMatricula = dataMatricula; }
} 