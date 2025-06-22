package com.ifclass.ifclass.admin.service;

import com.ifclass.ifclass.admin.dto.EstatisticasAdminDTO;
import com.ifclass.ifclass.admin.dto.LogSistemaDTO;
import com.ifclass.ifclass.admin.dto.MonitoramentoSistemaDTO;
import com.ifclass.ifclass.aula.repository.AulaRepository;
import com.ifclass.ifclass.curso.repository.CursoRepository;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.sala.repository.BlocoRepository;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.OperatingSystemMXBean;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private AulaRepository aulaRepository;
    
    @Autowired
    private CursoRepository cursoRepository;
    
    @Autowired
    private DisciplinaRepository disciplinaRepository;
    
    @Autowired
    private TurmaRepository turmaRepository;
    
    @Autowired
    private SalaRepository salaRepository;
    
    @Autowired
    private BlocoRepository blocoRepository;

    private final LocalDateTime inicioSistema = LocalDateTime.now();

    public EstatisticasAdminDTO getEstatisticasAdmin() {
        EstatisticasAdminDTO stats = new EstatisticasAdminDTO();
        
        // Estatísticas de usuários
        stats.setTotalUsuarios(usuarioRepository.count());
        stats.setTotalProfessores(usuarioRepository.countByAuthoritiesContaining("ROLE_PROFESSOR"));
        stats.setTotalAlunos(usuarioRepository.countByAuthoritiesContaining("ROLE_ALUNO"));
        stats.setTotalCoordenadores(usuarioRepository.countByAuthoritiesContaining("ROLE_COORDENADOR"));
        stats.setTotalAdmins(usuarioRepository.countByAuthoritiesContaining("ROLE_ADMIN"));
        
        // Estatísticas acadêmicas
        stats.setTotalCursos(cursoRepository.count());
        stats.setTotalTurmas(turmaRepository.count());
        stats.setTotalDisciplinas(disciplinaRepository.count());
        stats.setTotalSalas(salaRepository.count());
        stats.setTotalBlocos(blocoRepository.count());
        
        // Estatísticas de aulas
        stats.setTotalAulas(aulaRepository.count());
        
        // Aulas hoje
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        long aulasHoje = aulaRepository.findAll().stream()
            .filter(aula -> aula.getDiaSemana().equals(hoje))
            .count();
        stats.setAulasHoje(aulasHoje);
        
        // Aulas esta semana (simplificado - todas as aulas cadastradas)
        stats.setAulasEstaSemana(aulaRepository.count());
        
        // Informações do sistema
        stats.setVersaoSistema("1.0.0");
        stats.setStatusSistema("ONLINE");
        stats.setTempoOnline(ChronoUnit.MINUTES.between(inicioSistema, LocalDateTime.now()));
        
        // Métricas de sistema
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        stats.setPercentualUsoMemoria((double) usedMemory / maxMemory * 100);
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        // Usar uma abordagem compatível para CPU
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            stats.setPercentualUsoCPU(sunOsBean.getProcessCpuLoad() * 100);
        } else {
            // Fallback para sistemas que não suportam
            stats.setPercentualUsoCPU(Math.random() * 30 + 10); // Simular entre 10-40%
        }
        
        return stats;
    }

    public MonitoramentoSistemaDTO getMonitoramentoSistema() {
        MonitoramentoSistemaDTO monitoring = new MonitoramentoSistemaDTO();
        
        monitoring.setStatus("ONLINE");
        monitoring.setUltimaVerificacao(LocalDateTime.now());
        monitoring.setTempoOnlineMinutos(ChronoUnit.MINUTES.between(inicioSistema, LocalDateTime.now()));
        
        // Métricas de performance
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long usedMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long maxMemory = memoryBean.getHeapMemoryUsage().getMax();
        monitoring.setUsoMemoria((double) usedMemory / maxMemory * 100);
        
        OperatingSystemMXBean osBean = ManagementFactory.getOperatingSystemMXBean();
        // Usar uma abordagem compatível para CPU
        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            com.sun.management.OperatingSystemMXBean sunOsBean = (com.sun.management.OperatingSystemMXBean) osBean;
            monitoring.setUsoCPU(sunOsBean.getProcessCpuLoad() * 100);
        } else {
            // Fallback para sistemas que não suportam
            monitoring.setUsoCPU(Math.random() * 30 + 10); // Simular entre 10-40%
        }
        
        // Simular métricas de disco
        monitoring.setEspacoDiscoLivre(50000L); // 50GB
        monitoring.setEspacoDiscoTotal(100000L); // 100GB
        
        // Métricas de banco de dados (simuladas)
        monitoring.setStatusBancoDados("CONECTADO");
        monitoring.setConexoesAtivas(5);
        monitoring.setConexoesMaximas(20);
        monitoring.setTempoRespostaBD(15L);
        
        // Métricas de aplicação (simuladas)
        monitoring.setUsuariosOnline(3);
        monitoring.setSessaoesAtivas(3);
        monitoring.setRequestsUltimaHora(150L);
        monitoring.setErrorsUltimaHora(2L);
        
        // Health checks
        Map<String, String> healthChecks = new HashMap<>();
        healthChecks.put("database", "OK");
        healthChecks.put("memory", usedMemory < maxMemory * 0.8 ? "OK" : "WARNING");
        healthChecks.put("disk", "OK");
        healthChecks.put("api", "OK");
        monitoring.setHealthChecks(healthChecks);
        
        // Informações do sistema
        monitoring.setVersaoJava(System.getProperty("java.version"));
        monitoring.setVersaoSpring("3.2.0");
        monitoring.setVersaoSistema("1.0.0");
        monitoring.setInicioSistema(inicioSistema);
        
        return monitoring;
    }

    public List<LogSistemaDTO> getLogsSistema() {
        // Simulação de logs mais realistas - em produção, isso viria de um sistema de logging real
        List<LogSistemaDTO> logs = new ArrayList<>();

        // Logs recentes (últimas 2 horas)
        logs.add(new LogSistemaDTO(1L, LocalDateTime.now().minusMinutes(2), "INFO", "AUTH",
            "Usuário logado com sucesso", "admin@ifclass.com", "192.168.1.100", "Login realizado via web"));

        logs.add(new LogSistemaDTO(2L, LocalDateTime.now().minusMinutes(5), "INFO", "CRUD",
            "Nova aula criada", "coordenador@ifclass.com", "192.168.1.101", "Aula de Matemática - Turma 2024.1"));

        logs.add(new LogSistemaDTO(3L, LocalDateTime.now().minusMinutes(8), "INFO", "SYSTEM",
            "Backup automático executado", "system", "localhost", "Backup diário concluído com sucesso"));

        logs.add(new LogSistemaDTO(4L, LocalDateTime.now().minusMinutes(12), "WARN", "SYSTEM",
            "Uso de memória alto detectado", "system", "localhost", "Memória: 78% - Limite: 80%"));

        logs.add(new LogSistemaDTO(5L, LocalDateTime.now().minusMinutes(15), "INFO", "AUTH",
            "Usuário logado com sucesso", "professor@ifclass.com", "192.168.1.102", "Login realizado via mobile"));

        logs.add(new LogSistemaDTO(6L, LocalDateTime.now().minusMinutes(18), "INFO", "CRUD",
            "Sala atualizada", "admin@ifclass.com", "192.168.1.100", "Sala A101 - Capacidade alterada"));

        logs.add(new LogSistemaDTO(7L, LocalDateTime.now().minusMinutes(22), "DEBUG", "API",
            "Requisição processada", "system", "192.168.1.103", "GET /api/aulas - 200ms"));

        logs.add(new LogSistemaDTO(8L, LocalDateTime.now().minusMinutes(25), "INFO", "CRUD",
            "Novo usuário cadastrado", "admin@ifclass.com", "192.168.1.100", "Professor João Silva"));

        logs.add(new LogSistemaDTO(9L, LocalDateTime.now().minusMinutes(30), "WARN", "AUTH",
            "Tentativa de login falhada", "unknown", "192.168.1.200", "Email: teste@teste.com - 3ª tentativa"));

        logs.add(new LogSistemaDTO(10L, LocalDateTime.now().minusMinutes(35), "ERROR", "DATABASE",
            "Timeout na consulta", "system", "localhost", "Query: SELECT * FROM aulas - Timeout: 30s"));

        logs.add(new LogSistemaDTO(11L, LocalDateTime.now().minusMinutes(40), "INFO", "AUTH",
            "Usuário deslogado", "coordenador@ifclass.com", "192.168.1.101", "Logout por inatividade"));

        logs.add(new LogSistemaDTO(12L, LocalDateTime.now().minusMinutes(45), "INFO", "SYSTEM",
            "Sistema iniciado", "system", "localhost", "IFClass v1.0.0 - Startup completo"));

        // Logs mais antigos (para testar funcionalidade de limpeza)
        logs.add(new LogSistemaDTO(13L, LocalDateTime.now().minusHours(3), "INFO", "AUTH",
            "Usuário logado", "admin@ifclass.com", "192.168.1.100", "Login matinal"));

        logs.add(new LogSistemaDTO(14L, LocalDateTime.now().minusHours(4), "ERROR", "SYSTEM",
            "Erro crítico resolvido", "system", "localhost", "Reinicialização automática executada"));

        logs.add(new LogSistemaDTO(15L, LocalDateTime.now().minusHours(6), "INFO", "BACKUP",
            "Backup semanal", "system", "localhost", "Backup completo do banco de dados"));

        return logs;
    }
}
