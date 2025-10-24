package com.ifclass.ifclass.common.service;

import com.ifclass.ifclass.admin.dto.LogSistemaDTO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ExcelExportService {

    public ByteArrayInputStream logsToExcel(List<LogSistemaDTO> logs) throws IOException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet("Logs do Sistema");

            // --- Cria o Cabeçalho ---
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Timestamp", "Nível", "Categoria", "Mensagem", "Usuário", "IP", "Detalhes"};
            CellStyle headerCellStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerCellStyle.setFont(headerFont);

            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerCellStyle);
                sheet.autoSizeColumn(i); // Ajusta a largura da coluna
            }

            // --- Preenche os Dados ---
            int rowIdx = 1;
            for (LogSistemaDTO log : logs) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(log.getId());
                row.createCell(1).setCellValue(log.getTimestamp().format(formatter));
                row.createCell(2).setCellValue(log.getNivel());
                row.createCell(3).setCellValue(log.getCategoria());
                row.createCell(4).setCellValue(log.getMensagem());
                row.createCell(5).setCellValue(log.getUsuario());
                row.createCell(6).setCellValue(log.getIp());
                row.createCell(7).setCellValue(log.getDetalhes());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}