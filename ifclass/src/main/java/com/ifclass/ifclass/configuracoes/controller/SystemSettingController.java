package com.ifclass.ifclass.configuracoes.controller;

import com.ifclass.ifclass.configuracoes.model.SystemSetting;
import com.ifclass.ifclass.configuracoes.service.SystemSettingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/configuracoes/settings") // Endpoint para SystemSettings
@CrossOrigin(origins = "*") // Permite chamadas de qualquer origem (ajuste em produção)
public class SystemSettingController {
    @Autowired
    private SystemSettingService service;

    @GetMapping
    public ResponseEntity<List<SystemSetting>> getAllSettings() {
        return ResponseEntity.ok(service.getAllSettings());
    }

    @GetMapping("/{key}")
    public ResponseEntity<SystemSetting> getSettingByKey(@PathVariable String key) {
        return service.getSettingByKey(key)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<SystemSetting> createSetting(@RequestBody SystemSetting setting) {
        SystemSetting savedSetting = service.saveSetting(setting);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSetting);
    }

    @PutMapping("/{key}")
    public ResponseEntity<SystemSetting> updateSetting(@PathVariable String key, @RequestBody SystemSetting setting) {
        SystemSetting updatedSetting = service.updateSetting(key, setting);
        return ResponseEntity.ok(updatedSetting);
    }

    @DeleteMapping("/{key}")
    @ResponseStatus(HttpStatus.NO_CONTENT) // 204 No Content para deleção bem-sucedida
    public void deleteSetting(@PathVariable String key) {
        service.deleteSetting(key);
    }
}
