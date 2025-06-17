package com.ifclass.ifclass.configuracoes.model;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSetting {
    @Id
    private String configKey; // Chave única da configuração (ex: "nome_instituicao", "matriculas_abertas")
    private String configValue; // Valor da configuração (ex: "IFClass", "true", "2025-01-01")
    private String description; // Descrição para o admin (ex: "Nome da instituição exibido no sistema")
    private String type;        // Tipo de dado (ex: "STRING", "BOOLEAN", "NUMBER", "DATE", "TEXT_AREA", "EMAIL")
    private boolean adminOnly;  // true se apenas admins podem ver/editar (opcional, para diferenciar coord/prof)
}
