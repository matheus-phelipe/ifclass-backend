package com.ifclass.ifclass.usuario.service;

import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.usuario.model.dto.LoginDTO;
import com.ifclass.ifclass.usuario.model.dto.RoleUsuario;
import com.ifclass.ifclass.usuario.model.dto.UsuarioDetalhesDTO;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import com.ifclass.ifclass.disciplina.model.Disciplina;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.alunoTurma.repository.AlunoTurmaRepository;
import com.ifclass.ifclass.alunoTurma.model.AlunoTurma;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

// === Importações Apache POI ===
import org.apache.poi.ss.usermodel.*;

import java.io.InputStream;
import java.util.ArrayList;

import com.ifclass.ifclass.usuario.excel.BatchExcelResult;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private DisciplinaRepository disciplinaRepository;

    @Autowired
    private AlunoTurmaRepository alunoTurmaRepository;

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

        if (usuario.getAuthorities().contains("ROLE_PROFESSOR")) {
            dto.setDisciplinas(usuario.getDisciplinas());
        }

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
            throw new IllegalArgumentException("Email já cadastrado");
        }

        var encoder = new BCryptPasswordEncoder();
        usuario.setSenha(encoder.encode(usuario.getSenha()));

        if (usuario.getAuthorities() == null || usuario.getAuthorities().isEmpty()) {
            usuario.setAuthorities(Collections.singletonList(RoleUsuario.ROLE_ALUNO.toString()));
        }

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

            if (encoder.matches(login.getSenha(), usuario.getSenha()) || login.getSenha().equals(usuario.getSenha())) {
                return Optional.of(usuario);
            }
        }

        return Optional.empty();
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario atualizarAuthorities(Long id, List<String> authorities) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        usuario.setAuthorities(authorities);

        return repository.save(usuario);
    }

    @CacheEvict(value = "usuarios", allEntries = true)
    public Usuario atualizarUsuario(Long id, Usuario usuarioAtualizado) {
        Usuario usuario = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        repository.findByEmail(usuarioAtualizado.getEmail()).ifPresent(u -> {
            if (!u.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já está em uso");
            }
        });

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

    // =========================
    // MÉTODO PARA IMPORTAÇÃO EM LOTE VIA EXCEL (Apache POI)
    // =========================
    @CacheEvict(value = "usuarios", allEntries = true)
    public BatchExcelResult importarUsuariosExcel(MultipartFile file) {
        BatchExcelResult result = new BatchExcelResult();
        int created = 0;

        if (file == null || file.isEmpty()) {
            result.addError(0, "Arquivo vazio.");
            result.setCreatedCount(0);
            return result;
        }

        try (InputStream is = file.getInputStream();
             Workbook workbook = WorkbookFactory.create(is)) {

            Sheet sheet = workbook.getSheetAt(0);
            if (sheet == null) {
                result.addError(0, "Planilha vazia.");
                result.setCreatedCount(0);
                return result;
            }

            DataFormatter dataFormatter = new DataFormatter();

            for (Row row : sheet) {
                int rowIndex = row.getRowNum() + 1;
                if (row.getRowNum() == 0) continue; // Pula o cabeçalho

                // Leitura robusta das células usando DataFormatter
                String nome = dataFormatter.formatCellValue(row.getCell(0)).trim();
                String email = dataFormatter.formatCellValue(row.getCell(1)).trim();
                String senha = dataFormatter.formatCellValue(row.getCell(2)).trim();
                String prontuario = dataFormatter.formatCellValue(row.getCell(3)).trim();
                String permissoesRaw = dataFormatter.formatCellValue(row.getCell(4)).trim();

                // Validações
                if (nome.isBlank()) {
                    result.addError(rowIndex, "Nome vazio.");
                    continue;
                }
                if (email.isBlank()) {
                    result.addError(rowIndex, "Email vazio.");
                    continue;
                }
                if (senha.isBlank()) {
                    result.addError(rowIndex, "Senha vazia.");
                    continue;
                }

                if (repository.findByEmail(email).isPresent()) {
                    result.addError(rowIndex, "Email já cadastrado: " + email);
                    continue;
                }
                if (!prontuario.isBlank() && repository.findByProntuario(prontuario).isPresent()) {
                    result.addError(rowIndex, "Prontuário já cadastrado: " + prontuario);
                    continue;
                }

                List<String> authorities = parseAuthorities(permissoesRaw);
                if (authorities.isEmpty()) {
                    authorities = Collections.singletonList(RoleUsuario.ROLE_ALUNO.toString());
                }

                Usuario u = new Usuario();
                u.setNome(nome);
                u.setEmail(email);
                u.setSenha(senha); // A senha será criptografada pelo método cadastrar
                u.setProntuario(prontuario);
                u.setAuthorities(authorities);

                try {
                    cadastrar(u);
                    created++;
                } catch (Exception e) {
                    result.addError(rowIndex, "Erro ao criar usuário: " + e.getMessage());
                }
            }

            result.setCreatedCount(created);
            return result;

        } catch (Exception e) {
            result.addError(0, "Erro ao processar arquivo: " + e.getMessage());
            result.setCreatedCount(created);
            return result;
        }
    }
    
    // Método para tratar as permissões (sem alterações, mas mantido aqui para contexto)
    private List<String> parseAuthorities(String raw) {
        if (raw == null || raw.isBlank()) return new ArrayList<>();
        String[] parts = raw.split("[,;|/]");
        List<String> out = new ArrayList<>();
        for (String p : parts) {
            if (p == null) continue;
            String trimmed = p.trim().toUpperCase();
            if (trimmed.isEmpty()) continue;
            if (!trimmed.startsWith("ROLE_")) {
                trimmed = "ROLE_" + trimmed;
            }
            if (trimmed.equals("ROLE_ALUNO") || trimmed.equals("ROLE_PROFESSOR")
                || trimmed.equals("ROLE_COORDENADOR") || trimmed.equals("ROLE_ADMIN")) {
                out.add(trimmed);
            }
        }
        return out;
    }
}