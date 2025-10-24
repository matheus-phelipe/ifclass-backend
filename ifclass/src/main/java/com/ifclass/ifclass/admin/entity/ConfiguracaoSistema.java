package com.ifclass.ifclass.admin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "configuracoes_sistema")
public class ConfiguracaoSistema {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "chave", unique = true, nullable = false, length = 100)
    private String chave;
    
    @Column(name = "valor", nullable = false, length = 500)
    private String valor;
    
    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo;
    
    @Column(name = "descricao", length = 255)
    private String descricao;
    
    @Column(name = "categoria", length = 50)
    private String categoria;
    
    @Column(name = "editavel", nullable = false)
    private Boolean editavel = true;
    
    @Column(name = "valor_padrao", length = 500)
    private String valorPadrao;
    
    @Column(name = "ultima_atualizacao")
    private LocalDateTime ultimaAtualizacao;
    
    @Column(name = "usuario_atualizacao", length = 100)
    private String usuarioAtualizacao;
    
    // Construtores
    public ConfiguracaoSistema() {}
    
    public ConfiguracaoSistema(String chave, String valor, String tipo, String descricao, String categoria) {
        this.chave = chave;
        this.valor = valor;
        this.tipo = tipo;
        this.descricao = descricao;
        this.categoria = categoria;
        this.editavel = true;
        this.ultimaAtualizacao = LocalDateTime.now();
    }
    
    // Getters e Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
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
        this.ultimaAtualizacao = LocalDateTime.now();
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
    
    public Boolean getEditavel() {
        return editavel;
    }
    
    public void setEditavel(Boolean editavel) {
        this.editavel = editavel;
    }
    
    public String getValorPadrao() {
        return valorPadrao;
    }
    
    public void setValorPadrao(String valorPadrao) {
        this.valorPadrao = valorPadrao;
    }
    
    public LocalDateTime getUltimaAtualizacao() {
        return ultimaAtualizacao;
    }
    
    public void setUltimaAtualizacao(LocalDateTime ultimaAtualizacao) {
        this.ultimaAtualizacao = ultimaAtualizacao;
    }
    
    public String getUsuarioAtualizacao() {
        return usuarioAtualizacao;
    }
    
    public void setUsuarioAtualizacao(String usuarioAtualizacao) {
        this.usuarioAtualizacao = usuarioAtualizacao;
    }
    
    // Métodos utilitários
    public String getValorComoString() {
        return valor;
    }
    
    public Integer getValorComoInteger() {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Long getValorComoLong() {
        try {
            return Long.parseLong(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    public Boolean getValorComoBoolean() {
        return "true".equalsIgnoreCase(valor);
    }
    
    public Double getValorComoDouble() {
        try {
            return Double.parseDouble(valor);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
