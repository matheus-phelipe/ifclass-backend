package com.ifclass.ifclass.usuario.service;

import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.model.dto.LoginDTO;
import com.ifclass.ifclass.usuario.model.dto.RoleUsuario;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Cacheable(value = "usuarios", key = "'all'")
    public List<Usuario> listar() {
        return repository.findAll();
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario cadastrar(Usuario usuario) {
        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        var encoder = new BCryptPasswordEncoder();
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        usuario.setAuthorities(Collections.singletonList(RoleUsuario.ROLE_ALUNO.toString()));

        repository.save(usuario);
        usuario.setSenha(null);

        return usuario;
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public void excluir(Long id) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));
        repository.delete(usuario);
    }

    @Cacheable(value = "usuarios", key = "#login.email")
    public Optional<Usuario> logar(LoginDTO login) {
        Optional<Usuario> usuarioOpt = repository.findByEmail(login.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            if (encoder.matches(login.getSenha(), usuario.getSenha())) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty(); // E-mail não existe ou senha incorreta
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario atualizarAuthorities(Long id,  List<String> authorities) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        usuario.setAuthorities(authorities);

        return repository.save(usuario);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        // Verificar se já existe outro usuário com o mesmo email
        repository.findByEmail(usuarioAtualizado.getEmail()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já está em uso");
            }
        });

        // Verificar se já existe outro usuário com o mesmo prontuário
        repository.findByProntuario(usuarioAtualizado.getProntuario()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Prontuário já está em uso");
            }
        });

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setProntuario(usuarioAtualizado.getProntuario());

        return repository.save(usuario);
    }
}
