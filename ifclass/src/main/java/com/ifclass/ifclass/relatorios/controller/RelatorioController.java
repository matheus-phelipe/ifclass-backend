package com.ifclass.ifclass.relatorios.controller;

import com.ifclass.ifclass.relatorios.dto.RelatorioRequestDTO;
import com.ifclass.ifclass.relatorios.service.RelatorioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relatorios")
@CrossOrigin(origins = "*")
public class RelatorioController {

    @Autowired
    private RelatorioService relatorioService;

    @PostMapping("/gerar")
    public ResponseEntity<String> gerarRelatorio(@RequestBody RelatorioRequestDTO request) {
        try {
            String resultado = relatorioService.gerarRelatorio(request);
            return ResponseEntity.ok(resultado);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao gerar relatório: " + e.getMessage());
        }
    }

    @PostMapping("/exportar/pdf")
    public ResponseEntity<ByteArrayResource> exportarPDF(@RequestBody RelatorioRequestDTO request) {
        try {
            byte[] pdfBytes = relatorioService.gerarRelatorioPDF(request);
            
            ByteArrayResource resource = new ByteArrayResource(pdfBytes);
            
            String filename = "relatorio_" + request.getTipo() + "_" + 
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".pdf";
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdfBytes.length)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/exportar/excel")
    public ResponseEntity<ByteArrayResource> exportarExcel(@RequestBody RelatorioRequestDTO request) {
        try {
            byte[] csvBytes = relatorioService.gerarRelatorioExcel(request);

            ByteArrayResource resource = new ByteArrayResource(csvBytes);

            String filename = "relatorio_" + request.getTipo() + "_" +
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss")) + ".csv";

            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                .contentType(MediaType.parseMediaType("text/csv"))
                .contentLength(csvBytes.length)
                .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}
