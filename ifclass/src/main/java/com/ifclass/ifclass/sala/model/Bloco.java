package com.ifclass.ifclass.sala.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Bloco {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Um bloco agora tem uma coleção de entidades Sala.
    // Cascade.ALL: Salvar/deletar um Bloco afeta suas Salas.
    // orphanRemoval=true: Remover uma Sala da lista a deleta do banco.
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JoinColumn(name = "idbloco") // Isso cria a coluna 'bloco_id' na tabela 'sala'.
    private List<Sala> salas = new ArrayList<>();
}