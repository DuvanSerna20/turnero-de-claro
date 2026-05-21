package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "turnos")
public class Turno {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_correlativo", nullable = false, length = 20)
    private String numeroCorrelativo;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "departamento_id", nullable = false)
    private Departamento departamento;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('PENDIENTE','LLAMADO','ATENDIDO','CANCELADO') DEFAULT 'PENDIENTE'")
    private EstadoTurno estado = EstadoTurno.PENDIENTE;

    @Column(name = "prioridad_actual", nullable = false, precision = 3, scale = 1)
    private BigDecimal prioridadActual;

    @Column(name = "fecha_creacion", updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "fecha_llamado")
    private LocalDateTime fechaLlamado;

    @Column(name = "fecha_atencion")
    private LocalDateTime fechaAtencion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
    }

    public enum EstadoTurno {
        PENDIENTE, LLAMADO, ATENDIDO, CANCELADO
    }
}
