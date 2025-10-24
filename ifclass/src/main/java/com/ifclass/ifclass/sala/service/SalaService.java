package com.ifclass.ifclass.sala.service;

import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import com.ifclass.ifclass.util.log.AppLogger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class SalaService {

    @Autowired
    private SalaRepository repository;

    @Autowired
    private AppLogger appLogger;

    @Cacheable(value = "salas", key = "'all'")
    public List<Sala> listar() {
        return repository.findAll();
    }

    @CacheEvict(value = "salas", allEntries = true)
    public Sala salvar(Sala sala) {
         String operacao = (sala.getId() == null) ? "CRIACAO" : "ATUALIZACAO";
    
        Sala salaSalva = repository.save(sala);
        
        appLogger.logCrudSuccess("Sala", operacao, "ID: " + salaSalva.getId());
        
        return salaSalva;
    }

    @CacheEvict(value = "salas", allEntries = true)
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
        String motivo = "Tentativa de excluir sala não encontrada com ID: " + id;
        appLogger.logCrudWarning("Sala", "EXCLUSAO", motivo);

        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala nao encontrada");
        }
        
        repository.deleteById(id);
        
        appLogger.logCrudSuccess("Sala", "EXCLUSAO", "ID: " + id);
    }
}
