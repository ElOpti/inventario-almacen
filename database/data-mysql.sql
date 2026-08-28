-- ==========================================================
-- SISTEMA DE GESTIÓN DE INVENTARIO - DATOS INICIALES MYSQL
-- ==========================================================

USE inventario_db;

-- 1. Insertar Usuarios
-- Contraseña para ambos usuarios: 'admin123' y 'almacen123' (Hasheadas con BCrypt)
-- admin: admin123 -> $2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa
-- almacen: almacen123 -> $2a$10$w8T0M4j6nU9yE7yW5f8fEOhZ9mS3fQ8M5c8eL1uN3x8pY6vT7r6qa
INSERT INTO usuarios (id, username, password, nombre_completo, rol, activo, fecha_registro)
VALUES 
(1, 'admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Carlos Morales (Administrador General)', 'ROLE_ADMINISTRADOR', TRUE, NOW()),
(2, 'almacen', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Juan Pérez (Encargado de Almacén)', 'ROLE_ALMACENISTA', TRUE, NOW())
ON DUPLICATE KEY UPDATE nombre_completo = VALUES(nombre_completo);

-- 2. Insertar Productos de Prueba
INSERT INTO productos (id, codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
VALUES
(1, 'LAP-001', 'Laptop Dell Latitude 3420', 'Intel Core i5, 16GB RAM, SSD 512GB NVMe', 'Cómputo', 20, 'ACTIVO', NOW(), NOW()),
(2, 'MON-002', 'Monitor LG 27\'\' IPS Full HD', 'Pantalla 75Hz, HDMI/VGA, diseño sin bordes', 'Pantallas', 12, 'ACTIVO', NOW(), NOW()),
(3, 'TEC-003', 'Teclado Mecánico Logitech G413', 'Switches Romer-G, chasis de aluminio', 'Accesorios', 35, 'ACTIVO', NOW(), NOW()),
(4, 'MOU-004', 'Mouse Inalámbrico Logitech MX Master 3S', 'Sensor 8000 DPI, scroll electromagnético', 'Accesorios', 0, 'ACTIVO', NOW(), NOW()),
(5, 'CAB-005', 'Cable VGA a VGA 1.8m (Modelo Antiguo)', 'Cable analógico descontinuado', 'Cables', 8, 'INACTIVO', NOW(), NOW())
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- 3. Insertar Movimientos de Histórico
INSERT INTO movimientos (id, tipo, producto_id, cantidad, stock_anterior, stock_resultante, usuario_id, fecha_hora, motivo)
VALUES
(1, 'ENTRADA', 1, 20, 0, 20, 1, NOW(), 'Carga inicial por compra a proveedor #OC-4589'),
(2, 'ENTRADA', 2, 15, 0, 15, 1, NOW(), 'Ingreso de lote mayorista'),
(3, 'SALIDA', 2, 3, 15, 12, 2, NOW(), 'Despacho a sucursal norte - Guía #0084'),
(4, 'ENTRADA', 3, 35, 0, 35, 1, NOW(), 'Recepción de importación')
ON DUPLICATE KEY UPDATE motivo = VALUES(motivo);
