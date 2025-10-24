package com.ifclass.ifclass.usuario.repository;

import com.ifclass.ifclass.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByProntuario(String prontuario);

    List<Usuario> findAllByAuthoritiesNotContaining(String authority);

    // Métodos para coordenação
    Long countByAuthoritiesContaining(String authority);
    List<Usuario> findByAuthoritiesContaining(String authority);

    // Métodos para limpeza de duplicados
    @Query("SELECT u.prontuario, COUNT(u) FROM Usuario u GROUP BY u.prontuario HAVING COUNT(u) > 1")
    List<Object[]> findProntuariosDuplicados();

    @Query("SELECT u FROM Usuario u WHERE u.prontuario = :prontuario")
    List<Usuario> findUsuariosByProntuario(String prontuario);
}

