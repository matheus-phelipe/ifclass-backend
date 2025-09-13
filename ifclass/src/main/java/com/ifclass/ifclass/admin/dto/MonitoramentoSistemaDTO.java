package com.ifclass.ifclass.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonitoramentoSistemaDTO {
    private String status; // ONLINE, OFFLINE, MANUTENCAO
    private LocalDateTime ultimaVerificacao;
    private Long tempoOnlineMinutos;
    
    // Métricas de performance
    private Double usoMemoria;
    private Double usoCPU;
    private Long espacoDiscoLivre;
    private Long espacoDiscoTotal;
    
    // Métricas de banco de dados
    private String statusBancoDados;
    private Integer conexoesAtivas;
    private Integer conexoesMaximas;
    private Long tempoRespostaBD; // em ms
    
    // Métricas de aplicação
    private Integer usuariosOnline;
    private Integer sessaoesAtivas;
    private Long requestsUltimaHora;
    private Long errorsUltimaHora;
    
    // Health checks
    private Map<String, String> healthChecks;
    
    // Informações do sistema
    private String versaoJava;
    private String versaoSpring;
    private String versaoSistema;
    private LocalDateTime inicioSistema;
}
