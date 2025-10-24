package com.ifclass.ifclass.admin.dto;

import java.time.LocalDateTime;

public class ConfiguracaoSistemaDTO {
    private String chave;
    private String valor;
    private String tipo;
    private String descricao;
    private String categoria;
    private boolean editavel;
    private LocalDateTime ultimaAtualizacao;
    private String valorPadrao;

    // Construtor padrão
    public ConfiguracaoSistemaDTO() {}

    // Construtor com parâmetros
    public ConfiguracaoSistemaDTO(String chave, String valor, String tipo, String descricao, String categoria, boolean editavel) {
        this.chave = chave;
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.editavel = editavel;
        this.ultimaAtualizacao = LocalDateTime.now();
    }

    // Getters e Setters
    public String getChave() {
        return chave;
    }

    public void setChave(String chave) {
        this.chave = chave;
    }

    public String getValor() {
        return valor;
    }

    public void setValor(String valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isEditavel() {
        return editavel;
    }

    public void setEditavel(boolean editavel) {
        this.editavel = editavel;
    }

    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }

    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }

    public String getValorPadrao() {
        return valorPadrao;
    }

    public void setValorPadrao(String valorPadrao) {
        this.valorPadrao = valorPadrao;
    }
}
