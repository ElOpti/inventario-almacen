-- ==========================================================
-- SISTEMA DE GESTION DE INVENTARIO - ESQUEMA DDL SQL SERVER
-- CARPETA: SCRIPTS
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
        nombre VARCHAR(50) NOT NULL UNIQUE,          -- 'ROLE_ADMINISTRADOR', 'ROLE_ALMACENISTA'
        nombre_visible VARCHAR(50) NOT NULL,         -- 'Administrador', 'Almacenista'
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
        estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVO', -- 'ACTIVO', 'INACTIVO'
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
        tipo VARCHAR(20) NOT NULL,                    -- 'ENTRADA', 'SALIDA'
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
