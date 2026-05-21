package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.math.BigDecimal;

@Data
@Entity
@Table(name = "compras")
public class Compra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_item", columnDefinition = "ENUM('PLAN','PRODUCTO') NOT NULL")
    private TipoItem tipoItem;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "fecha_compra", updatable = false)
    private LocalDateTime fechaCompra;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago = "Tarjeta de Crédito";

    @Column(name = "precio_pagado", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioPagado;

    @PrePersist
    protected void onCreate() {
        if (fechaCompra == null) {
            fechaCompra = LocalDateTime.now();
        }
    }

    public enum TipoItem {
        PLAN, PRODUCTO
    }
}
