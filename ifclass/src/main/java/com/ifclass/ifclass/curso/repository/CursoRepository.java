package com.ifclass.ifclass.curso.repository;

import com.ifclass.ifclass.curso.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CursoRepository extends JpaRepository<Curso, Long> {
    // Busca um curso pelo nome (para verificar duplicidade na criação)
    Optional<Curso> findByNome(String nome);

    // Busca um curso pelo código (para verificar duplicidade na criação)
    Optional<Curso> findByCodigo(String codigo);

    // Busca um curso pelo nome, excluindo um ID específico (para verificar duplicidade na atualização)
    Optional<Curso> findByNomeAndIdNot(String nome, Long id);

    // Busca um curso pelo código, excluindo um ID específico (para verificar duplicidade na atualização)
    Optional<Curso> findByCodigoAndIdNot(String codigo, Long id);
}

