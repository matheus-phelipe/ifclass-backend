package com.ifclass.ifclass.aviso.service;

import com.ifclass.ifclass.aviso.model.Aviso;
import com.ifclass.ifclass.aviso.repository.AvisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AvisoService {

    @Autowired
    private AvisoRepository avisoRepository;

    public List<Aviso> listarTodos() {
        return avisoRepository.findAll();
    }

    public Optional<Aviso> buscarPorId(Long id) {
        return avisoRepository.findById(id);
    }

    public Aviso criar(Aviso aviso) {
        return avisoRepository.save(aviso);
    }

    public Optional<Aviso> atualizar(Long id, Aviso aviso) {
        if (!avisoRepository.existsById(id)) {
            return Optional.empty();
        }
        aviso.setId(id);
        return Optional.of(avisoRepository.save(aviso));
    }

    public boolean deletar(Long id) {
        if (!avisoRepository.existsById(id)) {
            return false;
        }
        avisoRepository.deleteById(id);
        return true;
    }
}
