package com.ifclass.ifclass.sala.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer posX;       // Posição X (coordenada horizontal)
    private Integer posY;       // Posição Y (coordenada vertical)
    private Integer largura;    // Largura da sala
    private Integer altura;

    private Integer capacidade;
    private String  codigo;
    private String cor;

    // Cria a relação de volta para o Bloco. Uma Sala pertence a um Bloco.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idbloco", nullable = false) // Define a coluna de chave estrangeira
    @JsonBackReference // Evita loops infinitos ao converter para JSON
    private Bloco bloco;

}