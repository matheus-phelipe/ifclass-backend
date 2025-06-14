package com.ifclass.ifclass.usuario.controller;

import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.model.dto.LoginDTO;
import com.ifclass.ifclass.usuario.service.UsuarioService;
import com.ifclass.ifclass.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin(origins = "*")
public class UsuarioController {

    @Autowired
    private UsuarioService service;

    @GetMapping
    public List<Usuario> listar() {
        return service.listar();
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
    public ResponseEntity<?> login(@RequestBody LoginDTO auth) {
        Optional<Usuario> usuario = service.logar(auth);
        if (usuario.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }

        String token = JwtUtil.generateToken(usuario.get().getEmail(), usuario.get().getAuthorities());
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

    @PatchMapping("/{id}/authority")
    public ResponseEntity<Usuario> atualizarAuthority(
            @PathVariable Long id,
            @RequestBody Map<String, String> request
    ) {
        try {
            String novaAuthority = request.get("authorities");
            Usuario atualizado = service.atualizarAuthority(id, novaAuthority);
            return ResponseEntity.ok(atualizado);
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(null);
        }
    }
}