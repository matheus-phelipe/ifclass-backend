package com.ifclass.ifclass.relatorios.service;

import com.ifclass.ifclass.aula.repository.AulaRepository;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.relatorios.dto.RelatorioRequestDTO;
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

    /**
     * Gera relatório HTML e salva no disco.
     */
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

    /**
     * Gera PDF formatado usando iText 7, com layout aprimorado.
     */
    public byte[] gerarRelatorioPDF(RelatorioRequestDTO request) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Cabeçalho Padrão
        document.add(new Paragraph(getTituloRelatorio(request.getTipo()))
                .setBold().setFontSize(18).setTextAlignment(TextAlignment.CENTER).setMarginBottom(5));
        document.add(new Paragraph("Gerado em: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
                .setFontSize(9).setItalic().setTextAlignment(TextAlignment.CENTER));
        if (request.getDataInicio() != null && request.getDataFim() != null) {
            document.add(new Paragraph("Período: " + request.getDataInicio() + " até " + request.getDataFim())
                    .setFontSize(9).setTextAlignment(TextAlignment.CENTER));
        }
        document.add(new Paragraph("\n").setFontSize(10));

        // Conteúdo do Relatório
        String conteudo = gerarConteudoRelatorio(request);
        List<String> linhas = Arrays.asList(conteudo.split("\n"));

        boolean inTable = false;
        Table table = null;

        for (String linha : linhas) {
            linha = linha.trim();
            if (linha.isEmpty() || linha.startsWith("====")) continue;

            // Detecta títulos de seção e os formata
            if (linha.endsWith(":") && !linha.contains("-")) {
                if (inTable) { // Adiciona a tabela anterior antes de começar uma nova seção
                    document.add(table);
                    inTable = false;
                }
                document.add(new Paragraph(linha)
                        .setBold().setFontSize(12).setMarginTop(10).setMarginBottom(5));

                // Prepara a tabela para a seção de detalhamento
                if (linha.toLowerCase().startsWith("detalhamento") || linha.toLowerCase().startsWith("grade horária") || linha.toLowerCase().contains("ativos")) {
                    inTable = true;
                    // Define os cabeçalhos da tabela com base no tipo de relatório
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
                // Adiciona linhas de dados à tabela
                List<String> parts = Arrays.stream(linha.split(" - "))
                        .map(p -> p.split(": ")[p.split(": ").length - 1])
                        .collect(Collectors.toList());
                for(String part : parts) {
                    table.addCell(new Cell().add(new Paragraph(part).setFontSize(9)));
                }
            } else {
                // Adiciona linhas de resumo como parágrafos normais
                document.add(new Paragraph(linha).setFontSize(10).setMarginLeft(10));
            }
        }

        if (inTable) { // Adiciona a última tabela se o relatório terminar com ela
            document.add(table);
        }

        document.close();
        return baos.toByteArray();
    }

    // Helper para adicionar cabeçalhos à tabela
    private void addTableHeader(Table table, String... headers) {
        for (String header : headers) {
            table.addHeaderCell(new Cell().add(new Paragraph(header).setBold().setFontSize(10))
                    .setBackgroundColor(ColorConstants.LIGHT_GRAY)
                    .setTextAlignment(TextAlignment.CENTER));
        }
    }

    /**
     * Gera CSV para abrir no Excel.
     */
    public byte[] gerarRelatorioExcel(RelatorioRequestDTO request) {
        // Implementação original mantida
        StringBuilder csv = new StringBuilder();
        csv.append(getTituloRelatorio(request.getTipo())).append("\n");
        csv.append("Gerado em: ").append(LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))).append("\n");

        if (request.getDataInicio() != null && request.getDataFim() != null) {
            csv.append("Período: ").append(request.getDataInicio())
                    .append(" até ").append(request.getDataFim()).append("\n");
        }
        csv.append("\n");

        String conteudo = gerarConteudoRelatorio(request);
        for (String linha : conteudo.split("\n")) {
            if (!linha.trim().isEmpty()) {
                String linhaEscapada = linha.replace("\"", "\"\"");
                csv.append("\"").append(linhaEscapada).append("\"\n");
            }
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * Gera o conteúdo bruto do relatório com base no tipo.
     */
    private String gerarConteudoRelatorio(RelatorioRequestDTO request) {
        switch (request.getTipo()) {
            case "ocupacao-salas": return gerarRelatorioOcupacaoSalas();
            case "carga-horaria": return gerarRelatorioCargaHoraria();
            case "desempenho-turmas": return gerarRelatorioDesempenhoTurmas();
            case "grade-horaria": return gerarRelatorioGradeHoraria();
            default: return "Tipo de relatório não reconhecido.";
        }
    }

    // =============== RELATÓRIOS ESPECÍFICOS (COM DETALHAMENTO) ===============

    private String gerarRelatorioOcupacaoSalas() {
        StringBuilder sb = new StringBuilder();
        sb.append("Resumo Geral:\n");
        long totalSalas = salaRepository.count();
        long totalAulas = aulaRepository.count();
        sb.append("- Total de Salas: ").append(totalSalas).append("\n");
        sb.append("- Total de Aulas Agendadas: ").append(totalAulas).append("\n");
        sb.append("- Taxa de Ocupação Média: ").append(String.format("%.1f%%", (totalAulas * 100.0 / (totalSalas * 25)))).append("\n\n");
        sb.append("Detalhamento por Sala:\n");
        salaRepository.findAll().forEach(sala -> {
            long aulasNaSala = aulaRepository.findAll().stream()
                    .filter(aula -> aula.getSala().getId().equals(sala.getId()))
                    .count();
            sb.append("Sala ").append(sala.getCodigo())
                    .append(" - Capacidade: ").append(sala.getCapacidade())
                    .append(" - Aulas: ").append(aulasNaSala)
                    .append(" - Ocupação: ").append(String.format("%.1f%%", (aulasNaSala * 100.0 / 25)))
                    .append("\n");
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
            long aulasProf = aulaRepository.findAll().stream()
                    .filter(aula -> aula.getProfessor().getId().equals(professor.getId()))
                    .count();
            String status = aulasProf < 10 ? "BAIXA" : aulasProf > 20 ? "ALTA" : "NORMAL";
            sb.append("Prof. ").append(professor.getNome())
                    .append(" - Aulas: ").append(aulasProf)
                    .append(" - Status: ").append(status)
                    .append("\n");
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
            long aulasTurma = aulaRepository.findAll().stream()
                    .filter(aula -> aula.getTurma().getId().equals(turma.getId()))
                    .count();
            String performance = aulasTurma < 15 ? "BAIXO" : aulasTurma > 25 ? "ALTO" : "MÉDIO";
            sb.append("Turma ").append(turma.getCurso().getNome()).append(" ").append(turma.getAno()).append("/").append(turma.getSemestre())
                    .append(" - Aulas: ").append(aulasTurma)
                    .append(" - Performance: ").append(performance)
                    .append("\n");
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
            aulaRepository.findAll().stream()
                    .filter(aula -> aula.getDiaSemana().toString().equals(dia))
                    .sorted((a1, a2) -> a1.getHora().compareTo(a2.getHora()))
                    .forEach(aula -> {
                        sb.append(aula.getHora())
                                .append(" - ").append(aula.getDisciplina().getNome())
                                .append(" - ").append(aula.getProfessor().getNome())
                                .append(" - ").append(aula.getSala().getCodigo())
                                .append(" - ").append(aula.getTurma().getCurso().getNome())
                                .append("\n");
                    });
        }
        return sb.toString();
    }

    // =======================================================

    private String gerarHTMLCompleto(RelatorioRequestDTO request, String conteudo) {
        // CSS aprimorado para um visual mais limpo e moderno
        String css = "<style>"
                + "body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; margin: 40px; background-color: #f9f9f9; color: #333; }"
                + "div.container { background-color: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 4px 8px rgba(0,0,0,0.1); }"
                + "h1 { color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px; }"
                + "p { font-size: 12px; color: #7f8c8d; }"
                + "pre { background: #ecf0f1; padding: 15px; border-radius: 5px; white-space: pre-wrap; word-wrap: break-word; font-size: 14px; line-height: 1.6; }"
                + "</style>";

        return "<!DOCTYPE html><html><head><meta charset='UTF-8'><title>" + getTituloRelatorio(request.getTipo()) + "</title>" + css + "</head>"
                + "<body><div class='container'><h1>" + getTituloRelatorio(request.getTipo()) + "</h1>"
                + "<p><strong>Gerado em:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "</p>"
                + "<pre>" + conteudo + "</pre></div></body></html>";
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
