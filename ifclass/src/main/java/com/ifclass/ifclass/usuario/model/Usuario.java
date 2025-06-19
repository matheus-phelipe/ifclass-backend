package com.ifclass.ifclass.usuario.model;

import jakarta.persistence.*;
import lombok.*;
import com.ifclass.ifclass.disciplina.model.Disciplina;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String senha;
    private String prontuario;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "usuario_authorities", joinColumns = @JoinColumn(name = "usuario_id"))
    @Column(name = "authority")
    private List<String> authorities;

    @ManyToMany
    @JoinTable(
        name = "professor_disciplina",
        joinColumns = @JoinColumn(name = "professor_id"),
        inverseJoinColumns = @JoinColumn(name = "disciplina_id")
    )
    private Set<Disciplina> disciplinas;
}