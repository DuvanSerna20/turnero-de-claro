package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;

//get and set
@Data
@Entity // representa la tabla de la base de datos
@Table(name = "usuarios")
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 150)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(unique = true, nullable = false, length = 20)
    private String documento;

    @Column(nullable = false, length = 20)
    private String celular;

    @Column(name = "tipo_cliente", columnDefinition = "ENUM('ESTANDAR', 'PREFERENCIAL', 'EMPRESARIAL') DEFAULT 'ESTANDAR'")
    private String tipoCliente = "ESTANDAR";
}
