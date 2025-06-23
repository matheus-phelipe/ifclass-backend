package com.ifclass.ifclass.usuario.model.dto;

import com.ifclass.ifclass.disciplina.model.Disciplina;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioDetalhesDTO {
    private Long id;
    private String nome;
    private String email;
    private String prontuario;
    private List<String> authorities;
    
    // Para professores
    private Set<Disciplina> disciplinas;
    
    // Para alunos
    private TurmaResumoDTO turma;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TurmaResumoDTO {
        private Long id;
        private Integer ano;
        private Long semestre;
        private CursoResumoDTO curso;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CursoResumoDTO {
        private Long id;
        private String nome;
        private String codigo;
    }
}
