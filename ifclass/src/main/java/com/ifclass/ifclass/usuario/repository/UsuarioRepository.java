package com.ifclass.ifclass.usuario.repository;

import com.ifclass.ifclass.usuario.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByProntuario(String prontuario);

    @Query(
            value = "SELECT * FROM usuario u " +
                    "WHERE u.id NOT IN (" +
                    "  SELECT ra.usuario_id FROM usuario_authorities ra WHERE ra.authority = 'ROLE_ADMIN'" +
                    ")",
            nativeQuery = true
    )
    List<Usuario> findByUsuarioExceptAdmin();



    @Query(
            value = "SELECT * FROM usuario u " +
                    "WHERE u.id NOT IN ( " +
                    "  SELECT ra.usuario_id FROM usuario_authorities ra " +
                    "  WHERE ra.authority IN ('ROLE_ADMIN', 'ROLE_COORDENADOR') " +
                    ")",
            nativeQuery = true
    )
    List<Usuario> findByUsuarioExceptAdminCoor();

}

