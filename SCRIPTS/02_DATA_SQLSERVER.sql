-- ==========================================================
-- SISTEMA DE GESTION DE INVENTARIO - DATOS INICIALES SQL SERVER
-- CARPETA: SCRIPTS
-- ==========================================================

USE inventario_db;
GO

-- 1. Insertar Tipos de Roles
IF NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ROLE_ADMINISTRADOR')
BEGIN
    INSERT INTO roles (nombre, nombre_visible, descripcion)
    VALUES ('ROLE_ADMINISTRADOR', 'Administrador', 'Acceso total a inventario, historial de movimientos y gestion de usuarios');
END

IF NOT EXISTS (SELECT 1 FROM roles WHERE nombre = 'ROLE_ALMACENISTA')
BEGIN
    INSERT INTO roles (nombre, nombre_visible, descripcion)
    VALUES ('ROLE_ALMACENISTA', 'Almacenista', 'Acceso a consulta de inventario y modulo de despacho/salida de productos');
END
GO

-- 2. Insertar Usuarios Iniciales
-- Contrasenas hasheadas con BCrypt: 'admin123' y 'almacen123'
DECLARE @AdminRolId BIGINT = (SELECT id FROM roles WHERE nombre = 'ROLE_ADMINISTRADOR');
DECLARE @AlmacenRolId BIGINT = (SELECT id FROM roles WHERE nombre = 'ROLE_ALMACENISTA');

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin')
BEGIN
    INSERT INTO usuarios (username, password, nombre_completo, rol_id, activo, fecha_registro)
    VALUES ('admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Carlos Morales (Administrador General)', @AdminRolId, 1, GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'almacen')
BEGIN
    INSERT INTO usuarios (username, password, nombre_completo, rol_id, activo, fecha_registro)
    VALUES ('almacen', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Juan Perez (Encargado de Almacen)', @AlmacenRolId, 1, GETDATE());
END
GO

-- 3. Insertar Productos de Prueba
IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'LAP-001')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('LAP-001', 'Laptop Dell Latitude 3420', 'Intel Core i5, 16GB RAM, SSD 512GB NVMe', 'Computo', 20, 'ACTIVO', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'MON-002')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('MON-002', 'Monitor LG 27'''' IPS Full HD', 'Pantalla 75Hz, HDMI/VGA, diseno sin bordes', 'Pantallas', 12, 'ACTIVO', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'TEC-003')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('TEC-003', 'Teclado Mecanico Logitech G413', 'Switches Romer-G, chasis de aluminio', 'Accesorios', 35, 'ACTIVO', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'MOU-004')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('MOU-004', 'Mouse Inalambrico Logitech MX Master 3S', 'Sensor 8000 DPI, scroll electromagnetico', 'Accesorios', 0, 'ACTIVO', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'CAB-005')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('CAB-005', 'Cable VGA a VGA 1.8m (Modelo Antiguo)', 'Cable analogico descontinuado', 'Cables', 8, 'INACTIVO', GETDATE(), GETDATE());
END
GO
