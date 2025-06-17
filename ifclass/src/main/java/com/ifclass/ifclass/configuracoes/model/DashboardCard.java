package com.ifclass.ifclass.configuracoes.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class DashboardCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String icon;    // Ícone do card (ex: "bi bi-calendar-week")
    private String title;   // Título do card (ex: "Aulas do dia")
    private String text;    // Texto descritivo do card (ex: "Confira quais aulas você tem hoje.")
    private String link;    // Rota Angular associada (ex: "/aulas")
    private String roles;   // Papéis que podem ver o card (ex: "ROLE_ALUNO,ROLE_PROFESSOR" - separados por vírgula)
    private boolean enabled; // Se o card está ativo/visível (true/false)
    private int cardOrder; // Ordem de exibição do card no dashboard
}
