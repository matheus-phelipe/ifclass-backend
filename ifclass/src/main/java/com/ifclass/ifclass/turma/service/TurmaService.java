package com.ifclass.ifclass.turma.service;

import com.ifclass.ifclass.alunoTurma.repository.AlunoTurmaRepository;
import com.ifclass.ifclass.turma.model.Turma;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import com.ifclass.ifclass.aula.repository.AulaRepository;
import com.ifclass.ifclass.util.log.AppLogger;
import com.ifclass.ifclass.common.exception.ResourceConflictException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@Service
public class TurmaService {

    @Autowired
    private TurmaRepository repository;

    @Autowired
    private AlunoTurmaRepository alunoTurmaRepository;

    @Autowired
    private AulaRepository aulaRepository;

    @Autowired
    private AppLogger appLogger;

    public List<Turma> listar() {
        return repository.findAll();
    }

    public Turma salvar(Turma turma) {
        Turma turmaSalva = repository.save(turma);
        appLogger.logCrudSuccess("Turma", "CRIACAO", "ID: " + turmaSalva.getId());
        return turmaSalva;
    }

    public Turma atualizar(Turma turma) {
        if (!repository.existsById(turma.getId())) {
            String motivo = "Tentativa de atualizar turma não encontrada com ID: " + turma.getId();
            appLogger.logCrudWarning("Turma", "ATUALIZACAO", motivo);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada");
        }
        Turma turmaAtualizada = repository.save(turma);
    
        appLogger.logCrudSuccess("Turma", "ATUALIZACAO", "ID: " + turmaAtualizada.getId());
    
        return turmaAtualizada;
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            String motivo = "Tentativa de excluir turma não encontrada com ID: " + id;
            appLogger.logCrudWarning("Turma", "EXCLUSAO", motivo);

            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Turma não encontrada");
        }

        // Validar relacionamentos antes da exclusão
        validarRelacionamentosAntesExclusao(id);

        // 1. Desvincula todos os alunos da turma
        alunoTurmaRepository.deleteAllByTurmaId(id);
        
        // 2. Exclui a turma, agora sem vínculos
        repository.deleteById(id);

        appLogger.logCrudSuccess("Turma", "EXCLUSAO", "ID: " + id);
    }

    /**
     * Valida se a turma possui relacionamentos que impedem sua exclusão
     */
    private void validarRelacionamentosAntesExclusao(Long turmaId) {
        StringBuilder relacionamentos = new StringBuilder();
        boolean temRelacionamentos = false;

        // Verificar aulas relacionadas
        long countAulas = aulaRepository.count();
        if (countAulas > 0) {
            // Verificar se existem aulas vinculadas a esta turma
            List<Object[]> aulasVinculadas = aulaRepository.findAulasByTurmaId(turmaId);
            if (!aulasVinculadas.isEmpty()) {
                temRelacionamentos = true;
                relacionamentos.append("• ").append(aulasVinculadas.size()).append(" aula(s): ");
                for (int i = 0; i < aulasVinculadas.size() && i < 3; i++) {
                    Object[] aula = aulasVinculadas.get(i);
                    relacionamentos.append("Aula ").append(aula[0]).append(" (").append(aula[1]).append(" - ").append(aula[2]).append(")");
                    if (i < aulasVinculadas.size() - 1 && i < 2) relacionamentos.append(", ");
                }
                if (aulasVinculadas.size() > 3) {
                    relacionamentos.append(" e mais ").append(aulasVinculadas.size() - 3).append(" aula(s)");
                }
                relacionamentos.append("\n");
            }
        }

        if (temRelacionamentos) {
            String motivo = "Não é possível excluir a turma pois ela possui relacionamentos ativos:\n" + relacionamentos.toString() + 
                           "\nPara excluir esta turma, primeiro remova ou altere os relacionamentos listados acima.";
            appLogger.logCrudWarning("Turma", "EXCLUSAO", motivo);
            throw new ResourceConflictException(motivo);
        }
    }
} 