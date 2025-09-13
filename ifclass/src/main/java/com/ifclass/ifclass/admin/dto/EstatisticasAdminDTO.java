package com.ifclass.ifclass.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasAdminDTO {
    private Long totalUsuarios;
    private Long totalProfessores;
    private Long totalAlunos;
    private Long totalCoordenadores;
    private Long totalAdmins;
    
    private Long totalCursos;
    private Long totalTurmas;
    private Long totalDisciplinas;
    private Long totalSalas;
    private Long totalBlocos;
    
    private Long totalAulas;
    private Long aulasHoje;
    private Long aulasEstaSemana;
    
    // Estatísticas do sistema
    private String versaoSistema;
    private String statusSistema;
    private Long tempoOnline; // em minutos
    private Double percentualUsoMemoria;
    private Double percentualUsoCPU;
}
