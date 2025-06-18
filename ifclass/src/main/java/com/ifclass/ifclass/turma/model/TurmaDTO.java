package com.ifclass.ifclass.turma.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TurmaDTO {
    private Long cursoId;
    private Integer ano;
    private Long semestre;
} 