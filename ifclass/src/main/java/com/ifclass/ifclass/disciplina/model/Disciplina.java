package com.ifclass.ifclass.disciplina.model;

import com.ifclass.ifclass.curso.model.Curso;
import com.ifclass.ifclass.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Date;
import java.util.Set;

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

    @ManyToMany(mappedBy = "disciplinas")
    @JsonIgnore
    private Set<Usuario> professores;
}