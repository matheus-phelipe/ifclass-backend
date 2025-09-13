package com.ifclass.ifclass.aula.service;

import com.ifclass.ifclass.aula.model.Aula;
import com.ifclass.ifclass.aula.repository.AulaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class AulaService {
    @Autowired
    private AulaRepository repository;

    public Aula salvar(Aula aula) {
        return repository.save(aula);
    }

    public void remover(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Aula não encontrada com o id: " + id);
        }
        repository.deleteById(id);
    }

    public List<Aula> buscarPorTurmaEData(Long turmaId, LocalDate data) {
        return repository.findByTurmaIdAndDiaSemana(turmaId, data.getDayOfWeek());
    }

    public List<Aula> buscarPorProfessorEData(Long professorId, LocalDate data) {
        return repository.findByProfessorIdAndDiaSemana(professorId, data.getDayOfWeek());
    }

    public List<Aula> listarTodas() {
        return repository.findAll();
    }

    public List<Aula> buscarPorProfessor(Long professorId) {
        return repository.findByProfessorId(professorId);
    }

    public Optional<Aula> buscarProximaAula(Long professorId) {
        LocalTime agora = LocalTime.now();
        DayOfWeek diaAtual = LocalDate.now().getDayOfWeek();

        List<Aula> todasAulas = repository.findByProfessorId(professorId);

        // 1. Procura por aulas mais tarde no mesmo dia
        Optional<Aula> proximaHoje = todasAulas.stream()
                .filter(a -> a.getDiaSemana() == diaAtual && a.getHora().isAfter(agora))
                .min(Comparator.comparing(Aula::getHora));

        if (proximaHoje.isPresent()) {
            return proximaHoje;
        }

        // 2. Se não houver, procura nos dias seguintes da semana
        List<DayOfWeek> diasSeguintes = Stream.iterate(diaAtual.plus(1), d -> d.plus(1))
                .limit(6)
                .collect(java.util.stream.Collectors.toList());

        for (DayOfWeek dia : diasSeguintes) {
            Optional<Aula> proximaNaSemana = todasAulas.stream()
                    .filter(a -> a.getDiaSemana() == dia)
                    .min(Comparator.comparing(Aula::getHora));

            if (proximaNaSemana.isPresent()) {
                return proximaNaSemana;
            }
        }

        return Optional.empty(); // Nenhuma aula futura encontrada
    }

    public List<Aula> buscarAulasDeHoje() {
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        return repository.findByDiaSemana(hoje)
                .stream()
                .sorted(Comparator.comparing(Aula::getHora))
                .collect(java.util.stream.Collectors.toList());
    }
}