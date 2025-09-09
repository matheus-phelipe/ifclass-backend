package com.ifclass.ifclass.relatorios.service;

import com.ifclass.ifclass.aula.repository.AulaRepository;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.relatorios.dto.RelatorioRequestDTO;
import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class RelatorioService {
    @Autowired
    private AulaRepository aulaRepository;
    @Autowired
    private UsuarioRepository usuarioRepository;
    @Autowired
    private SalaRepository salaRepository;
    @Autowired
    private TurmaRepository turmaRepository;
    @Autowired
    private DisciplinaRepository disciplinaRepository;

    // Métodos de geração de HTML e PDF permanecem os mesmos...
    public String gerarRelatorio(RelatorioRequestDTO request) throws IOException {
        String conteudo = gerarConteudoRelatorio(request);
        Path relatoriosDir = Paths.get(System.getProperty("user.home"), "ifclass-relatorios");
        if (!Files.exists(relatoriosDir)) {
            Files.createDirectories(relatoriosDir);
        }
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String filename = "relatorio_" + request.getTipo() + "_" + timestamp + ".html";
        Path filePath = relatoriosDir.resolve(filename);
        String html = gerarHTMLCompleto(request, conteudo);
        Files.writeString(filePath, html, StandardCharsets.UTF_8);
        return String.format("Relatório gerado!\nTipo: %s\nArquivo: %s\nLocal: %s",
                getTituloRelatorio(request.getTipo()), filename, relatoriosDir);
    }

    public byte[] gerarRelatorioPDF(RelatorioRequestDTO request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph(getTituloRelatorio(request.getTipo()))
                .setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER).setMarginBottom(5));
        document.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .setFontSize(9).setItalic().setTextAlignment(TextAlignment.CENTER));
        if (request.getDataInicio() != null && request.getDataFim() != null) {
            document.add(new Paragraph("Período: " + request.getDataInicio() + " até " + request.getDataFim())
                    .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        }
        document.add(new Paragraph("\n").setFontSize(10));

        String conteudo = gerarConteudoRelatorio(request);
        List<String> linhas = Arrays.asList(conteudo.split("\n"));

        boolean inTable = false;
        Table table = null;

        for (String linha : linhas) {
            linha = linha.trim();
            if (linha.isEmpty() || linha.startsWith("====")) continue;

            if (linha.endsWith(":") && !linha.contains("-")) {
                if (inTable) { document.add(table); inTable = false; }
                document.add(new Paragraph(linha).setBold().setFontSize(12).setMarginTop(10).setMarginBottom(5));

                if (linha.toLowerCase().startsWith("detalhamento") || linha.toLowerCase().startsWith("grade horária")) {
                    inTable = true;
                    if (request.getTipo().equals("ocupacao-salas")) {
                        table = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25})).useAllAvailableWidth();
                        addTableHeader(table, "Sala", "Capacidade", "Aulas", "Ocupação");
                    } else if (request.getTipo().equals("carga-horaria")) {
                        table = new Table(UnitValue.createPercentArray(new float[]{50, 25, 25})).useAllAvailableWidth();
                        addTableHeader(table, "Professor", "Aulas", "Status");
                    } else if (request.getTipo().equals("desempenho-turmas")) {
                        table = new Table(UnitValue.createPercentArray(new float[]{50, 25, 25})).useAllAvailableWidth();
                        addTableHeader(table, "Turma", "Aulas", "Performance");
                    } else if (request.getTipo().equals("grade-horaria")) {
                        table = new Table(UnitValue.createPercentArray(new float[]{15, 30, 25, 15, 15})).useAllAvailableWidth();
                        addTableHeader(table, "Hora", "Disciplina", "Professor", "Sala", "Turma");
                    }
                }
            } else if (inTable && table != null) {
                List<String> parts = Arrays.stream(linha.split(" - ")).map(p -> p.split(": ")[p.split(": ").length - 1]).collect(Collectors.toList());
                for(String part : parts) { table.addCell(new Cell().add(new Paragraph(part).setFontSize(9))); }
            } else {
                document.add(new Paragraph(linha).setFontSize(10).setMarginLeft(10));
            }
        }

        if (inTable) { document.add(table); }
        document.close();
        return baos.toByteArray();
    }

    private void addTableHeader(Table table, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold().setFontSize(10)).setBackgroundColor(ColorConstants.LIGHT_GRAY).setTextAlignment(TextAlignment.CENTER));
        }
    }

    /**
     * Gera um arquivo .xlsx nativo usando Apache POI.
     * O arquivo DEVE ser salvo com a extensão .xlsx
     */
    public byte[] gerarRelatorioExcel(RelatorioRequestDTO requisicao) throws IOException {
        try (XSSFWorkbook pastaDeTrabalho = new XSSFWorkbook(); ByteArrayOutputStream fluxoDeSaidaBytes = new ByteArrayOutputStream()) {
            XSSFSheet aba = pastaDeTrabalho.createSheet(getTituloRelatorio(requisicao.getTipo()));

            CellStyle estiloCabecalho = pastaDeTrabalho.createCellStyle();
            Font fonte = pastaDeTrabalho.createFont();
            fonte.setBold(true);
            estiloCabecalho.setFont(fonte);

            AtomicInteger numeroLinha = new AtomicInteger(1);
            Row linhaCabecalho = aba.createRow(0);

            switch (requisicao.getTipo()) {
                case "ocupacao-salas": {
                    String[] cabecalhos = {"Sala", "Capacidade", "Aulas", "Ocupação"};
                    for (int i = 0; i < cabecalhos.length; i++) {
                        org.apache.poi.ss.usermodel.Cell celula = linhaCabecalho.createCell(i);
                        celula.setCellValue(cabecalhos[i]);
                        celula.setCellStyle(estiloCabecalho);
                    }
                    salaRepository.findAll().forEach(sala -> {
                        // --- LÓGICA DE CONTAGEM REVERTIDA ---
                        long aulasNaSala = aulaRepository.findAll().stream().filter(aula -> aula.getSala().getId().equals(sala.getId())).count();

                        Row linha = aba.createRow(numeroLinha.getAndIncrement());
                        linha.createCell(0).setCellValue(sala.getCodigo());
                        linha.createCell(1).setCellValue(sala.getCapacidade());
                        linha.createCell(2).setCellValue(aulasNaSala);

                        // --- CÁLCULO DA PORCENTAGEM CORRIGIDO ---
                        double ocupacao = 0.0;
                        if (sala.getCapacidade() > 0) {
                            ocupacao = (aulasNaSala * 100.0) / sala.getCapacidade();
                        }
                        linha.createCell(3).setCellValue(String.format("%.1f%%", ocupacao));
                    });
                    break;
                }
                case "carga-horaria": {
                    String[] cabecalhos = {"Professor", "Aulas", "Status"};
                    for (int i = 0; i < cabecalhos.length; i++) {
                        org.apache.poi.ss.usermodel.Cell celula = linhaCabecalho.createCell(i);
                        celula.setCellValue(cabecalhos[i]);
                        celula.setCellStyle(estiloCabecalho);
                    }
                    usuarioRepository.findByAuthoritiesContaining("ROLE_PROFESSOR").forEach(professor -> {
                        long aulasDoProfessor = aulaRepository.findAll().stream().filter(aula -> aula.getProfessor().getId().equals(professor.getId())).count();
                        String situacao = aulasDoProfessor < 10 ? "BAIXA" : aulasDoProfessor > 20 ? "ALTA" : "NORMAL";
                        Row linha = aba.createRow(numeroLinha.getAndIncrement());
                        linha.createCell(0).setCellValue(professor.getNome());
                        linha.createCell(1).setCellValue(aulasDoProfessor);
                        linha.createCell(2).setCellValue(situacao);
                    });
                    break;
                }
                case "desempenho-turmas": {
                    String[] cabecalhos = {"Turma", "Curso", "Ano", "Semestre", "Aulas", "Performance"};
                    for (int i = 0; i < cabecalhos.length; i++) {
                        org.apache.poi.ss.usermodel.Cell celula = linhaCabecalho.createCell(i);
                        celula.setCellValue(cabecalhos[i]);
                        celula.setCellStyle(estiloCabecalho);
                    }
                    turmaRepository.findAll().forEach(turma -> {
                        long aulasDaTurma = aulaRepository.findAll().stream().filter(aula -> aula.getTurma().getId().equals(turma.getId())).count();
                        String desempenho = aulasDaTurma < 15 ? "BAIXO" : aulasDaTurma > 25 ? "ALTO" : "MÉDIO";
                        String nomeTurma = turma.getCurso().getNome() + " " + turma.getAno() + "/" + turma.getSemestre();
                        Row linha = aba.createRow(numeroLinha.getAndIncrement());
                        linha.createCell(0).setCellValue(nomeTurma);
                        linha.createCell(1).setCellValue(turma.getCurso().getNome());
                        linha.createCell(2).setCellValue(turma.getAno());
                        linha.createCell(3).setCellValue(turma.getSemestre());
                        linha.createCell(4).setCellValue(aulasDaTurma);
                        linha.createCell(5).setCellValue(desempenho);
                    });
                    break;
                }
                case "grade-horaria": {
                    String[] cabecalhos = {"Dia", "Hora", "Disciplina", "Professor", "Sala", "Turma"};
                    for (int i = 0; i < cabecalhos.length; i++) {
                        org.apache.poi.ss.usermodel.Cell celula = linhaCabecalho.createCell(i);
                        celula.setCellValue(cabecalhos[i]);
                        celula.setCellStyle(estiloCabecalho);
                    }
                    String[] diasSemana = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
                    String[] nomesDias = {"Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira"};

                    for (int i = 0; i < diasSemana.length; i++) {
                        final String dia = diasSemana[i];
                        final String nomeDia = nomesDias[i];
                        aulaRepository.findAll().stream()
                                .filter(aula -> aula.getDiaSemana().toString().equals(dia))
                                .sorted((aula1, aula2) -> aula1.getHora().compareTo(aula2.getHora()))
                                .forEach(aula -> {
                                    Row linha = aba.createRow(numeroLinha.getAndIncrement());
                                    linha.createCell(0).setCellValue(nomeDia);
                                    linha.createCell(1).setCellValue(aula.getHora().toString());
                                    linha.createCell(2).setCellValue(aula.getDisciplina().getNome());
                                    linha.createCell(3).setCellValue(aula.getProfessor().getNome());
                                    linha.createCell(4).setCellValue(aula.getSala().getCodigo());
                                    linha.createCell(5).setCellValue(aula.getTurma().getCurso().getNome());
                                });
                    }
                    break;
                }
                default:
                    Row linha = aba.createRow(0);
                    linha.createCell(0).setCellValue("Tipo de relatório não reconhecido.");
            }

            int numeroDeColunas = aba.getRow(0).getPhysicalNumberOfCells();
            for (int i = 0; i < numeroDeColunas; i++) {
                aba.autoSizeColumn(i);
            }

            pastaDeTrabalho.write(fluxoDeSaidaBytes);
            return fluxoDeSaidaBytes.toByteArray();
        }
    }

    // =======================================================
    // MÉTODOS GERADORES DE CONTEÚDO (PARA PDF/HTML)
    // =======================================================

    private String gerarConteudoRelatorio(RelatorioRequestDTO request) {
        switch (request.getTipo()) {
            case "ocupacao-salas": return gerarRelatorioOcupacaoSalas();
            case "carga-horaria": return gerarRelatorioCargaHoraria();
            case "desempenho-turmas": return gerarRelatorioDesempenhoTurmas();
            case "grade-horaria": return gerarRelatorioGradeHoraria();
            default: return "Tipo de relatório não reconhecido.";
        }
    }

    private String gerarRelatorioOcupacaoSalas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resumo Geral:\n");
        long totalSalas = salaRepository.count();
        long totalAulas = aulaRepository.count();
        // A lógica do resumo foi revertida para o cálculo original para manter consistência
        sb.append("- Total de Salas: ").append(totalSalas).append("\n");
        sb.append("- Total de Aulas Agendadas: ").append(totalAulas).append("\n");

        // Vamos usar o cálculo corrigido para o resumo também
        long totalCapacidade = 0;
        for (Sala s : salaRepository.findAll()) { totalCapacidade += s.getCapacidade(); }
        double taxaOcupacaoMedia = 0.0;
        if (totalCapacidade > 0) {
            taxaOcupacaoMedia = (totalAulas * 100.0) / totalCapacidade;
        }
        sb.append("- Taxa de Ocupação Média: ").append(String.format("%.1f%%", taxaOcupacaoMedia)).append("\n\n");

        sb.append("Detalhamento por Sala:\n");
        salaRepository.findAll().forEach(sala -> {
            long aulasNaSala = aulaRepository.findAll().stream().filter(aula -> aula.getSala().getId().equals(sala.getId())).count();
            double ocupacao = 0.0;
            if (sala.getCapacidade() > 0) {
                ocupacao = (aulasNaSala * 100.0) / sala.getCapacidade();
            }
            sb.append("Sala: ").append(sala.getCodigo())
                    .append(" - Capacidade: ").append(sala.getCapacidade())
                    .append(" - Aulas: ").append(aulasNaSala)
                    .append(" - Ocupação: ").append(String.format("%.1f%%", ocupacao)).append("\n");
        });
        return sb.toString();
    }

    private String gerarRelatorioCargaHoraria() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resumo Geral:\n");
        long totalProfessores = usuarioRepository.countByAuthoritiesContaining("ROLE_PROFESSOR");
        long totalAulas = aulaRepository.count();
        sb.append("- Total de Professores: ").append(totalProfessores).append("\n");
        sb.append("- Total de Aulas: ").append(totalAulas).append("\n");
        if (totalProfessores > 0) {
            sb.append("- Média de Aulas por Professor: ").append(String.format("%.1f", (double) totalAulas / totalProfessores)).append("\n\n");
        }
        sb.append("Detalhamento por Professor:\n");
        usuarioRepository.findByAuthoritiesContaining("ROLE_PROFESSOR").forEach(professor -> {
            long aulasProf = aulaRepository.findAll().stream().filter(aula -> aula.getProfessor().getId().equals(professor.getId())).count();
            String status = aulasProf < 10 ? "BAIXA" : aulasProf > 20 ? "ALTA" : "NORMAL";
            sb.append("Prof. ").append(professor.getNome()).append(" - Aulas: ").append(aulasProf).append(" - Status: ").append(status).append("\n");
        });
        return sb.toString();
    }

    private String gerarRelatorioDesempenhoTurmas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resumo Geral:\n");
        long totalTurmas = turmaRepository.count();
        long totalAulas = aulaRepository.count();
        sb.append("- Total de Turmas: ").append(totalTurmas).append("\n");
        sb.append("- Total de Aulas: ").append(totalAulas).append("\n");
        if (totalTurmas > 0) {
            sb.append("- Média de Aulas por Turma: ").append(String.format("%.1f", (double) totalAulas / totalTurmas)).append("\n\n");
        }
        sb.append("Detalhamento por Turma:\n");
        turmaRepository.findAll().forEach(turma -> {
            long aulasTurma = aulaRepository.findAll().stream().filter(aula -> aula.getTurma().getId().equals(turma.getId())).count();
            String performance = aulasTurma < 15 ? "BAIXO" : aulasTurma > 25 ? "ALTO" : "MÉDIO";
            sb.append("Turma ").append(turma.getCurso().getNome()).append(" ").append(turma.getAno()).append("/").append(turma.getSemestre()).append(" - Aulas: ").append(aulasTurma).append(" - Performance: ").append(performance).append("\n");
        });
        return sb.toString();
    }

    private String gerarRelatorioGradeHoraria() {
        StringBuilder sb = new StringBuilder();
        sb.append("Grade Horária Consolidada:\n");
        String[] diasSemana = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] nomesDias = {"Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira"};

        for (int i = 0; i < diasSemana.length; i++) {
            sb.append("\n").append(nomesDias[i]).append(":\n");
            final String dia = diasSemana[i];
            aulaRepository.findAll().stream().filter(aula -> aula.getDiaSemana().toString().equals(dia)).sorted((a1, a2) -> a1.getHora().compareTo(a2.getHora())).forEach(aula -> {
                sb.append(aula.getHora()).append(" - ").append(aula.getDisciplina().getNome()).append(" - ").append(aula.getProfessor().getNome()).append(" - ").append(aula.getSala().getCodigo()).append(" - ").append(aula.getTurma().getCurso().getNome()).append("\n");
            });
        }
        return sb.toString();
    }

    private String gerarHTMLCompleto(RelatorioRequestDTO request, String conteudo) {
        String css = "<style>body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; margin: 40px; background-color: #f9f9f9; color: #333; } div.container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); } h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; } p { font-size: 12px; color: #7f8c8d; } pre { background: #ecf0f1; padding: 15px; border-radius: 5px; white-space: pre-wrap; word-wrap: break-word; font-size: 14px; line-height: 1.6; }</style>";
        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>" + getTituloRelatorio(request.getTipo()) + "</title>" + css + "</head><body><div class='container'><h1>" + getTituloRelatorio(request.getTipo()) + "</h1><p><strong>Gerado em:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "</p><pre>" + conteudo + "</pre></div></body></html>";
    }

    private String getTituloRelatorio(String tipo) {
        switch (tipo) {
            case "ocupacao-salas": return "Relatório de Ocupação de Salas";
            case "carga-horaria": return "Relatório de Carga Horária dos Professores";
            case "desempenho-turmas": return "Relatório de Desempenho por Turma";
            case "grade-horaria": return "Relatório de Grade Horária Geral";
            default: return "Relatório";
        }
    }
}