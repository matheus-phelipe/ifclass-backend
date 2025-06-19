package com.ifclass.ifclass.disciplina.model;

import com.ifclass.ifclass.curso.model.Curso;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String codigo;
    private String departamento;
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "idcurso")
    private Curso curso;
    private Integer cargaHoraria;
}