package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad JPA: tabla turnos.
 * Cada registro es un turno solicitado por un usuario en un departamento.
 */
@Data
@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Código mostrado en pantalla (ej. RET-001, VEN-002) */
    @Column(name = "numero_correlativo", nullable = false, length = 20)
    private String numeroCorrelativo;

    /** Cliente que solicitó el turno */
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    /** Área donde será atendido */
    @ManyToOne
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    /** Ciclo de vida del turno en la cola */
    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDIENTE','LLAMADO','ATENDIDO','CANCELADO') DEFAULT 'PENDIENTE'")
    private EstadoTurno estado = EstadoTurno.PENDIENTE;

    /** Valor numérico para ordenar la cola (menor = se atiende antes) */
    @Column(name = "prioridad_actual", nullable = false, precision = 3, scale = 1)
    private BigDecimal prioridadActual;

    /** true si el usuario marcó "atención preferencial" */
    @Column(name = "es_prioritario", nullable = false)
    private Boolean esPrioritario = false;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    /** Cuando el simulador o el operador llama el turno a ventanilla */
    @Column(name = "fecha_llamado")
    private LocalDateTime fechaLlamado;

    /** Cuando el turno ya fue atendido */
    @Column(name = "fecha_atencion")
    private LocalDateTime fechaAtencion;

    /** Asigna fecha de creación automáticamente al guardar por primera vez */
    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    /** Estados posibles de un turno */
    public enum EstadoTurno {
        PENDIENTE,  // En cola esperando
        LLAMADO,    // Llamado a ventanilla
        ATENDIDO,   // Ya fue atendido
        CANCELADO   // El usuario o el sistema lo canceló
    }
}
