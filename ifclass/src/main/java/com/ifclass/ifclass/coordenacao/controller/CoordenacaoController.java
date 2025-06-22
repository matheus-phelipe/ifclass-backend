package com.ifclass.ifclass.coordenacao.controller;

import com.ifclass.ifclass.coordenacao.dto.EstatisticasCoordenacaoDTO;
import com.ifclass.ifclass.coordenacao.dto.ProfessorCargaDTO;
import com.ifclass.ifclass.coordenacao.service.CoordenacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/coordenacao")
@CrossOrigin(origins = "*")
public class CoordenacaoController {

    @Autowired
    private CoordenacaoService coordenacaoService;

    @GetMapping("/dashboard/estatisticas")
    public ResponseEntity<EstatisticasCoordenacaoDTO> getEstatisticasDashboard() {
        EstatisticasCoordenacaoDTO estatisticas = coordenacaoService.getEstatisticasDashboard();
        return ResponseEntity.ok(estatisticas);
    }

    @GetMapping("/professores/carga")
    public ResponseEntity<List<ProfessorCargaDTO>> getProfessoresCarga() {
        List<ProfessorCargaDTO> professores = coordenacaoService.getProfessoresCarga();
        return ResponseEntity.ok(professores);
    }

    @GetMapping("/professores/carga/{professorId}")
    public ResponseEntity<ProfessorCargaDTO> getProfessorCarga(@PathVariable Long professorId) {
        List<ProfessorCargaDTO> professores = coordenacaoService.getProfessoresCarga();
        ProfessorCargaDTO professor = professores.stream()
            .filter(p -> p.getId().equals(professorId))
            .findFirst()
            .orElse(null);
        
        if (professor == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(professor);
    }
}
