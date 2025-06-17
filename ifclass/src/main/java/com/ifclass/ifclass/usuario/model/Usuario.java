package com.ifclass.ifclass.usuario.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

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
    private List<String> authorities;}