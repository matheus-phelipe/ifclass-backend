package com.ifclass.ifclass.usuario.service;

import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.model.dto.LoginDTO;
import com.ifclass.ifclass.usuario.model.dto.RoleUsuario;
import com.ifclass.ifclass.usuario.model.dto.UsuarioDetalhesDTO;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import com.ifclass.ifclass.util.log.AppLogger;
import com.ifclass.ifclass.disciplina.model.Disciplina;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.alunoTurma.repository.AlunoTurmaRepository;
import com.ifclass.ifclass.alunoTurma.model.AlunoTurma;
import com.ifclass.ifclass.admin.service.ConfiguracaoAplicacaoService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioService {

    @Autowired
    private AppLogger appLogger;

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private AlunoTurmaRepository alunoTurmaRepository;
    
    @Autowired
    private ConfiguracaoAplicacaoService configuracaoService;

    @Cacheable(value = "usuarios", key = "'all'")
    public List<Usuario> listar() {
        return repository.findAllByAuthoritiesNotContaining("ROLE_ADMIN");
    }

    public List<Usuario> listarProfessores() {
        return repository.findByAuthoritiesContaining("ROLE_PROFESSOR");
    }

    @Cacheable(value = "usuarios", key = "'detalhes'")
    public List<UsuarioDetalhesDTO> listarComDetalhes() {
        List<Usuario> usuarios = repository.findAllByAuthoritiesNotContaining("ROLE_ADMIN");
        return usuarios.stream().map(this::converterParaDetalhesDTO).collect(Collectors.toList());
    }

    private UsuarioDetalhesDTO converterParaDetalhesDTO(Usuario usuario) {
        UsuarioDetalhesDTO dto = new UsuarioDetalhesDTO();
        dto.setId(usuario.getId());
        dto.setNome(usuario.getNome());
        dto.setEmail(usuario.getEmail());
        dto.setProntuario(usuario.getProntuario());
        dto.setAuthorities(usuario.getAuthorities());

        // Se for professor, buscar disciplinas
        if (usuario.getAuthorities().contains("ROLE_PROFESSOR")) {
            dto.setDisciplinas(usuario.getDisciplinas());
        }

        // Se for aluno, buscar turma
        if (usuario.getAuthorities().contains("ROLE_ALUNO")) {
            List<AlunoTurma> vinculos = alunoTurmaRepository.findByAluno(usuario);
            if (!vinculos.isEmpty()) {
                AlunoTurma vinculo = vinculos.get(0);
                UsuarioDetalhesDTO.TurmaResumoDTO turmaDTO = new UsuarioDetalhesDTO.TurmaResumoDTO();
                turmaDTO.setId(vinculo.getTurma().getId());
                turmaDTO.setAno(vinculo.getTurma().getAno());
                turmaDTO.setSemestre(vinculo.getTurma().getSemestre());

                if (vinculo.getTurma().getCurso() != null) {
                    UsuarioDetalhesDTO.CursoResumoDTO cursoDTO = new UsuarioDetalhesDTO.CursoResumoDTO();
                    cursoDTO.setId(vinculo.getTurma().getCurso().getId());
                    cursoDTO.setNome(vinculo.getTurma().getCurso().getNome());
                    cursoDTO.setCodigo(vinculo.getTurma().getCurso().getCodigo());
                    turmaDTO.setCurso(cursoDTO);
                }

                dto.setTurma(turmaDTO);
            }
        }

        return dto;
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario cadastrar(Usuario usuario) {
        if (repository.findByEmail(usuario.getEmail()).isPresent()) {
            appLogger.logCrudWarning("Usuario", "CRIACAO", "Tentativa de cadastrar com email já existente: " + usuario.getEmail());
            throw new IllegalArgumentException("Email já cadastrado");
        }

        // Verificar se já existe usuário com o mesmo prontuário
        try {
            if (repository.findByProntuario(usuario.getProntuario()).isPresent()) {
                appLogger.logCrudWarning("Usuario", "CRIACAO", "Tentativa de cadastrar com prontuário já existente: " + usuario.getProntuario());
                throw new IllegalArgumentException("Prontuário já cadastrado");
            }
        } catch (Exception e) {
            // Se houver múltiplos resultados, também é considerado duplicado
            appLogger.logCrudWarning("Usuario", "CRIACAO", "Múltiplos usuários com prontuário: " + usuario.getProntuario());
            throw new IllegalArgumentException("Prontuário já cadastrado");
        }

        var encoder = new BCryptPasswordEncoder();
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        // Se o front não mandar authorities, define padrão ROLE_ALUNO
        if (usuario.getAuthorities() == null || usuario.getAuthorities().isEmpty()) {
            usuario.setAuthorities(Collections.singletonList(RoleUsuario.ROLE_ALUNO.toString()));
        }

        Usuario usuarioSalvo = repository.save(usuario);
        appLogger.logCrudSuccess("Usuario", "CRIACAO", "ID: " + usuarioSalvo.getId() + ", Email: " + usuarioSalvo.getEmail());

        usuario.setSenha(null);

        return usuario;
    }


    @CacheEvict(value = "usuarios", allEntries = true)
    public void excluir(Long id) {
        Optional<Usuario> usuarioOpt = repository.findById(id);

        if (usuarioOpt.isEmpty()) {
            appLogger.logCrudWarning("Usuario", "DELECAO", "Tentativa de excluir usuário não encontrado com ID: " + id);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado");
        }

        repository.delete(usuarioOpt.get());
        
        appLogger.logCrudSuccess("Usuario", "DELECAO", "ID: " + id);
    }

    public Optional<Usuario> logar(LoginDTO login) {
        Optional<Usuario> usuarioOpt = repository.findByEmail(login.getEmail());

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            // Validar senha usando configurações do sistema
            if (!configuracaoService.validarSenha(login.getSenha())) {
                Integer tamanhoMinimo = configuracaoService.getTamanhoMinimoSenha();
                if (tamanhoMinimo == null) tamanhoMinimo = 6; // valor padrão se configuração não existir
                
                appLogger.logServiceError("UsuarioService", "logar", 
                    "Tentativa de login com senha inválida para usuário: " + login.getEmail());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, 
                    "Senha deve ter pelo menos " + tamanhoMinimo + " caracteres");
            }

            if (encoder.matches(login.getSenha(), usuario.getSenha())) {
                // Log de debug se habilitado
                if (configuracaoService.isModoDebug()) {
                    appLogger.logCrudSuccess("Usuario", "LOGIN", 
                        "Login realizado com sucesso: " + login.getEmail() + 
                        " | Configurações: " + configuracaoService.getInformacoesDebug());
                }
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
        try {
            repository.findByProntuario(usuarioAtualizado.getProntuario()).ifPresent(u -> {
                if (!u.getId().equals(id)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Prontuário já está em uso");
                }
            });
        } catch (Exception e) {
            // Se houver múltiplos resultados, também é considerado duplicado
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prontuário já está em uso");
        }

        usuario.setNome(usuarioAtualizado.getNome());
        usuario.setEmail(usuarioAtualizado.getEmail());
        usuario.setProntuario(usuarioAtualizado.getProntuario());

        return repository.save(usuario);
    }

    @Transactional
    public void limparProntuariosDuplicados() {
        // Buscar prontuários duplicados
        List<Object[]> duplicados = repository.findProntuariosDuplicados();
        
        for (Object[] duplicado : duplicados) {
            String prontuario = (String) duplicado[0];
            Long count = (Long) duplicado[1];
            
            if (count > 1) {
                // Buscar todos os usuários com este prontuário
                List<Usuario> usuariosComProntuario = repository.findUsuariosByProntuario(prontuario);
                
                // Manter apenas o mais recente (maior ID) e remover os outros
                usuariosComProntuario.sort((u1, u2) -> Long.compare(u2.getId(), u1.getId()));
                
                // Remover todos exceto o primeiro (mais recente)
                for (int i = 1; i < usuariosComProntuario.size(); i++) {
                    Usuario usuarioParaRemover = usuariosComProntuario.get(i);
                    repository.delete(usuarioParaRemover);
                    appLogger.logCrudSuccess("Usuario", "LIMPEZA", "Usuário duplicado removido: ID " + usuarioParaRemover.getId() + ", Prontuário: " + prontuario);
                }
            }
        }
    }

    public void vincularDisciplina(Long professorId, Long disciplinaId) {
        Usuario professor = repository.findById(professorId)
            .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
            .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));
        professor.getDisciplinas().add(disciplina);
        repository.save(professor);
    }

    public void desvincularDisciplina(Long professorId, Long disciplinaId) {
        Usuario professor = repository.findById(professorId)
            .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
            .orElseThrow(() -> new EntityNotFoundException("Disciplina não encontrada"));
        professor.getDisciplinas().remove(disciplina);
        repository.save(professor);
    }

    public Set<Disciplina> listarDisciplinas(Long professorId) {
        Usuario professor = repository.findById(professorId)
            .orElseThrow(() -> new EntityNotFoundException("Professor não encontrado"));
        return professor.getDisciplinas();
    }
}
