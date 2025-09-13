package com.ifclass.ifclass.disciplina.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisciplinaDTO {
    private Long cursoId;
    private String nome;
    private String codigo;
    private Integer cargaHoraria;
    private String departamento;
    private String descricao;
} 