package com.ifclass.ifclass.configuracoes.service;

import com.ifclass.ifclass.common.exception.ResourceNotFoundException;
import com.ifclass.ifclass.configuracoes.model.SystemSetting;
import com.ifclass.ifclass.configuracoes.respository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SystemSettingService {
    @Autowired
    private SystemSettingRepository repository;

    public List<SystemSetting> getAllSettings() {
        return repository.findAll();
    }

    public Optional<SystemSetting> getSettingByKey(String key) {
        return repository.findById(key);
    }

    @Transactional
    public SystemSetting saveSetting(SystemSetting setting) {
        // Validações básicas (pode expandir)
        if (setting.getConfigKey() == null || setting.getConfigKey().trim().isEmpty()) {
            throw new IllegalArgumentException("A chave da configuração não pode ser vazia.");
        }
        // Se precisar de validação de duplicidade, como configKey é @Id, o save fará um update se já existir.
        return repository.save(setting);
    }

    @Transactional
    public SystemSetting updateSetting(String key, SystemSetting updatedSetting) {
        return repository.findById(key)
                .map(existingSetting -> {
                    existingSetting.setConfigValue(updatedSetting.getConfigValue());
                    existingSetting.setDescription(updatedSetting.getDescription());
                    existingSetting.setType(updatedSetting.getType());
                    existingSetting.setAdminOnly(updatedSetting.isAdminOnly());
                    // Não permita mudar a configKey em um update
                    return repository.save(existingSetting);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Configuração não encontrada com chave: " + key));
    }

    @Transactional
    public void deleteSetting(String key) {
        if (!repository.existsById(key)) {
            throw new ResourceNotFoundException("Configuração não encontrada com chave: " + key + " para exclusão.");
        }
        repository.deleteById(key);
    }

    // Método para obter um valor de configuração como String
    public String getSettingValue(String key) {
        return repository.findById(key)
                .map(SystemSetting::getConfigValue)
                .orElse(null); // Ou lance uma exceção, dependendo da necessidade
    }

    // Método para obter um valor de configuração como boolean
    public boolean getSettingBooleanValue(String key) {
        return repository.findById(key)
                .map(setting -> Boolean.parseBoolean(setting.getConfigValue()))
                .orElse(false); // Retorna false se não encontrar ou falhar a conversão
    }
    
    @Transactional
    public void resetToDefaults() {
        // 1. Apaga todas as configurações personalizadas existentes
        repository.deleteAll();

        // 2. Obtém a lista de valores padrão
        List<SystemSetting> defaultSettings = getValoresPadrao();

        // 3. Salva os valores padrão no banco
        repository.saveAll(defaultSettings);
    }

    // ----- ADICIONAR ESTE MÉTODO AUXILIAR -----
    /**
     * Retorna a lista de configurações padrão do sistema.
     */
    private List<SystemSetting> getValoresPadrao() {
        List<SystemSetting> defaults = new ArrayList<>();

        // (Use os mesmos valores que estavam mockados no seu frontend)
        defaults.add(new SystemSetting("app.name", "IFClass", "Nome da aplicação", "STRING", false));
        defaults.add(new SystemSetting("session.timeout", "3600", "Timeout da sessão em segundos", "NUMBER", true));
        defaults.add(new SystemSetting("security.max.login.attempts", "5", "Máximo de tentativas de login", "NUMBER", true));
        defaults.add(new SystemSetting("backup.enabled", "true", "Backup automático habilitado", "BOOLEAN", true));
        defaults.add(new SystemSetting("backup.time", "03:00", "Horário do backup automático", "STRING", true));
        
        // (Você vai precisar de um construtor na sua entidade SystemSetting que aceite esses campos)
        // Ex: public SystemSetting(String key, String value, String desc, String type, boolean admin) { ... }

        return defaults;
    }
}
