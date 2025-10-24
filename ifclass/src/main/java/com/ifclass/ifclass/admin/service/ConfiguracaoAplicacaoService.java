package com.ifclass.ifclass.admin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Serviço que aplica as configurações do sistema no comportamento da aplicação
 */
@Service
public class ConfiguracaoAplicacaoService {
    
    @Autowired
    private ConfiguracaoSistemaService configuracaoService;
    
    // ===== CONFIGURAÇÕES DE APLICAÇÃO =====
    
    /**
     * Retorna o nome da aplicação
     */
    public String getNomeAplicacao() {
        return configuracaoService.getValor("app.name");
    }
    
    /**
     * Retorna a versão da aplicação
     */
    public String getVersaoAplicacao() {
        return configuracaoService.getValor("app.version");
    }
    
    /**
     * Retorna o ambiente da aplicação
     */
    public String getAmbienteAplicacao() {
        return configuracaoService.getValor("app.environment");
    }
    
    /**
     * Verifica se o modo debug está habilitado
     */
    public boolean isModoDebug() {
        return configuracaoService.getValorComoBoolean("app.debug");
    }
    
    // ===== CONFIGURAÇÕES DE SEGURANÇA =====
    
    /**
     * Retorna o timeout da sessão em segundos
     */
    public Integer getTimeoutSessao() {
        return configuracaoService.getValorComoInteger("security.session.timeout");
    }
    
    /**
     * Retorna o máximo de tentativas de login
     */
    public Integer getMaxTentativasLogin() {
        return configuracaoService.getValorComoInteger("security.max.login.attempts");
    }
    
    /**
     * Retorna o tamanho mínimo da senha
     */
    public Integer getTamanhoMinimoSenha() {
        return configuracaoService.getValorComoInteger("security.password.min.length");
    }
    
    // ===== CONFIGURAÇÕES DE BACKUP =====
    
    /**
     * Verifica se o backup automático está habilitado
     */
    public boolean isBackupAutomaticoHabilitado() {
        return configuracaoService.getValorComoBoolean("backup.automatic.enabled");
    }
    
    /**
     * Retorna o horário do backup automático
     */
    public String getHorarioBackup() {
        return configuracaoService.getValor("backup.schedule.time");
    }
    
    /**
     * Retorna os dias de retenção do backup
     */
    public Integer getDiasRetencaoBackup() {
        return configuracaoService.getValorComoInteger("backup.retention.days");
    }
    
    // ===== CONFIGURAÇÕES DE EMAIL =====
    
    /**
     * Retorna o host SMTP
     */
    public String getHostSMTP() {
        return configuracaoService.getValor("email.smtp.host");
    }
    
    /**
     * Retorna a porta SMTP
     */
    public Integer getPortaSMTP() {
        return configuracaoService.getValorComoInteger("email.smtp.port");
    }
    
    /**
     * Verifica se as notificações por email estão habilitadas
     */
    public boolean isNotificacoesEmailHabilitadas() {
        return configuracaoService.getValorComoBoolean("email.notifications.enabled");
    }
    
    // ===== CONFIGURAÇÕES DE DATABASE =====
    
    /**
     * Retorna o tamanho do pool de conexões
     */
    public Integer getTamanhoPoolConexoes() {
        return configuracaoService.getValorComoInteger("database.connection.pool.size");
    }
    
    /**
     * Retorna o timeout de consultas em segundos
     */
    public Integer getTimeoutConsultas() {
        return configuracaoService.getValorComoInteger("database.query.timeout");
    }
    
    // ===== MÉTODOS DE VALIDAÇÃO =====
    
    /**
     * Valida se uma senha atende aos critérios mínimos
     */
    public boolean validarSenha(String senha) {
        if (senha == null) return false;
        
        Integer tamanhoMinimo = getTamanhoMinimoSenha();
        if (tamanhoMinimo == null) tamanhoMinimo = 6; // valor padrão
        
        return senha.length() >= tamanhoMinimo;
    }
    
    /**
     * Valida se o número de tentativas de login está dentro do limite
     */
    public boolean validarTentativasLogin(int tentativasAtuais) {
        Integer maxTentativas = getMaxTentativasLogin();
        if (maxTentativas == null) maxTentativas = 5; // valor padrão
        
        return tentativasAtuais < maxTentativas;
    }
    
    /**
     * Retorna informações de debug se o modo debug estiver habilitado
     */
    public String getInformacoesDebug() {
        if (!isModoDebug()) {
            return "Modo debug desabilitado";
        }
        
        StringBuilder debug = new StringBuilder();
        debug.append("=== INFORMAÇÕES DE DEBUG ===\n");
        debug.append("Aplicação: ").append(getNomeAplicacao()).append("\n");
        debug.append("Versão: ").append(getVersaoAplicacao()).append("\n");
        debug.append("Ambiente: ").append(getAmbienteAplicacao()).append("\n");
        debug.append("Timeout Sessão: ").append(getTimeoutSessao()).append("s\n");
        debug.append("Max Tentativas Login: ").append(getMaxTentativasLogin()).append("\n");
        debug.append("Tamanho Mínimo Senha: ").append(getTamanhoMinimoSenha()).append("\n");
        debug.append("Backup Automático: ").append(isBackupAutomaticoHabilitado()).append("\n");
        debug.append("Horário Backup: ").append(getHorarioBackup()).append("\n");
        debug.append("Notificações Email: ").append(isNotificacoesEmailHabilitadas()).append("\n");
        debug.append("Pool Conexões: ").append(getTamanhoPoolConexoes()).append("\n");
        debug.append("Timeout Consultas: ").append(getTimeoutConsultas()).append("s\n");
        
        return debug.toString();
    }
}
