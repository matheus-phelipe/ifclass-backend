package com.ifclass.ifclass.curso.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Curso {
    @Id
    @GeneratedValue
    private Long id;
    private String nome;
}