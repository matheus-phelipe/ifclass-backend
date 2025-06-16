package com.ifclass.ifclass.usuario.repository.resetarSenha;

import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.model.resetarSenha.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByUser(Usuario user); // Para limpar tokens antigos

    Optional<PasswordResetToken> findByUser(Usuario user);
}
