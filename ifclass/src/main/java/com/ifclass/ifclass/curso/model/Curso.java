package com.ifclass.ifclass.curso.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity // Mapeia esta classe para uma tabela de banco de dados
public class Curso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Gera ID automaticamente, bom para PostgreSQL
    private Long id;
    private String nome;
    private String codigo;
    private Integer cargaHoraria;
    private String departamento;
    private String descricao;
}