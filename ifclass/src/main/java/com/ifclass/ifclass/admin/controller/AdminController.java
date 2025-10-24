package com.ifclass.ifclass.admin.controller;

import com.ifclass.ifclass.admin.dto.EstatisticasAdminDTO;
import com.ifclass.ifclass.admin.dto.LogSistemaDTO;
import com.ifclass.ifclass.admin.dto.MonitoramentoSistemaDTO;
import com.ifclass.ifclass.admin.dto.ConfiguracaoSistemaDTO;
import com.ifclass.ifclass.admin.service.AdminService;
import com.ifclass.ifclass.common.service.ExcelExportService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/dashboard/estatisticas")
    public ResponseEntity<EstatisticasAdminDTO> getEstatisticasAdmin() {
        EstatisticasAdminDTO estatisticas = adminService.getEstatisticasAdmin();
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/sistema/monitoramento")
    public ResponseEntity<MonitoramentoSistemaDTO> getMonitoramentoSistema() {
        MonitoramentoSistemaDTO monitoramento = adminService.getMonitoramentoSistema();
        return ResponseEntity.ok(monitoramento);
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, Object>> getPerformanceMetrics() {
        Map<String, Object> metrics = adminService.getPerformanceMetrics();
        return ResponseEntity.ok(metrics);
    }

    @GetMapping("/sistema/logs")
    public ResponseEntity<List<LogSistemaDTO>> getLogsSistema() {
        List<LogSistemaDTO> logs = adminService.getLogsSistema();
        return ResponseEntity.ok(logs);
    }

    @GetMapping("/sistema/logs/export")
    public ResponseEntity<InputStreamResource> exportLogsToExcel(
            // Recebe todos os filtros do frontend como parâmetros
            @RequestParam(required = false) String dataInicio,
            @RequestParam(required = false) String dataFim,
            @RequestParam(required = false) String nivel,
            @RequestParam(required = false) String fonte,
            @RequestParam(required = false) String termoBusca
    ) throws IOException {

        List<LogSistemaDTO> todosOsLogs = adminService.getLogsSistema();

        List<LogSistemaDTO> logsFiltrados = todosOsLogs.stream()
            .filter(log -> {
                // Filtro de Data de Início
                if (dataInicio != null && !dataInicio.isEmpty()) {
                    if (log.getTimestamp().toLocalDate().isBefore(LocalDate.parse(dataInicio))) {
                        return false;
                    }
                }
                // Filtro de Data de Fim
                if (dataFim != null && !dataFim.isEmpty()) {
                    if (log.getTimestamp().toLocalDate().isAfter(LocalDate.parse(dataFim))) {
                        return false;
                    }
                }
                // Filtro de Nível
                if (nivel != null && !nivel.isEmpty()) {
                    if (!log.getNivel().equalsIgnoreCase(nivel)) {
                        return false;
                    }
                }
                // Filtro de Fonte (Categoria)
                if (fonte != null && !fonte.isEmpty()) {
                    if (!log.getCategoria().equalsIgnoreCase(fonte)) {
                        return false;
                    }
                }
                // Filtro de Termo de Busca (na mensagem)
                if (termoBusca != null && !termoBusca.isEmpty()) {
                    if (!log.getMensagem().toLowerCase().contains(termoBusca.toLowerCase())) {
                        return false;
                    }
                }
                return true;
            })
            .collect(Collectors.toList());

        ByteArrayInputStream in = excelExportService.logsToExcel(logsFiltrados);
        
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=logs_filtrados.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
    @GetMapping("/sistema/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("OK");
    }

    @PostMapping("/sistema/backup")
    public ResponseEntity<String> criarBackup() {
        try {
            String resultado = adminService.criarBackupReal();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao criar backup: " + e.getMessage());
        }
    }

    @PostMapping("/sistema/restart")
    public ResponseEntity<String> reiniciarServicos() {
        try {
            String resultado = adminService.reiniciarServicosReal();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao reiniciar serviços: " + e.getMessage());
        }
    }

    @PostMapping("/sistema/cache/clear")
    public ResponseEntity<String> limparCache() {
        try {
            String resultado = adminService.limparCacheReal();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao limpar cache: " + e.getMessage());
        }
    }

    @PostMapping("/sistema/database/optimize")
    public ResponseEntity<String> otimizarBanco() {
        try {
            String resultado = adminService.otimizarBancoReal();
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao otimizar banco: " + e.getMessage());
        }
    }

    // ===== ENDPOINTS DE CONFIGURAÇÕES =====

    @GetMapping("/configuracoes")
    public ResponseEntity<List<ConfiguracaoSistemaDTO>> getConfiguracoes() {
        try {
            List<ConfiguracaoSistemaDTO> configuracoes = adminService.getConfiguracoesSistema();
            return ResponseEntity.ok(configuracoes);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @GetMapping("/configuracoes/{chave}")
    public ResponseEntity<ConfiguracaoSistemaDTO> getConfiguracao(@PathVariable String chave) {
        try {
            ConfiguracaoSistemaDTO configuracao = adminService.getConfiguracao(chave);
            if (configuracao != null) {
                return ResponseEntity.ok(configuracao);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/configuracoes/{chave}")
    public ResponseEntity<Map<String, Object>> atualizarConfiguracao(
            @PathVariable String chave, 
            @RequestBody Map<String, String> request) {
        try {
            String novoValor = request.get("valor");
            boolean sucesso = adminService.atualizarConfiguracao(chave, novoValor);
            
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Configuração atualizada com sucesso" : "Erro ao atualizar configuração");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro interno: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/configuracoes/reset")
    public ResponseEntity<Map<String, Object>> resetarConfiguracoes() {
        try {
            boolean sucesso = adminService.resetarConfiguracoes();
            
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", sucesso);
            response.put("mensagem", sucesso ? "Configurações resetadas com sucesso" : "Erro ao resetar configurações");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("sucesso", false);
            response.put("mensagem", "Erro interno: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
