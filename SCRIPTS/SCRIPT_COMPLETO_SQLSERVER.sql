-- ==========================================================
-- SISTEMA DE GESTION DE INVENTARIO - SCRIPT COMPLETO SQL SERVER
-- CARPETA: SCRIPTS
-- (Incluye Creacion de BD, Tablas, Relaciones y Datos Iniciales)
-- ==========================================================

IF NOT EXISTS (SELECT * FROM sys.databases WHERE name = 'inventario_db')
BEGIN
    CREATE DATABASE inventario_db;
END
GO

USE inventario_db;
GO

-- 1. Tabla de Tipos de Roles
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'roles')
BEGIN
    CREATE TABLE roles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(50) NOT NULL UNIQUE,
        nombre_visible VARCHAR(50) NOT NULL,
        descripcion NVARCHAR(200)
    );
END
GO

-- 2. Tabla de Usuarios
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'usuarios')
BEGIN
    CREATE TABLE usuarios (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        username VARCHAR(50) NOT NULL UNIQUE,
        password VARCHAR(255) NOT NULL,
        nombre_completo VARCHAR(100) NOT NULL,
        rol_id BIGINT NOT NULL,
        activo BIT NOT NULL DEFAULT 1,
        fecha_registro DATETIME2 NOT NULL DEFAULT GETDATE(),
        CONSTRAINT fk_usuario_rol FOREIGN KEY (rol_id) REFERENCES roles (id)
    );
END
GO

-- 3. Tabla de Productos
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'productos')
BEGIN
    CREATE TABLE productos (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        codigo VARCHAR(50) NOT NULL UNIQUE,
        nombre VARCHAR(150) NOT NULL,
        descripcion NVARCHAR(MAX),
        categoria VARCHAR(100) NOT NULL,
        stock INT NOT NULL DEFAULT 0,
        estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO',
        fecha_creacion DATETIME2 NOT NULL DEFAULT GETDATE(),
        fecha_modificacion DATETIME2 NOT NULL DEFAULT GETDATE()
    );
END
GO

-- 4. Tabla de Historico de Movimientos
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'movimientos')
BEGIN
    CREATE TABLE movimientos (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        tipo VARCHAR(20) NOT NULL,
        producto_id BIGINT NOT NULL,
        cantidad INT NOT NULL,
        stock_anterior INT NOT NULL,
        stock_resultante INT NOT NULL,
        usuario_id BIGINT NOT NULL,
        fecha_hora DATETIME2 NOT NULL DEFAULT GETDATE(),
        motivo NVARCHAR(500),
        CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES productos (id),
        CONSTRAINT fk_movimiento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios (id)
    );
END
GO

-- ==========================================================
-- INSERCION DE DATOS INICIALES
-- ==========================================================

-- Roles
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

-- Usuarios
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

-- Productos
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
