package com.ifclass.ifclass.relatorios.service;

import com.ifclass.ifclass.aula.repository.AulaRepository;
import com.ifclass.ifclass.disciplina.repository.DisciplinaRepository;
import com.ifclass.ifclass.relatorios.dto.RelatorioRequestDTO;
import com.ifclass.ifclass.sala.repository.SalaRepository;
import com.ifclass.ifclass.turma.repository.TurmaRepository;
import com.ifclass.ifclass.usuario.repository.UsuarioRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

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

    public String gerarRelatorio(RelatorioRequestDTO request) throws IOException {
        String conteudo = gerarConteudoRelatorio(request);
        
        // Salvar relatório em arquivo
        String userHome = System.getProperty("user.home");
        Path relatoriosDir = Paths.get(userHome, "ifclass-relatorios");
        
        if (!Files.exists(relatoriosDir)) {
            Files.createDirectories(relatoriosDir);
        }
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        String timestamp = LocalDateTime.now().format(formatter);
        String filename = "relatorio_" + request.getTipo() + "_" + timestamp + ".html";
        Path relatorioFile = relatoriosDir.resolve(filename);
        
        String htmlCompleto = gerarHTMLCompleto(request, conteudo);
        Files.write(relatorioFile, htmlCompleto.getBytes());
        
        return String.format("Relatório gerado com sucesso!\n\nTipo: %s\nArquivo: %s\nLocalização: %s", 
            getTituloRelatorio(request.getTipo()), filename, relatoriosDir.toString());
    }

    public byte[] gerarRelatorioPDF(RelatorioRequestDTO request) throws IOException {
        // Gerar PDF simples como texto formatado
        StringBuilder pdfContent = new StringBuilder();

        // Cabeçalho
        pdfContent.append("%PDF-1.4\n");
        pdfContent.append("1 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Catalog\n");
        pdfContent.append("/Pages 2 0 R\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");

        // Páginas
        pdfContent.append("2 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Pages\n");
        pdfContent.append("/Kids [3 0 R]\n");
        pdfContent.append("/Count 1\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");

        // Página
        pdfContent.append("3 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Page\n");
        pdfContent.append("/Parent 2 0 R\n");
        pdfContent.append("/MediaBox [0 0 612 792]\n");
        pdfContent.append("/Contents 4 0 R\n");
        pdfContent.append("/Resources <<\n");
        pdfContent.append("/Font <<\n");
        pdfContent.append("/F1 5 0 R\n");
        pdfContent.append(">>\n");
        pdfContent.append(">>\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");

        // Conteúdo
        String conteudo = gerarConteudoRelatorio(request);
        String titulo = getTituloRelatorio(request.getTipo());
        String dataGeracao = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

        String streamContent = "BT\n" +
            "/F1 16 Tf\n" +
            "50 750 Td\n" +
            "(" + titulo + ") Tj\n" +
            "0 -20 Td\n" +
            "/F1 10 Tf\n" +
            "(Gerado em: " + dataGeracao + ") Tj\n" +
            "0 -30 Td\n" +
            "/F1 8 Tf\n";

        // Adicionar conteúdo linha por linha
        String[] linhas = conteudo.split("\n");
        for (int i = 0; i < Math.min(linhas.length, 40); i++) { // Limitar a 40 linhas
            String linha = linhas[i].replace("(", "\\(").replace(")", "\\)");
            if (linha.length() > 80) {
                linha = linha.substring(0, 80) + "...";
            }
            streamContent += "(" + linha + ") Tj\n0 -12 Td\n";
        }

        streamContent += "ET\n";

        pdfContent.append("4 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Length " + streamContent.length() + "\n");
        pdfContent.append(">>\n");
        pdfContent.append("stream\n");
        pdfContent.append(streamContent);
        pdfContent.append("endstream\n");
        pdfContent.append("endobj\n\n");

        // Font
        pdfContent.append("5 0 obj\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Type /Font\n");
        pdfContent.append("/Subtype /Type1\n");
        pdfContent.append("/BaseFont /Helvetica\n");
        pdfContent.append(">>\n");
        pdfContent.append("endobj\n\n");

        // xref
        pdfContent.append("xref\n");
        pdfContent.append("0 6\n");
        pdfContent.append("0000000000 65535 f \n");
        pdfContent.append("0000000009 65535 n \n");
        pdfContent.append("0000000074 65535 n \n");
        pdfContent.append("0000000120 65535 n \n");
        pdfContent.append("0000000179 65535 n \n");
        pdfContent.append("0000000364 65535 n \n");

        pdfContent.append("trailer\n");
        pdfContent.append("<<\n");
        pdfContent.append("/Size 6\n");
        pdfContent.append("/Root 1 0 R\n");
        pdfContent.append(">>\n");
        pdfContent.append("startxref\n");
        pdfContent.append("492\n");
        pdfContent.append("%%EOF\n");

        return pdfContent.toString().getBytes();
    }

    public byte[] gerarRelatorioExcel(RelatorioRequestDTO request) throws IOException {
        // Gerar CSV que pode ser aberto no Excel
        StringBuilder csvContent = new StringBuilder();

        // Cabeçalho
        csvContent.append(getTituloRelatorio(request.getTipo())).append("\n");
        csvContent.append("Gerado em: ").append(
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
        ).append("\n");

        if (request.getDataInicio() != null && request.getDataFim() != null) {
            csvContent.append("Período: ").append(request.getDataInicio())
                     .append(" até ").append(request.getDataFim()).append("\n");
        }

        csvContent.append("\n");

        // Conteúdo do relatório
        String conteudo = gerarConteudoRelatorio(request);
        String[] linhas = conteudo.split("\n");

        for (String linha : linhas) {
            if (linha.trim().isEmpty() || linha.contains("=====")) {
                continue;
            }

            // Escapar aspas e vírgulas para CSV
            String linhaEscapada = linha.replace("\"", "\"\"");
            if (linhaEscapada.contains(",") || linhaEscapada.contains("\"") || linhaEscapada.contains("\n")) {
                linhaEscapada = "\"" + linhaEscapada + "\"";
            }

            csvContent.append(linhaEscapada).append("\n");
        }

        return csvContent.toString().getBytes("UTF-8");
    }

    private String gerarConteudoRelatorio(RelatorioRequestDTO request) {
        switch (request.getTipo()) {
            case "ocupacao-salas":
                return gerarRelatorioOcupacaoSalas(request);
            case "carga-horaria":
                return gerarRelatorioCargaHoraria(request);
            case "desempenho-turmas":
                return gerarRelatorioDesempenhoTurmas(request);
            case "grade-horaria":
                return gerarRelatorioGradeHoraria(request);
            case "analytics-dashboard":
                return gerarRelatorioAnalyticsDashboard(request);
            case "analytics-export":
                return gerarRelatorioAnalyticsExport(request);
            default:
                return "Tipo de relatório não reconhecido: " + request.getTipo();
        }
    }

    private String gerarRelatorioOcupacaoSalas(RelatorioRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO DE OCUPAÇÃO DE SALAS\n");
        sb.append("=====================================\n\n");
        
        long totalSalas = salaRepository.count();
        long totalAulas = aulaRepository.count();
        
        sb.append("Resumo Geral:\n");
        sb.append("- Total de Salas: ").append(totalSalas).append("\n");
        sb.append("- Total de Aulas Agendadas: ").append(totalAulas).append("\n");
        sb.append("- Taxa de Ocupação Média: ").append(String.format("%.1f%%", (totalAulas * 100.0 / (totalSalas * 25)))).append("\n\n");
        
        sb.append("Detalhamento por Sala:\n");
        sb.append("----------------------\n");
        
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

    private String gerarRelatorioCargaHoraria(RelatorioRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO DE CARGA HORÁRIA DOS PROFESSORES\n");
        sb.append("==========================================\n\n");
        
        long totalProfessores = usuarioRepository.countByAuthoritiesContaining("ROLE_PROFESSOR");
        long totalAulas = aulaRepository.count();
        
        sb.append("Resumo Geral:\n");
        sb.append("- Total de Professores: ").append(totalProfessores).append("\n");
        sb.append("- Total de Aulas: ").append(totalAulas).append("\n");
        sb.append("- Média de Aulas por Professor: ").append(String.format("%.1f", (double)totalAulas / totalProfessores)).append("\n\n");
        
        sb.append("Detalhamento por Professor:\n");
        sb.append("---------------------------\n");
        
        usuarioRepository.findAll().stream()
            .filter(usuario -> usuario.getAuthorities().contains("ROLE_PROFESSOR"))
            .forEach(professor -> {
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

    private String gerarRelatorioDesempenhoTurmas(RelatorioRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO DE DESEMPENHO POR TURMA\n");
        sb.append("==================================\n\n");
        
        long totalTurmas = turmaRepository.count();
        long totalAulas = aulaRepository.count();
        
        sb.append("Resumo Geral:\n");
        sb.append("- Total de Turmas: ").append(totalTurmas).append("\n");
        sb.append("- Total de Aulas: ").append(totalAulas).append("\n");
        sb.append("- Média de Aulas por Turma: ").append(String.format("%.1f", (double)totalAulas / totalTurmas)).append("\n\n");
        
        sb.append("Detalhamento por Turma:\n");
        sb.append("-----------------------\n");
        
        turmaRepository.findAll().forEach(turma -> {
            long aulasTurma = aulaRepository.findAll().stream()
                .filter(aula -> aula.getTurma().getId().equals(turma.getId()))
                .count();
            
            String performance = aulasTurma < 15 ? "BAIXO" : aulasTurma > 25 ? "ALTO" : "MÉDIO";
            
            sb.append("Turma ").append(turma.getCurso().getNome())
              .append(" ").append(turma.getAno()).append("/").append(turma.getSemestre())
              .append(" - Aulas: ").append(aulasTurma)
              .append(" - Performance: ").append(performance)
              .append("\n");
        });
        
        return sb.toString();
    }

    private String gerarRelatorioGradeHoraria(RelatorioRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO DE GRADE HORÁRIA GERAL\n");
        sb.append("================================\n\n");
        
        sb.append("Grade Horária Consolidada:\n");
        sb.append("--------------------------\n");
        
        String[] diasSemana = {"MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY"};
        String[] nomesDias = {"Segunda-feira", "Terça-feira", "Quarta-feira", "Quinta-feira", "Sexta-feira"};
        
        for (int i = 0; i < diasSemana.length; i++) {
            sb.append("\n").append(nomesDias[i]).append(":\n");
            sb.append("----------\n");
            
            final String dia = diasSemana[i];
            aulaRepository.findAll().stream()
                .filter(aula -> aula.getDiaSemana().toString().equals(dia))
                .sorted((a1, a2) -> a1.getHora().compareTo(a2.getHora()))
                .forEach(aula -> {
                    sb.append(aula.getHora())
                      .append(" - ").append(aula.getDisciplina().getNome())
                      .append(" - Prof. ").append(aula.getProfessor().getNome())
                      .append(" - Sala ").append(aula.getSala().getCodigo())
                      .append(" - Turma ").append(aula.getTurma().getCurso().getNome())
                      .append("\n");
                });
        }
        
        return sb.toString();
    }

    private String gerarHTMLCompleto(RelatorioRequestDTO request, String conteudo) {
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset='UTF-8'>\n" +
               "    <title>" + getTituloRelatorio(request.getTipo()) + "</title>\n" +
               "    <style>\n" +
               "        body { font-family: Arial, sans-serif; margin: 20px; }\n" +
               "        h1 { color: #2c3e50; }\n" +
               "        pre { background: #f8f9fa; padding: 15px; border-radius: 5px; }\n" +
               "    </style>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <h1>" + getTituloRelatorio(request.getTipo()) + "</h1>\n" +
               "    <p><strong>Gerado em:</strong> " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "</p>\n" +
               "    <p><strong>Período:</strong> " + (request.getDataInicio() != null ? request.getDataInicio() : "N/A") + 
               " até " + (request.getDataFim() != null ? request.getDataFim() : "N/A") + "</p>\n" +
               "    <hr>\n" +
               "    <pre>" + conteudo + "</pre>\n" +
               "</body>\n" +
               "</html>";
    }

    private String gerarRelatorioAnalyticsDashboard(RelatorioRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append("RELATÓRIO ANALYTICS DASHBOARD\n");
        sb.append("============================\n\n");

        // Estatísticas gerais
        long totalUsuarios = usuarioRepository.count();
        long totalProfessores = usuarioRepository.countByAuthoritiesContaining("ROLE_PROFESSOR");
        long totalAulas = aulaRepository.count();
        long totalSalas = salaRepository.count();

        sb.append("📊 RESUMO EXECUTIVO\n");
        sb.append("-------------------\n");
        sb.append("Total de Usuários: ").append(totalUsuarios).append("\n");
        sb.append("Total de Professores: ").append(totalProfessores).append("\n");
        sb.append("Total de Aulas: ").append(totalAulas).append("\n");
        sb.append("Total de Salas: ").append(totalSalas).append("\n\n");

        // Análise de utilização
        sb.append("📈 ANÁLISE DE UTILIZAÇÃO\n");
        sb.append("------------------------\n");

        // Salas mais utilizadas
        sb.append("Salas Mais Utilizadas:\n");
        salaRepository.findAll().forEach(sala -> {
            long aulasSala = aulaRepository.findAll().stream()
                .filter(aula -> aula.getSala().getId().equals(sala.getId()))
                .count();
            if (aulasSala > 0) {
                sb.append("- ").append(sala.getCodigo())
                  .append(": ").append(aulasSala).append(" aulas\n");
            }
        });

        sb.append("\n");

        // Professores mais ativos
        sb.append("Professores Mais Ativos:\n");
        usuarioRepository.findAll().stream()
            .filter(usuario -> usuario.getAuthorities().contains("ROLE_PROFESSOR"))
            .forEach(professor -> {
                long aulasProf = aulaRepository.findAll().stream()
                    .filter(aula -> aula.getProfessor().getId().equals(professor.getId()))
                    .count();
                if (aulasProf > 0) {
                    sb.append("- ").append(professor.getNome())
                      .append(": ").append(aulasProf).append(" aulas\n");
                }
            });

        sb.append("\n");

        // Insights e recomendações
        sb.append("💡 INSIGHTS E RECOMENDAÇÕES\n");
        sb.append("---------------------------\n");

        double utilizacaoSalas = totalSalas > 0 ? (double) totalAulas / totalSalas : 0;
        sb.append("- Taxa de utilização média das salas: ").append(String.format("%.1f", utilizacaoSalas)).append(" aulas/sala\n");

        if (totalProfessores > 0) {
            double mediaAulasProf = (double) totalAulas / totalProfessores;
            sb.append("- Média de aulas por professor: ").append(String.format("%.1f", mediaAulasProf)).append(" aulas\n");
        }

        sb.append("- Sistema operando com ").append(totalUsuarios).append(" usuários ativos\n");
        sb.append("- Recomenda-se monitorar a distribuição de carga horária entre professores\n");

        return sb.toString();
    }

    private String gerarRelatorioAnalyticsExport(RelatorioRequestDTO request) {
        StringBuilder sb = new StringBuilder();
        sb.append("EXPORTAÇÃO ANALYTICS - DADOS DETALHADOS\n");
        sb.append("======================================\n\n");

        // Dados para exportação em formato estruturado
        sb.append("DADOS ESTRUTURADOS PARA ANÁLISE\n");
        sb.append("-------------------------------\n");

        // Cabeçalhos CSV-like para facilitar importação
        sb.append("AULAS_POR_PROFESSOR:\n");
        sb.append("Professor,Total_Aulas,Status_Carga\n");

        usuarioRepository.findAll().stream()
            .filter(usuario -> usuario.getAuthorities().contains("ROLE_PROFESSOR"))
            .forEach(professor -> {
                long aulasProf = aulaRepository.findAll().stream()
                    .filter(aula -> aula.getProfessor().getId().equals(professor.getId()))
                    .count();

                String status = aulasProf < 10 ? "BAIXA" : aulasProf > 20 ? "ALTA" : "NORMAL";

                sb.append(professor.getNome().replace(",", ";"))
                  .append(",").append(aulasProf)
                  .append(",").append(status)
                  .append("\n");
            });

        sb.append("\nUSO_SALAS:\n");
        sb.append("Codigo,Total_Aulas,Bloco\n");

        salaRepository.findAll().forEach(sala -> {
            long aulasSala = aulaRepository.findAll().stream()
                .filter(aula -> aula.getSala().getId().equals(sala.getId()))
                .count();

            sb.append(sala.getCodigo())
              .append(",").append(aulasSala)
              .append(",").append(sala.getBloco().getNome().replace(",", ";"))
              .append("\n");
        });

        return sb.toString();
    }

    private String getTituloRelatorio(String tipo) {
        switch (tipo) {
            case "ocupacao-salas": return "Relatório de Ocupação de Salas";
            case "carga-horaria": return "Relatório de Carga Horária dos Professores";
            case "desempenho-turmas": return "Relatório de Desempenho por Turma";
            case "grade-horaria": return "Relatório de Grade Horária Geral";
            case "analytics-dashboard": return "Relatório Analytics Dashboard";
            case "analytics-export": return "Exportação de Dados Analytics";
            default: return "Relatório";
        }
    }
}
