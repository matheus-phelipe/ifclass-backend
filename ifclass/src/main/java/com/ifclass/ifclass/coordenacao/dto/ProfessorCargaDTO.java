package com.ifclass.ifclass.coordenacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorCargaDTO {
    private Long id;
    private String nome;
    private String email;
    private String prontuario;
    private Integer horasSemanais;
    private Integer numeroDisciplinas;
    private String status; // NORMAL, SOBRECARREGADO, SUBUTILIZADO
    private List<DisciplinaProfessorDTO> disciplinas;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DisciplinaProfessorDTO {
        private Long id;
        private String nome;
        private String codigo;
        private Integer horasSemanais;
        private List<String> turmas;
    }
}
