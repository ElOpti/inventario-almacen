-- ==========================================================
-- SISTEMA DE GESTION DE INVENTARIO - DATOS INICIALES MYSQL
-- CARPETA: SCRIPTS
-- ==========================================================

USE inventario_db;

-- 1. Insertar Tipos de Roles
INSERT INTO roles (id, nombre, nombre_visible, descripcion)
VALUES 
(1, 'ROLE_ADMINISTRADOR', 'Administrador', 'Acceso total a inventario, historial de movimientos y gestion de usuarios'),
(2, 'ROLE_ALMACENISTA', 'Almacenista', 'Acceso a consulta de inventario y modulo de despacho/salida de productos')
ON DUPLICATE KEY UPDATE nombre_visible = VALUES(nombre_visible);

-- 2. Insertar Usuarios Iniciales
-- Contrasenas hasheadas con BCrypt: 'admin123' y 'almacen123'
INSERT INTO usuarios (id, username, password, nombre_completo, rol_id, activo, fecha_registro)
VALUES 
(1, 'admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Carlos Morales (Administrador General)', 1, TRUE, NOW()),
(2, 'almacen', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Juan Perez (Encargado de Almacen)', 2, TRUE, NOW())
ON DUPLICATE KEY UPDATE nombre_completo = VALUES(nombre_completo);

-- 3. Insertar Productos de Prueba
INSERT INTO productos (id, codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
VALUES
(1, 'LAP-001', 'Laptop Dell Latitude 3420', 'Intel Core i5, 16GB RAM, SSD 512GB NVMe', 'Computo', 20, 'ACTIVO', NOW(), NOW()),
(2, 'MON-002', 'Monitor LG 27\'\' IPS Full HD', 'Pantalla 75Hz, HDMI/VGA, diseno sin bordes', 'Pantallas', 12, 'ACTIVO', NOW(), NOW()),
(3, 'TEC-003', 'Teclado Mecanico Logitech G413', 'Switches Romer-G, chasis de aluminio', 'Accesorios', 35, 'ACTIVO', NOW(), NOW()),
(4, 'MOU-004', 'Mouse Inalambrico Logitech MX Master 3S', 'Sensor 8000 DPI, scroll electromagnetico', 'Accesorios', 0, 'ACTIVO', NOW(), NOW()),
(5, 'CAB-005', 'Cable VGA a VGA 1.8m (Modelo Antiguo)', 'Cable analogico descontinuado', 'Cables', 8, 'INACTIVO', NOW(), NOW())
ON DUPLICATE KEY UPDATE nombre = VALUES(nombre);

-- 4. Insertar Historico de Movimientos
INSERT INTO movimientos (id, tipo, producto_id, cantidad, stock_anterior, stock_resultante, usuario_id, fecha_hora, motivo)
VALUES
(1, 'ENTRADA', 1, 20, 0, 20, 1, NOW(), 'Carga inicial por compra a proveedor #OC-4589'),
(2, 'ENTRADA', 2, 15, 0, 15, 1, NOW(), 'Ingreso de lote mayorista'),
(3, 'SALIDA', 2, 3, 15, 12, 2, NOW(), 'Despacho a sucursal norte - Guia #0084'),
(4, 'ENTRADA', 3, 35, 0, 35, 1, NOW(), 'Recepcion de importacion')
ON DUPLICATE KEY UPDATE motivo = VALUES(motivo);
