package com.ifclass.ifclass.curso.repository;

import com.ifclass.ifclass.curso.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CursoRepository extends JpaRepository<Curso, Long> {}

