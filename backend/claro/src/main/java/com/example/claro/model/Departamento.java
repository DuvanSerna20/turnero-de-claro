package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(name = "codigo_prefijo", nullable = false, length = 5)
    private String codigoPrefijo;

    @Column(name = "nivel_prioridad", nullable = false)
    private Integer nivelPrioridad;

    @Column(name = "es_prioritario", nullable = false)
    private Boolean esPrioritario;
}
