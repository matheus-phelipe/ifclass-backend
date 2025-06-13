package com.ifclass.ifclass.disciplina.model;

import com.ifclass.ifclass.curso.model.Curso;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Data
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


    @ManyToOne
    @JoinColumn(name = "idcurso")
    private Curso curso;
    private Integer cargaHorario;
}