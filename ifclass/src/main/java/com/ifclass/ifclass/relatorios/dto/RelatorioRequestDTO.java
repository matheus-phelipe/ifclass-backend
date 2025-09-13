package com.ifclass.ifclass.relatorios.dto;

import java.time.LocalDate;

public class RelatorioRequestDTO {
    private String tipo;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    private String formato; // "pdf", "excel", "html"

    public RelatorioRequestDTO() {}

    public RelatorioRequestDTO(String tipo, LocalDate dataInicio, LocalDate dataFim, String formato) {
        this.tipo = tipo;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.formato = formato;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(LocalDate dataInicio) {
        this.dataInicio = dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public void setDataFim(LocalDate dataFim) {
        this.dataFim = dataFim;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }
}
