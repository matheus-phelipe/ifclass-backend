package com.ifclass.ifclass.sala.service;

import com.ifclass.ifclass.sala.model.Bloco;
import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.repository.BlocoRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BlocoService {

    @Autowired
    private BlocoRepository blocoRepository;

    @Transactional(readOnly = true)
    public List<Bloco> findAll() {
        return blocoRepository.findAll();
    }

    @Transactional
    public Bloco createBloco(Bloco bloco) {
        if (bloco.getSalas() == null) {
            bloco.setSalas(new java.util.ArrayList<>());
        }
        return blocoRepository.save(bloco);
    }

    @Transactional
    public Bloco addSalaToBloco(Long blocoId, Sala sala) {
        Bloco bloco = blocoRepository.findById(blocoId)
                .orElseThrow(() -> new EntityNotFoundException("Bloco não encontrado com id: " + blocoId));
        bloco.getSalas().add(sala);
        return blocoRepository.save(bloco);
    }

    @Transactional
    public void deleteBloco(Long blocoId) {
        if (!blocoRepository.existsById(blocoId)) {
            throw new EntityNotFoundException("Bloco não encontrado com id: " + blocoId);
        }
        blocoRepository.deleteById(blocoId);
    }

    @Transactional
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
