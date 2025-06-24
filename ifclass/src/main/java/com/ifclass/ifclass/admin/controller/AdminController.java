package com.ifclass.ifclass.admin.controller;

import com.ifclass.ifclass.admin.dto.EstatisticasAdminDTO;
import com.ifclass.ifclass.admin.dto.LogSistemaDTO;
import com.ifclass.ifclass.admin.dto.MonitoramentoSistemaDTO;
import com.ifclass.ifclass.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private AdminService adminService;

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
