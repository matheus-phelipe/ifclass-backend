package com.ifclass.ifclass.sala.service;

import com.ifclass.ifclass.sala.model.Bloco;
import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.repository.BlocoRepository;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import com.ifclass.ifclass.util.log.AppLogger;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlocoService {

    @Autowired
    private BlocoRepository blocoRepository;

    @Autowired
    private SalaRepository salaRepository;

    @Autowired
    private AppLogger appLogger;

    @Transactional(readOnly = true)
    @Cacheable(value = "blocos", key = "'all'")
    public List<Bloco> findAll() {
        return blocoRepository.findAll();
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public Bloco createBloco(Bloco bloco) {
        if (bloco.getSalas() == null) {
            bloco.setSalas(new java.util.ArrayList<>());
        }
        Bloco blocoSalvo = blocoRepository.save(bloco);
    
        appLogger.logCrudSuccess("Bloco", "CRIACAO", "ID: " + blocoSalvo.getId() + ", Nome: " + blocoSalvo.getNome());
        
        return blocoSalvo;
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public Bloco addSalaToBloco(Long blocoId, Sala sala) {
        // 1. Encontra o bloco "pai"
        Bloco bloco = blocoRepository.findById(blocoId).orElseThrow(() -> {
        String motivo = "Bloco não encontrado com o id: " + blocoId;
        appLogger.logCrudWarning("Bloco", "ATUALIZACAO", motivo + " ao tentar adicionar sala.");
        return new EntityNotFoundException(motivo);
        });

        // 2. Usa o método auxiliar para adicionar a sala.
        //    Isso garante que sala.setBloco(bloco) seja chamado internamente.
        bloco.addSala(sala);

        // 3. Salva a entidade Bloco. A cascata (CascadeType.ALL) cuidará de salvar a nova sala.
        Bloco blocoSalvo = blocoRepository.save(bloco);

        Sala salaAdicionada = blocoSalvo.getSalas().stream()
        .filter(s -> s.getCodigo().equals(sala.getCodigo()))
        .findFirst().orElse(null);
        Long salaId = salaAdicionada != null ? salaAdicionada.getId() : -1L;

        appLogger.logCrudSuccess("Bloco", "ATUALIZACAO", "Sala ID " + salaId + " adicionada ao Bloco ID " + blocoId);

        // Retorna a sala recém-adicionada (que agora tem um ID)
        return blocoSalvo;
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public Sala updateSala(Long blocoId, Long salaId, Sala salaDetails) {
        // Verifica se o bloco existe
        if (!blocoRepository.existsById(blocoId)) {
            String motivo = "Bloco não encontrado com o id: " + blocoId;
            appLogger.logCrudWarning("Sala", "ATUALIZACAO", motivo);
            throw new EntityNotFoundException(motivo);
        }

        // Encontra a sala que será atualizada
        Sala salaExistente = salaRepository.findById(salaId).orElseThrow(() -> {
        String motivo = "Sala não encontrada com o id: " + salaId;
        appLogger.logCrudWarning("Sala", "ATUALIZACAO", motivo);
        return new EntityNotFoundException(motivo);
        });

        // Validação extra: garante que a sala pertence ao bloco correto
        if (!salaExistente.getBloco().getId().equals(blocoId)) {
            String motivo = "A sala " + salaId + " não pertence ao bloco " + blocoId;
            appLogger.logCrudWarning("Sala", "ATUALIZACAO", motivo);
            throw new IllegalArgumentException(motivo);
        }

        // Atualiza os campos da sala existente com os novos detalhes
        salaExistente.setCodigo(salaDetails.getCodigo());
        salaExistente.setCapacidade(salaDetails.getCapacidade());
        salaExistente.setPosX(salaDetails.getPosX());
        salaExistente.setPosY(salaDetails.getPosY());
        salaExistente.setLargura(salaDetails.getLargura());
        salaExistente.setAltura(salaDetails.getAltura());
        salaExistente.setCor(salaDetails.getCor());

        // Salva e retorna a sala atualizada
        Sala salaSalva = salaRepository.save(salaExistente);

        appLogger.logCrudSuccess("Sala", "ATUALIZACAO", "ID: " + salaSalva.getId() + " no Bloco ID: " + blocoId);

        return salaSalva;
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public void deleteBloco(Long blocoId) {
        if (!blocoRepository.existsById(blocoId)) {
            String motivo = "Bloco não encontrado com id: " + blocoId;
            appLogger.logCrudWarning("Bloco", "EXCLUSAO", motivo);

            throw new EntityNotFoundException("Bloco não encontrado com id: " + blocoId);
        }
        blocoRepository.deleteById(blocoId);

        appLogger.logCrudSuccess("Bloco", "EXCLUSAO", "ID: " + blocoId);
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public Bloco deleteSalaFromBloco(Long blocoId, Long salaId) {
        Bloco bloco = blocoRepository.findById(blocoId).orElseThrow(() -> {
        String motivo = "Bloco não encontrado com id: " + blocoId;
        appLogger.logCrudWarning("Bloco", "ATUALIZACAO", motivo + " ao tentar remover sala.");
        return new EntityNotFoundException(motivo);
        });

        boolean removed = bloco.getSalas().removeIf(s -> s.getId().equals(salaId));
        if (!removed) {
            String motivo = "Sala não encontrada com id: " + salaId + " no Bloco " + blocoId;
            appLogger.logCrudWarning("Bloco", "ATUALIZACAO", motivo);
            throw new EntityNotFoundException(motivo);
        }

        Bloco blocoSalvo = blocoRepository.save(bloco);

        appLogger.logCrudSuccess("Bloco", "ATUALIZACAO", "Sala ID " + salaId + " removida do Bloco ID " + blocoId);

        return blocoSalvo;
    }
}
