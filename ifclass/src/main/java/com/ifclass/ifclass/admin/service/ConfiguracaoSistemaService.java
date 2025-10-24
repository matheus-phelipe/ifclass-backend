package com.ifclass.ifclass.admin.service;

import com.ifclass.ifclass.admin.dto.ConfiguracaoSistemaDTO;
import com.ifclass.ifclass.admin.entity.ConfiguracaoSistema;
import com.ifclass.ifclass.admin.repository.ConfiguracaoSistemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.annotation.PostConstruct;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConfiguracaoSistemaService {
    
    @Autowired
    private ConfiguracaoSistemaRepository configuracaoRepository;
    
    /**
     * Inicializa automaticamente as configurações quando o serviço é criado
     */
    @PostConstruct
    public void inicializarConfiguracoesAutomaticamente() {
        try {
            inicializarConfiguracoesPadrao();
            System.out.println("✅ Configurações do sistema inicializadas automaticamente");
        } catch (Exception e) {
            System.err.println("❌ Erro ao inicializar configurações: " + e.getMessage());
        }
    }
    
    /**
     * Inicializa as configurações padrão do sistema
     */
    public void inicializarConfiguracoesPadrao() {
        if (configuracaoRepository.count() == 0) {
            criarConfiguracao("app.name", "IFClass", "STRING", "Nome da aplicação", "Geral", true);
            criarConfiguracao("app.version", "1.0.0", "STRING", "Versão da aplicação", "Geral", false);
            criarConfiguracao("app.environment", "development", "STRING", "Ambiente de execução", "Geral", false);
            criarConfiguracao("app.debug", "true", "BOOLEAN", "Modo debug", "Geral", true);
            
            criarConfiguracao("security.session.timeout", "3600", "NUMBER", "Timeout da sessão em segundos", "Segurança", true);
            criarConfiguracao("security.max.login.attempts", "5", "NUMBER", "Máximo de tentativas de login", "Segurança", true);
            criarConfiguracao("security.password.min.length", "6", "NUMBER", "Tamanho mínimo da senha", "Segurança", true);
            
            criarConfiguracao("backup.automatic.enabled", "true", "BOOLEAN", "Backup automático habilitado", "Backup", true);
            criarConfiguracao("backup.schedule.time", "03:00", "STRING", "Horário do backup automático", "Backup", true);
            criarConfiguracao("backup.retention.days", "30", "NUMBER", "Dias de retenção do backup", "Backup", true);
            
            criarConfiguracao("email.smtp.host", "localhost", "STRING", "Servidor SMTP", "Email", true);
            criarConfiguracao("email.smtp.port", "587", "NUMBER", "Porta SMTP", "Email", true);
            criarConfiguracao("email.notifications.enabled", "true", "BOOLEAN", "Notificações por email habilitadas", "Email", true);
            
            criarConfiguracao("database.connection.pool.size", "10", "NUMBER", "Tamanho do pool de conexões", "Database", true);
            criarConfiguracao("database.query.timeout", "30", "NUMBER", "Timeout de consultas em segundos", "Database", true);
        }
    }
    
    private void criarConfiguracao(String chave, String valor, String tipo, String descricao, String categoria, boolean editavel) {
        ConfiguracaoSistema config = new ConfiguracaoSistema(chave, valor, tipo, descricao, categoria);
        config.setEditavel(editavel);
        config.setValorPadrao(valor);
        configuracaoRepository.save(config);
    }
    
    /**
     * Busca todas as configurações
     */
    @Transactional(readOnly = true)
    public List<ConfiguracaoSistemaDTO> getTodasConfiguracoes() {
        return configuracaoRepository.findAll().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca configuração por chave
     */
    @Transactional(readOnly = true)
    public Optional<ConfiguracaoSistemaDTO> getConfiguracao(String chave) {
        return configuracaoRepository.findByChave(chave)
                .map(this::converterParaDTO);
    }
    
    /**
     * Atualiza uma configuração
     */
    public ConfiguracaoSistemaDTO atualizarConfiguracao(String chave, String novoValor, String usuario) {
        ConfiguracaoSistema config = configuracaoRepository.findByChave(chave)
                .orElseThrow(() -> new RuntimeException("Configuração não encontrada: " + chave));
        
        if (!config.getEditavel()) {
            throw new RuntimeException("Configuração não é editável: " + chave);
        }
        
        // Validar tipo
        validarValor(config.getTipo(), novoValor);
        
        config.setValor(novoValor);
        config.setUsuarioAtualizacao(usuario);
        config.setUltimaAtualizacao(LocalDateTime.now());
        
        ConfiguracaoSistema configSalva = configuracaoRepository.save(config);
        return converterParaDTO(configSalva);
    }
    
    /**
     * Reseta todas as configurações para valores padrão
     */
    public void resetarConfiguracoes(String usuario) {
        List<ConfiguracaoSistema> configuracoes = configuracaoRepository.findAll();
        for (ConfiguracaoSistema config : configuracoes) {
            if (config.getEditavel() && config.getValorPadrao() != null) {
                config.setValor(config.getValorPadrao());
                config.setUsuarioAtualizacao(usuario);
                config.setUltimaAtualizacao(LocalDateTime.now());
            }
        }
        configuracaoRepository.saveAll(configuracoes);
    }
    
    /**
     * Busca configurações por categoria
     */
    @Transactional(readOnly = true)
    public List<ConfiguracaoSistemaDTO> getConfiguracoesPorCategoria(String categoria) {
        return configuracaoRepository.findByCategoriaOrderByChave(categoria).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Busca todas as categorias
     */
    @Transactional(readOnly = true)
    public List<String> getCategorias() {
        return configuracaoRepository.findDistinctCategorias();
    }
    
    /**
     * Valida valor baseado no tipo
     */
    private void validarValor(String tipo, String valor) {
        switch (tipo.toUpperCase()) {
            case "NUMBER":
                try {
                    Double.parseDouble(valor);
                } catch (NumberFormatException e) {
                    throw new RuntimeException("Valor deve ser um número válido");
                }
                break;
            case "BOOLEAN":
                if (!"true".equalsIgnoreCase(valor) && !"false".equalsIgnoreCase(valor)) {
                    throw new RuntimeException("Valor deve ser 'true' ou 'false'");
                }
                break;
            case "STRING":
                if (valor == null || valor.trim().isEmpty()) {
                    throw new RuntimeException("Valor não pode ser vazio");
                }
                break;
        }
    }
    
    /**
     * Converte entidade para DTO
     */
    private ConfiguracaoSistemaDTO converterParaDTO(ConfiguracaoSistema config) {
        ConfiguracaoSistemaDTO dto = new ConfiguracaoSistemaDTO();
        dto.setChave(config.getChave());
        dto.setValor(config.getValor());
        dto.setTipo(config.getTipo());
        dto.setDescricao(config.getDescricao());
        dto.setCategoria(config.getCategoria());
        dto.setEditavel(config.getEditavel());
        dto.setUltimaAtualizacao(config.getUltimaAtualizacao());
        dto.setValorPadrao(config.getValorPadrao());
        return dto;
    }
    
    /**
     * Busca valor de configuração como String
     */
    @Transactional(readOnly = true)
    public String getValor(String chave) {
        return configuracaoRepository.findByChave(chave)
                .map(ConfiguracaoSistema::getValor)
                .orElse(null);
    }
    
    /**
     * Busca valor de configuração como Integer
     */
    @Transactional(readOnly = true)
    public Integer getValorComoInteger(String chave) {
        return configuracaoRepository.findByChave(chave)
                .map(ConfiguracaoSistema::getValorComoInteger)
                .orElse(null);
    }
    
    /**
     * Busca valor de configuração como Boolean
     */
    @Transactional(readOnly = true)
    public Boolean getValorComoBoolean(String chave) {
        return configuracaoRepository.findByChave(chave)
                .map(ConfiguracaoSistema::getValorComoBoolean)
                .orElse(false);
    }
    
    /**
     * Busca valor de configuração como Long
     */
    @Transactional(readOnly = true)
    public Long getValorComoLong(String chave) {
        return configuracaoRepository.findByChave(chave)
                .map(ConfiguracaoSistema::getValorComoLong)
                .orElse(null);
    }
}
