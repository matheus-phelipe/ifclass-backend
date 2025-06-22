package com.ifclass.ifclass.coordenacao.service;

import com.ifclass.ifclass.coordenacao.dto.EstatisticasCoordenacaoDTO;
import com.ifclass.ifclass.coordenacao.dto.ProfessorCargaDTO;
import com.ifclass.ifclass.aula.repository.AulaRepository;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;
import com.ifclass.ifclass.aula.model.Aula;
import com.ifclass.ifclass.usuario.model.Usuario;
import com.ifclass.ifclass.disciplina.model.Disciplina;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CoordenacaoService {

    @Autowired
    private UsuarioRepository usuarioRepository;
    
    @Autowired
    private AulaRepository aulaRepository;
    
    @Autowired
    private DisciplinaRepository disciplinaRepository;
    
    @Autowired
    private TurmaRepository turmaRepository;
    
    @Autowired
    private SalaRepository salaRepository;

    public EstatisticasCoordenacaoDTO getEstatisticasDashboard() {
        EstatisticasCoordenacaoDTO stats = new EstatisticasCoordenacaoDTO();
        
        // Estatísticas básicas
        stats.setTotalProfessores(usuarioRepository.countByAuthoritiesContaining("ROLE_PROFESSOR"));
        stats.setProfessoresAtivos(usuarioRepository.countByAuthoritiesContaining("ROLE_PROFESSOR")); // Todos ativos por enquanto
        stats.setTotalDisciplinas(disciplinaRepository.count());
        stats.setTotalTurmas(turmaRepository.count());
        stats.setTotalAulas(aulaRepository.count());
        stats.setTotalSalas(salaRepository.count());
        
        // Aulas hoje
        DayOfWeek hoje = LocalDate.now().getDayOfWeek();
        List<Aula> aulasHoje = aulaRepository.findAll().stream()
            .filter(aula -> aula.getDiaSemana().equals(hoje))
            .collect(Collectors.toList());
        stats.setAulasHoje((long) aulasHoje.size());
        
        // Salas ocupadas hoje
        long salasOcupadasHoje = aulasHoje.stream()
            .map(aula -> aula.getSala().getId())
            .distinct()
            .count();
        stats.setSalasOcupadas(salasOcupadasHoje);
        
        // Cálculo de carga horária dos professores
        calcularEstatisticasCargaHoraria(stats);
        
        // Percentuais de ocupação
        if (stats.getTotalSalas() > 0) {
            stats.setPercentualOcupacaoSalas((double) salasOcupadasHoje / stats.getTotalSalas() * 100);
        }
        
        return stats;
    }

    private void calcularEstatisticasCargaHoraria(EstatisticasCoordenacaoDTO stats) {
        List<Usuario> professores = usuarioRepository.findByAuthoritiesContaining("ROLE_PROFESSOR");
        
        long normal = 0, sobrecarregados = 0, subutilizados = 0;
        double totalHoras = 0;
        
        for (Usuario professor : professores) {
            int horasSemanais = calcularHorasSemanaisProfessor(professor.getId());
            totalHoras += horasSemanais;
            
            if (horasSemanais >= 20) {
                sobrecarregados++;
            } else if (horasSemanais < 12) {
                subutilizados++;
            } else {
                normal++;
            }
        }
        
        stats.setProfessoresNormal(normal);
        stats.setProfessoresSobrecarregados(sobrecarregados);
        stats.setProfessoresSubutilizados(subutilizados);
        
        if (!professores.isEmpty()) {
            stats.setMediaHorasPorProfessor(totalHoras / professores.size());
        }
    }

    public List<ProfessorCargaDTO> getProfessoresCarga() {
        List<Usuario> professores = usuarioRepository.findByAuthoritiesContaining("ROLE_PROFESSOR");
        
        return professores.stream().map(professor -> {
            ProfessorCargaDTO dto = new ProfessorCargaDTO();
            dto.setId(professor.getId());
            dto.setNome(professor.getNome());
            dto.setEmail(professor.getEmail());
            dto.setProntuario(professor.getProntuario());
            
            int horasSemanais = calcularHorasSemanaisProfessor(professor.getId());
            dto.setHorasSemanais(horasSemanais);
            dto.setNumeroDisciplinas(professor.getDisciplinas() != null ? professor.getDisciplinas().size() : 0);
            
            // Determinar status
            if (horasSemanais >= 20) {
                dto.setStatus("SOBRECARREGADO");
            } else if (horasSemanais < 12) {
                dto.setStatus("SUBUTILIZADO");
            } else {
                dto.setStatus("NORMAL");
            }
            
            // Mapear disciplinas
            if (professor.getDisciplinas() != null) {
                List<ProfessorCargaDTO.DisciplinaProfessorDTO> disciplinasDTO = professor.getDisciplinas().stream()
                    .map(disciplina -> {
                        ProfessorCargaDTO.DisciplinaProfessorDTO discDTO = new ProfessorCargaDTO.DisciplinaProfessorDTO();
                        discDTO.setId(disciplina.getId());
                        discDTO.setNome(disciplina.getNome());
                        discDTO.setCodigo(disciplina.getCodigo());
                        
                        // Calcular horas semanais desta disciplina para este professor
                        int horasDisciplina = calcularHorasDisciplinaProfessor(professor.getId(), disciplina.getId());
                        discDTO.setHorasSemanais(horasDisciplina);
                        
                        // Buscar turmas desta disciplina
                        List<String> turmas = aulaRepository.findByProfessorId(professor.getId()).stream()
                            .filter(aula -> aula.getDisciplina().getId().equals(disciplina.getId()))
                            .map(aula -> aula.getTurma().getCurso().getNome() + " - " + aula.getTurma().getAno() + "º/" + aula.getTurma().getSemestre())
                            .distinct()
                            .collect(Collectors.toList());
                        discDTO.setTurmas(turmas);
                        
                        return discDTO;
                    })
                    .collect(Collectors.toList());
                dto.setDisciplinas(disciplinasDTO);
            }
            
            return dto;
        }).collect(Collectors.toList());
    }

    private int calcularHorasSemanaisProfessor(Long professorId) {
        List<Aula> aulas = aulaRepository.findByProfessorId(professorId);
        return aulas.size(); // Cada aula = 1 hora (simplificado)
    }
    
    private int calcularHorasDisciplinaProfessor(Long professorId, Long disciplinaId) {
        List<Aula> aulas = aulaRepository.findByProfessorId(professorId);
        return (int) aulas.stream()
            .filter(aula -> aula.getDisciplina().getId().equals(disciplinaId))
            .count();
    }
}
