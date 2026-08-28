-- ==========================================================
-- SISTEMA DE GESTIÓN DE INVENTARIO - DATOS INICIALES SQL SERVER
-- ==========================================================

USE inventario_db;
GO

-- 1. Insertar Usuarios
IF NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'admin')
BEGIN
    INSERT INTO usuarios (username, password, nombre_completo, rol, activo, fecha_registro)
    VALUES ('admin', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Carlos Morales (Administrador General)', 'ROLE_ADMINISTRADOR', 1, GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM usuarios WHERE username = 'almacen')
BEGIN
    INSERT INTO usuarios (username, password, nombre_completo, rol, activo, fecha_registro)
    VALUES ('almacen', '$2a$10$GRLdNijSQMUvl/au9ofL.eDwmoohzzS7.rmNSJZ.0FxGQrvkWChTa', 'Juan Pérez (Encargado de Almacén)', 'ROLE_ALMACENISTA', 1, GETDATE());
END
GO

-- 2. Insertar Productos de Prueba
IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'LAP-001')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('LAP-001', 'Laptop Dell Latitude 3420', 'Intel Core i5, 16GB RAM, SSD 512GB NVMe', 'Cómputo', 20, 'ACTIVO', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'MON-002')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('MON-002', 'Monitor LG 27'''' IPS Full HD', 'Pantalla 75Hz, HDMI/VGA, diseño sin bordes', 'Pantallas', 12, 'ACTIVO', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'TEC-003')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('TEC-003', 'Teclado Mecánico Logitech G413', 'Switches Romer-G, chasis de aluminio', 'Accesorios', 35, 'ACTIVO', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'MOU-004')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('MOU-004', 'Mouse Inalámbrico Logitech MX Master 3S', 'Sensor 8000 DPI, scroll electromagnético', 'Accesorios', 0, 'ACTIVO', GETDATE(), GETDATE());
END

IF NOT EXISTS (SELECT 1 FROM productos WHERE codigo = 'CAB-005')
BEGIN
    INSERT INTO productos (codigo, nombre, descripcion, categoria, stock, estado, fecha_creacion, fecha_modificacion)
    VALUES ('CAB-005', 'Cable VGA a VGA 1.8m (Modelo Antiguo)', 'Cable analógico descontinuado', 'Cables', 8, 'INACTIVO', GETDATE(), GETDATE());
END
GO
