package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA: tabla departamentos.
 * Cada departamento es un área de atención en sucursal (Ventas, Soporte, Pagos, etc.).
 */
@Data
@Entity
@Table(name = "departamentos")
public class Departamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Nombre visible en el turnero (ej. "Ventas") */
    @Column(nullable = false, length = 50)
    private String nombre;

    /** Prefijo del número de turno (ej. VEN → turno VEN-001) */
    @Column(name = "codigo_prefijo", nullable = false, length = 5)
    private String codigoPrefijo;

    /** Menor número = mayor prioridad en la cola */
    @Column(name = "nivel_prioridad", nullable = false)
    private Integer nivelPrioridad;

    /** Si el departamento atiende clientes preferenciales por defecto */
    @Column(name = "es_prioritario", nullable = false)
    private Boolean esPrioritario;
}
