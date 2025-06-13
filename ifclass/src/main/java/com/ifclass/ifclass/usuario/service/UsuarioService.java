package com.ifclass.ifclass.usuario.service;

import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.model.dto.LoginDTO;
import com.ifclass.ifclass.usuario.model.dto.RoleUsuario;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    public List<Usuario> listar() {
        return repository.findAll();
    }

    public Usuario cadastrar(Usuario usuario) {
        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }

        var encoder = new BCryptPasswordEncoder();
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        usuario.setPermissao(String.valueOf(RoleUsuario.ROLE_ALUNO));

        repository.save(usuario);
        usuario.setSenha(null);

        return usuario;
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }

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
}
