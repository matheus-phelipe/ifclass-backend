package com.ifclass.ifclass.usuario.repository;

import com.ifclass.ifclass.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {}

