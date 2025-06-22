package com.ifclass.ifclass.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogSistemaDTO {
    private Long id;
    private LocalDateTime timestamp;
    private String nivel; // INFO, WARN, ERROR, DEBUG
    private String categoria; // AUTH, CRUD, SYSTEM, API
    private String mensagem;
    private String usuario;
    private String ip;
    private String detalhes;
}
