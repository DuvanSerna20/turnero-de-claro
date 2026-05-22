package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Entidad JPA: tabla planes_servicios.
 * Planes que el cliente puede comprar en la tienda (móvil, hogar, entretenimiento).
 */
@Data
@Entity
@Table(name = "planes_servicios")
public class PlanServicio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('MOVIL','HOGAR','ENTRETENIMIENTO')")
    private TipoPlan tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    public enum TipoPlan {
        MOVIL, HOGAR, ENTRETENIMIENTO
    }
}
