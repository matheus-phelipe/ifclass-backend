package com.ifclass.ifclass.usuario.controller;

import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.model.dto.LoginDTO;
import com.ifclass.ifclass.usuario.model.dto.UsuarioDetalhesDTO;
import com.ifclass.ifclass.usuario.service.PasswordResetService;
import com.ifclass.ifclass.usuario.service.UsuarioService;
import com.ifclass.ifclass.util.JwtUtil;
import com.ifclass.ifclass.util.security.InputValidator;
import com.ifclass.ifclass.util.security.SecurityLogger;
import com.ifclass.ifclass.disciplina.model.Disciplina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.servlet.http.HttpServletRequest;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private InputValidator inputValidator;

    @Autowired
    private SecurityLogger securityLogger;

    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
    }

    @GetMapping("/detalhes")
    public List<UsuarioDetalhesDTO> listarComDetalhes() {
        return service.listarComDetalhes();
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody Usuario usuario) {
        try {
            Usuario novoUsuario = service.cadastrar(usuario);
            return ResponseEntity.status(201).body(novoUsuario);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(409).body(e.getMessage()); // 409 Conflict
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro interno");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO auth, HttpServletRequest request) {
        String clientIP = securityLogger.getClientIP(request);

        // Validações de entrada
        if (auth.getEmail() == null || auth.getSenha() == null) {
            securityLogger.logLoginAttempt("null", clientIP, false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email e senha são obrigatórios");
        }

        // Validar formato do email
        if (!inputValidator.isValidEmail(auth.getEmail())) {
            securityLogger.logLoginAttempt(auth.getEmail(), clientIP, false);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Formato de email inválido");
        }

        // Verificar tentativas de XSS ou SQL Injection
        if (!inputValidator.isSafeInput(auth.getEmail()) || !inputValidator.isSafeInput(auth.getSenha())) {
            securityLogger.logXSSAttempt(auth.getEmail(), clientIP, "/api/usuarios/login");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Entrada inválida detectada");
        }

        Optional<Usuario> usuario = service.logar(auth);
        if (usuario.isEmpty()) {
            securityLogger.logLoginAttempt(auth.getEmail(), clientIP, false);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        // Login bem-sucedido
        securityLogger.logLoginAttempt(auth.getEmail(), clientIP, true);
        String token = jwtUtil.generateToken(usuario.get().getId(), usuario.get().getEmail(), usuario.get().getAuthorities());
        return ResponseEntity.ok().body(Map.of("token", token));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizarUsuario(@PathVariable Long id, @RequestBody Usuario usuarioAtualizado) {
        try {
            Usuario atualizado = service.atualizarUsuario(id, usuarioAtualizado);
            return ResponseEntity.ok(atualizado);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        try {
            service.excluir(id);
            return ResponseEntity.noContent().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).build();
        }
    }

    @PatchMapping("/{id}/authorities")
    public ResponseEntity<Usuario> atualizarAuthorities(
            @PathVariable Long id,
            @RequestBody Map<String, List<String>> request
    ) {
        try {
            List<String> novasAuthorities = request.get("authorities");
            Usuario atualizado = service.atualizarAuthorities(id, novasAuthorities);
            return ResponseEntity.ok(atualizado);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }

    // Endpoint para solicitar o link de redefinição
    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        passwordResetService.createPasswordResetTokenForUser(email);
        // Sempre retorne OK, mesmo se o email não existir, por segurança.
        return ResponseEntity.ok().build();
    }

    // Endpoint para redefinir a senha
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");

        // O serviço lançará exceções se o token for inválido/expirado
        passwordResetService.resetPassword(token, newPassword);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{professorId}/disciplinas/{disciplinaId}")
    public ResponseEntity<?> vincularDisciplina(@PathVariable Long professorId, @PathVariable Long disciplinaId) {
        service.vincularDisciplina(professorId, disciplinaId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{professorId}/disciplinas/{disciplinaId}")
    public ResponseEntity<?> desvincularDisciplina(@PathVariable Long professorId, @PathVariable Long disciplinaId) {
        service.desvincularDisciplina(professorId, disciplinaId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{professorId}/disciplinas")
    public ResponseEntity<Set<Disciplina>> listarDisciplinas(@PathVariable Long professorId) {
        return ResponseEntity.ok(service.listarDisciplinas(professorId));
    }
}