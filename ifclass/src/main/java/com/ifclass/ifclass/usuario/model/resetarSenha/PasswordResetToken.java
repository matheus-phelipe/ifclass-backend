package com.ifclass.ifclass.usuario.model.resetarSenha;

import com.ifclass.ifclass.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @OneToOne(targetEntity = Usuario.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id", unique = true) // Garante a unicidade de user_id
    private Usuario user;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Version
    private Long version; // Campo para controle de versão otimista

    // Construtor padrão
    public PasswordResetToken() {}

    // Construtor completo (se você usa) - inclua o 'version' se quiser inicializar, mas o JPA gerencia
    public PasswordResetToken(String token, Usuario user, LocalDateTime expiryDate) {
        this.token = token;
        this.user = user;
        this.expiryDate = expiryDate;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiryDate);
    }
}
