CREATE DATABASE IF NOT EXISTS turnero_claro;
USE turnero_claro;

-- ==========================================
-- MODULO 1: USUARIOS
-- ==========================================

CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(150) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    documento VARCHAR(20) UNIQUE NOT NULL,
    celular VARCHAR(20) NOT NULL, -- Requisito para envío de SMS
    tipo_cliente ENUM('ESTANDAR', 'PREFERENCIAL', 'EMPRESARIAL') DEFAULT 'ESTANDAR'
);

-- ==========================================
-- MÓDULO 2: TIENDA (Planes y Tecnología)
-- ==========================================

CREATE TABLE planes_servicios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    tipo ENUM('MOVIL', 'HOGAR', 'ENTRETENIMIENTO') NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL
);

CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(150) NOT NULL,    -- Ej: 'iPhone 15 Pro Max'
    marca VARCHAR(50) NOT NULL,      -- Ej: 'Apple'
    tipo ENUM('CELULAR', 'COMPUTADOR', 'ACCESORIOS') NOT NULL,
    descripcion TEXT,
    precio DECIMAL(10,2) NOT NULL
);

-- Tabla unificada de compras (Soporta Planes y Dispositivos)
CREATE TABLE compras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL,
    tipo_item ENUM('PLAN', 'PRODUCTO') NOT NULL, 
    item_id BIGINT NOT NULL, -- Apunta al ID de 'planes_servicios' u 'productos' desde el código
    fecha_compra TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    metodo_pago VARCHAR(50) DEFAULT 'Tarjeta de Crédito',
    precio_pagado DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);


CREATE TABLE departamentos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    codigo_prefijo VARCHAR(5) NOT NULL, 
    nivel_prioridad INT NOT NULL, 
    es_prioritario BOOLEAN NOT NULL 
);

CREATE TABLE turnos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero_correlativo VARCHAR(20) NOT NULL, 
    usuario_id BIGINT NOT NULL, 
    departamento_id BIGINT NOT NULL,
    estado ENUM('PENDIENTE', 'LLAMADO', 'ATENDIDO', 'CANCELADO') DEFAULT 'PENDIENTE',
    prioridad_actual DECIMAL(3,1) NOT NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_llamado TIMESTAMP NULL,
    fecha_atencion TIMESTAMP NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (departamento_id) REFERENCES departamentos(id)
);



INSERT INTO departamentos (nombre, codigo_prefijo, nivel_prioridad, es_prioritario) VALUES 
('Retenciones', 'RET', 1, TRUE),
('Soporte técnico', 'SOP', 2, TRUE),
('Ventas', 'VEN', 3, TRUE),
('Pagos', 'PAG', 4, FALSE);

INSERT INTO planes_servicios (nombre, tipo, descripcion, precio) VALUES 
('Plan Postpago Ilimitado', 'MOVIL', 'Datos y minutos ilimitados', 60000.00),
('Internet Fibra Óptica 500MB', 'HOGAR', 'Internet hogar de alta velocidad', 80000.00),
('Claro TV + Netflix', 'ENTRETENIMIENTO', 'Televisión digital más suscripción Netflix', 95000.00);

INSERT INTO productos (nombre, marca, tipo, descripcion, precio) VALUES 
('Samsung Galaxy S24 Ultra 512GB', 'Samsung', 'CELULAR', 'Pantalla 6.8, Cámara 200MP', 5499900.00),
('iPhone 15 Pro Max 256GB', 'Apple', 'CELULAR', 'Titanio, Chip A17 Pro', 5899900.00),
('Portátil Lenovo ThinkPad E14', 'Lenovo', 'COMPUTADOR', 'Ryzen 5, 16GB RAM, 512GB SSD', 3200000.00),
('Audífonos Huawei FreeBuds 5i', 'Huawei', 'ACCESORIOS', 'Cancelación de ruido activa', 350000.00);



-- 1. Trigger para asignar la prioridad base automáticamente al sacar un turno
DELIMITER //
CREATE TRIGGER before_insert_turnos
BEFORE INSERT ON turnos
FOR EACH ROW
BEGIN
    DECLARE base_priority DECIMAL(3,1);
    SELECT nivel_prioridad INTO base_priority FROM departamentos WHERE id = NEW.departamento_id;
    SET NEW.prioridad_actual = base_priority;
END;
//
DELIMITER ;


-- 2. Vista Inteligente con Cola y Envejecimiento (Aging) de Turnos
CREATE VIEW vista_cola_turnos AS
SELECT 
    t.id AS turno_id,
    t.numero_correlativo,
    u.nombres AS usuario,
    u.tipo_cliente,
    d.nombre AS departamento,
    t.estado,
    t.fecha_creacion,
    TIMESTAMPDIFF(MINUTE, t.fecha_creacion, NOW()) AS minutos_espera,
    CASE 
        -- AGING: Si es Ventas (Prioridad 3) y lleva esperando 5 minutos o más, 
        -- su prioridad sube dinámicamente a 1.5 para ganarle a Soporte Técnico.
        WHEN d.nombre = 'Ventas' AND TIMESTAMPDIFF(MINUTE, t.fecha_creacion, NOW()) >= 5 THEN 1.5
        ELSE t.prioridad_actual 
    END AS prioridad_dinamica
FROM turnos t
JOIN usuarios u ON t.usuario_id = u.id
JOIN departamentos d ON t.departamento_id = d.id
WHERE t.estado = 'PENDIENTE'
ORDER BY prioridad_dinamica ASC, t.fecha_creacion ASC;
