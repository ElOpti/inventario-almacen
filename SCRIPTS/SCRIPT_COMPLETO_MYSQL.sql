-- ==========================================================
-- SISTEMA DE GESTION DE INVENTARIO - SCRIPT COMPLETO MYSQL
-- CARPETA: SCRIPTS
-- (Incluye Creacion de BD, Tablas, Relaciones y Datos Iniciales)
-- ==========================================================

CREATE DATABASE IF NOT EXISTS inventario_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE inventario_db;

-- 1. Tabla de Tipos de Roles
CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE,
    nombre_visible VARCHAR(50) NOT NULL,
    descripcion VARCHAR(200)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 2. Tabla de Usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100) NOT NULL,
    rol_id BIGINT NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 3. Tabla de Productos
CREATE TABLE IF NOT EXISTS productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(150) NOT NULL,
    descripcion TEXT,
    categoria VARCHAR(100) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 4. Tabla de Historico de Movimientos
CREATE TABLE IF NOT EXISTS movimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    stock_anterior INT NOT NULL,
    stock_resultante INT NOT NULL,
    usuario_id BIGINT NOT NULL,
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    motivo VARCHAR(500),
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES productos (id) ON DELETE RESTRICT,
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id) ON DELETE RESTRICT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==========================================================
-- INSERCION DE DATOS INICIALES
-- ==========================================================

-- Roles
INSERT INTO roles (id, nombre, nombre_visible, descripcion)
VALUES 
(1, 'ROLE_ADMINISTRADOR', 'Administrador', 'Acceso total a inventario, historial de movimientos y gestion de usuarios'),
(2, 'ROLE_ALMACENISTA', 'Almacenista', 'Acceso a consulta de inventario y modulo de despacho/salida de productos')
ON DUPLICATE KEY UPDATE nombre_visible = VALUES(nombre_visible);

-- Usuarios (Password: 'admin123' / 'almacen123')
INSERT INTO usuarios (id, username, password, nombre_completo, rol_id, activo, fecha_registro)
VALUES 
(1, 'admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Carlos Morales (Administrador General)', 1, TRUE, NOW()),
(2, 'almacen', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Juan Perez (Encargado de Almacen)', 2, TRUE, NOW())
ON DUPLICATE KEY UPDATE nombre_completo = VALUES(nombre_completo);

-- Productos
INSERT INTO productos (id, codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
VALUES
(1, 'LAP-001', 'Laptop Dell Latitude 3420', 'Intel Core i5, 16GB RAM, SSD 512GB NVMe', 'Computo', 20, 'ACTIVO', NOW(), NOW()),
(2, 'MON-002', 'Monitor LG 27\'\' IPS Full HD', 'Pantalla 75Hz, HDMI/VGA, diseno sin bordes', 'Pantallas', 12, 'ACTIVO', NOW(), NOW()),
(3, 'TEC-003', 'Teclado Mecanico Logitech G413', 'Switches Romer-G, chasis de aluminio', 'Accesorios', 35, 'ACTIVO', NOW(), NOW()),
(4, 'MOU-004', 'Mouse Inalambrico Logitech MX Master 3S', 'Sensor 8000 DPI, scroll electromagnetico', 'Accesorios', 0, 'ACTIVO', NOW(), NOW()),
(5, 'CAB-005', 'Cable VGA a VGA 1.8m (Modelo Antiguo)', 'Cable analogico descontinuado', 'Cables', 8, 'INACTIVO', NOW(), NOW())
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- Movimientos
INSERT INTO movimientos (id, tipo, producto_id, cantidad, stock_anterior, stock_resultante, usuario_id, fecha_hora, motivo)
VALUES
(1, 'ENTRADA', 1, 20, 0, 20, 1, NOW(), 'Carga inicial por compra a proveedor #OC-4589'),
(2, 'ENTRADA', 2, 15, 0, 15, 1, NOW(), 'Ingreso de lote mayorista'),
(3, 'SALIDA', 2, 3, 15, 12, 2, NOW(), 'Despacho a sucursal norte - Guia #0084'),
(4, 'ENTRADA', 3, 35, 0, 35, 1, NOW(), 'Recepcion de importacion')
ON DUPLICATE KEY UPDATE motivo = VALUES(motivo);
