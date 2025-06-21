package com.ifclass.ifclass.aula.model;

import com.ifclass.ifclass.sala.model.Sala;
import com.ifclass.ifclass.turma.model.Turma;
import com.ifclass.ifclass.disciplina.model.Disciplina;
import com.ifclass.ifclass.usuario.model.Usuario;
import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Entity
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Sala sala;

    @ManyToOne(optional = false)
    private Turma turma;

    @ManyToOne(optional = false)
    private Disciplina disciplina;

    @ManyToOne(optional = false)
    private Usuario professor;

    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;

    private LocalTime hora;
} 