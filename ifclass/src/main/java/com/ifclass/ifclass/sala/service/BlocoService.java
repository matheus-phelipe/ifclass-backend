package com.ifclass.ifclass.sala.service;

import com.ifclass.ifclass.sala.model.Bloco;
import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.repository.BlocoRepository;
import com.ifclass.ifclass.sala.repository.SalaRepository;
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
        return blocoRepository.save(bloco);
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public Bloco addSalaToBloco(Long blocoId, Sala sala) {
        // 1. Encontra o bloco "pai"
        Bloco bloco = blocoRepository.findById(blocoId)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado com o id: " + blocoId));

        // 2. Usa o método auxiliar para adicionar a sala.
        //    Isso garante que sala.setBloco(bloco) seja chamado internamente.
        bloco.addSala(sala);

        // 3. Salva a entidade Bloco. A cascata (CascadeType.ALL) cuidará de salvar a nova sala.
        blocoRepository.save(bloco);

        // Retorna a sala recém-adicionada (que agora tem um ID)
        return bloco;
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public Sala updateSala(Long blocoId, Long salaId, Sala salaDetails) {
        // Verifica se o bloco existe
        if (!blocoRepository.existsById(blocoId)) {
            throw new EntityNotFoundException("Bloco não encontrado com o id: " + blocoId);
        }

        // Encontra a sala que será atualizada
        Sala salaExistente = salaRepository.findById(salaId)
                .orElseThrow(() -> new EntityNotFoundException("Sala não encontrada com o id: " + salaId));

        // Validação extra: garante que a sala pertence ao bloco correto
        if (!salaExistente.getBloco().getId().equals(blocoId)) {
            throw new IllegalArgumentException("A sala " + salaId + " não pertence ao bloco " + blocoId);
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
        return salaRepository.save(salaExistente);
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public void deleteBloco(Long blocoId) {
        if (!blocoRepository.existsById(blocoId)) {
            throw new EntityNotFoundException("Bloco não encontrado com id: " + blocoId);
        }
        blocoRepository.deleteById(blocoId);
    }

    @Transactional
    @CacheEvict(value = "blocos", allEntries = true)
    public Bloco deleteSalaFromBloco(Long blocoId, Long salaId) {
        Bloco bloco = blocoRepository.findById(blocoId)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado com id: " + blocoId));

        boolean removed = bloco.getSalas().removeIf(s -> s.getId().equals(salaId));
        if (!removed) {
            throw new EntityNotFoundException("Sala não encontrada com id: " + salaId + " no Bloco " + blocoId);
        }

        return blocoRepository.save(bloco);
    }
}
