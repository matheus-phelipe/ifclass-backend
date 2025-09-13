package com.ifclass.ifclass.coordenacao.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstatisticasCoordenacaoDTO {
    private Long totalProfessores;
    private Long professoresAtivos;
    private Long totalDisciplinas;
    private Long totalTurmas;
    private Long totalAulas;
    private Long aulasHoje;
    private Long salasOcupadas;
    private Long totalSalas;
    
    // Estatísticas de carga horária
    private Long professoresNormal;
    private Long professoresSobrecarregados;
    private Long professoresSubutilizados;
    private Double mediaHorasPorProfessor;
    
    // Estatísticas de ocupação
    private Double percentualOcupacaoSalas;
    private Double percentualOcupacaoTurno;
}
