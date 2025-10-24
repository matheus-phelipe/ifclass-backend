package com.ifclass.ifclass.admin.dto;

import java.time.LocalDateTime;


public class LogSistemaDTO {
    private Long id;
    private LocalDateTime timestamp;
    private String nivel; // INFO, WARN, ERROR, DEBUG
    private String categoria; // AUTH, CRUD, SYSTEM, API
    private String mensagem;
    private String usuario;
    private String ip;
    private String detalhes;


    public LogSistemaDTO() {}

    public LogSistemaDTO(Long id, LocalDateTime timestamp, String nivel, String categoria, String mensagem, String usuario, String ip, String detalhes) {
        this.id = id;
        this.timestamp = timestamp;
        this.nivel = nivel;
        this.categoria = categoria;
        this.mensagem = mensagem;
        this.usuario = usuario;
        this.ip = ip;
        this.detalhes = detalhes;
    }

    public Long getId() { return id; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public String getNivel() { return nivel; }
    public String getCategoria() { return categoria; }
    public String getMensagem() { return mensagem; }
    public String getUsuario() { return usuario; }
    public String getIp() { return ip; }
    public String getDetalhes() { return detalhes; }
}