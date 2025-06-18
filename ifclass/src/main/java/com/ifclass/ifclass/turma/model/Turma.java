package com.ifclass.ifclass.turma.model;

import com.ifclass.ifclass.curso.model.Curso;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Turma {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "idcurso")
    private Curso curso;
    private Integer ano;
    private Long semestre;
}