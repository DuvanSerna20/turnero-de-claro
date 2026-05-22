package com.example.claro.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * Entidad JPA: tabla usuarios.
 * Representa a un cliente registrado en el sistema (login, turnos, compras).
 */
@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    /** Identificador único autogenerado por MySQL */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Correo para iniciar sesión (no puede repetirse) */
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    /** Contraseña en texto plano (proyecto académico; en producción se encriptaría) */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String nombres;

    /** Cédula o documento de identidad (único) */
    @Column(unique = true, nullable = false, length = 20)
    private String documento;

    /** Número de celular (usado para notificaciones SMS/correo) */
    @Column(nullable = false, length = 20)
    private String celular;

    /** ESTANDAR, PREFERENCIAL o EMPRESARIAL */
    @Column(name = "tipo_cliente", columnDefinition = "ENUM('ESTANDAR', 'PREFERENCIAL', 'EMPRESARIAL') DEFAULT 'ESTANDAR'")
    private String tipoCliente = "ESTANDAR";
}
