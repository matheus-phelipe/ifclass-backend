package com.ifclass.ifclass.admin.controller;

import com.ifclass.ifclass.admin.dto.EstatisticasAdminDTO;
import com.ifclass.ifclass.admin.dto.LogSistemaDTO;
import com.ifclass.ifclass.admin.dto.MonitoramentoSistemaDTO;
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

        // 1. Busca a lista completa de logs (como antes)
        List<LogSistemaDTO> todosOsLogs = adminService.getLogsSistema();

        // 2. Aplica todos os filtros recebidos na lista em memória
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
                return true; // Se passar por todos os filtros, mantém o log
            })
            .collect(Collectors.toList());

        // 3. Gera o arquivo Excel apenas com os logs filtrados
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
        // Simular reinicialização de serviços
        return ResponseEntity.ok("Serviços reiniciados com sucesso");
    }

    @PostMapping("/sistema/cache/clear")
    public ResponseEntity<String> limparCache() {
        // Simular limpeza de cache
        return ResponseEntity.ok("Cache limpo com sucesso");
    }

    @PostMapping("/sistema/database/optimize")
    public ResponseEntity<String> otimizarBanco() {
        // Simular otimização do banco
        return ResponseEntity.ok("Banco de dados otimizado com sucesso");
    }
}
