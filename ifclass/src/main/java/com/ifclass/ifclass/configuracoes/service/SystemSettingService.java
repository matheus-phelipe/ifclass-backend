package com.ifclass.ifclass.configuracoes.service;

import com.ifclass.ifclass.common.exception.ResourceNotFoundException;
import com.ifclass.ifclass.configuracoes.model.SystemSetting;
import com.ifclass.ifclass.configuracoes.respository.SystemSettingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
