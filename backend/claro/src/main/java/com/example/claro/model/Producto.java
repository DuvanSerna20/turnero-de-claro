package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "productos")
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String nombre;

    @Column(nullable = false, length = 50)
    private String marca;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "ENUM('CELULAR','COMPUTADOR','ACCESORIOS') NOT NULL")
    private TipoProducto tipo;

    @Column(columnDefinition = "TEXT")
    private String descripcion;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precio;

    public enum TipoProducto {
        CELULAR, COMPUTADOR, ACCESORIOS
    }
}
