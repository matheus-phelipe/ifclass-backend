package com.ifclass.ifclass.sala.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    @OneToMany(mappedBy = "bloco", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    @JsonManagedReference
    private List<Sala> salas = new ArrayList<>();

    /**
     * Adiciona uma sala a este bloco, garantindo a consistência
     * do relacionamento bidirecional.
     * @param sala A sala a ser adicionada.
     */
    public void addSala(Sala sala) {
        this.salas.add(sala);
        sala.setBloco(this);
    }

    /**
     * Remove uma sala deste bloco, garantindo a consistência
     * do relacionamento bidirecional.
     * @param sala A sala a ser removida.
     */
    public void removeSala(Sala sala) {
        this.salas.remove(sala);
        sala.setBloco(null);
    }
}