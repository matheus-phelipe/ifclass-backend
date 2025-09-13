package com.ifclass.ifclass.aviso.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Getter @Setter @AllArgsConstructor
@Entity
public class Aviso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100, message = "O título deve ter no máximo 100 caracteres")
    private String titulo;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime dataInsercao;

    @NotBlank
    @Size(max = 500, message = "As informações devem ter no máximo 500 carácteres")
    @Column(length = 500)
    private String informacoes;

    public Aviso() {
    }

    public Aviso(String titulo, String data, String informacoes){
        this.titulo = titulo;
        this.informacoes = informacoes;
    }


}
