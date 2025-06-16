package com.ifclass.ifclass.usuario.service;

import com.ifclass.ifclass.common.exception.ResourceNotFoundException;
import com.ifclass.ifclass.common.service.EmailService;
import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.model.resetarSenha.PasswordResetToken;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import com.ifclass.ifclass.usuario.repository.resetarSenha.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class PasswordResetService {

    @Autowired
    private UsuarioRepository usuarioRepository; // Seu repositório de usuários
    @Autowired
    private PasswordResetTokenRepository tokenRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PasswordEncoder passwordEncoder; // Injete seu PasswordEncoder (ex: BCryptPasswordEncoder)

    @Transactional // Garante que a transação abranja todas as operações
    public void createPasswordResetTokenForUser(String email) {
        Usuario user = usuarioRepository.findByEmail(email).orElse(null);

        if (user != null) {
            // Tenta encontrar um token existente para o usuário
            Optional<PasswordResetToken> existingToken = tokenRepository.findByUser(user);

            if (existingToken.isPresent()) {
                // Se um token existir, atualize-o em vez de deletar e inserir
                PasswordResetToken tokenToUpdate = existingToken.get();
                tokenToUpdate.setToken(UUID.randomUUID().toString());
                tokenToUpdate.setExpiryDate(LocalDateTime.now().plusHours(1)); // Válido por 1 hora
                tokenRepository.save(tokenToUpdate); // Salva a atualização
            } else {
                // Se não existir, crie um novo token
                String token = UUID.randomUUID().toString();
                LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
                PasswordResetToken resetToken = new PasswordResetToken(token, user, expiryDate);
                tokenRepository.save(resetToken);
            }

            // O código abaixo para envio de e-mail pode ser reutilizado
            String currentToken = existingToken.isPresent() ? existingToken.get().getToken() : UUID.randomUUID().toString(); // Obtenha o token correto, ou gere um temporário se for novo
            if(existingToken.isPresent()){
                currentToken = existingToken.get().getToken();
            } else {
                // Isso é para o caso de um novo token, mas o save acima já cuida disso
                // Você pode precisar refatorar um pouco para pegar o token salvo
                currentToken = tokenRepository.findByUser(user).get().getToken(); // Garante que você pegue o token recém-salvo
            }


            // Melhoria: Recuperar o token atualizado/criado para o email
            PasswordResetToken latestToken = tokenRepository.findByUser(user)
                    .orElseThrow(() -> new RuntimeException("Token não encontrado após criação/atualização."));
            String resetLink = "http://localhost:4200/resetar-senha?token=" + latestToken.getToken(); // URL do seu front-end
            String emailBody = "Olá " + user.getNome() + ",\n\n"
                    + "Você solicitou a redefinição de senha para sua conta IFClass. "
                    + "Por favor, clique no link abaixo para redefinir sua senha:\n"
                    + resetLink + "\n\n"
                    + "Este link expirará em 1 hora.\n\n"
                    + "Se você não solicitou esta redefinição, por favor, ignore este e-mail.\n\n"
                    + "Atenciosamente,\nEquipe IFClass";

            emailService.sendEmail(user.getEmail(), "Redefinição de Senha IFClass", emailBody);
        }
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new ResourceNotFoundException("Token de redefinição de senha inválido."));

        if (resetToken.isExpired()) {
            tokenRepository.delete(resetToken); // Limpa o token expirado
            throw new ResourceNotFoundException("Token de redefinição de senha expirado.");
        }

        Usuario user = resetToken.getUser();
        user.setSenha(passwordEncoder.encode(newPassword)); // Criptografa a nova senha
        usuarioRepository.save(user);

        tokenRepository.delete(resetToken); // Token usado, então exclua-o
    }
}
