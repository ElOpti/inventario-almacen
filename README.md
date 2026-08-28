# Sistema de Gestion de Inventario de Almacen

Aplicacion web desarrollada con Java y Spring Boot bajo el patron de diseno MVC, para la administracion del inventario de un almacen. Incluye control de acceso basado en roles, gestion de productos, registro de movimientos y administracion de usuarios.

---

## Datos Tecnicos del Proyecto

### IDE Utilizado

- **IntelliJ IDEA** 2024.x (Community o Ultimate)
- Compatible con: **Visual Studio Code** con la extension "Extension Pack for Java" o **Eclipse IDE** 2024-09 con Spring Tools.

### Version del Lenguaje de Programacion

- **Java 23** (JDK 23.0.1 o superior)
- Compatible con Java 21 LTS.
- Spring Boot requiere un minimo de Java 17.

### DBMS Utilizado y su Version

La aplicacion soporta tres modos de base de datos:

| Motor              | Version             | Perfil         | Uso recomendado            |
|--------------------|---------------------|----------------|----------------------------|
| H2 Database        | 2.3.232 (embebida)  | `default`      | Desarrollo y pruebas       |
| MySQL Server       | 8.0 / 8.4           | `mysql`        | Produccion (Linux/Windows) |
| Microsoft SQL Server | 2019 / 2022       | `sqlserver`    | Produccion (Windows)       |

### Framework y Dependencias Principales

| Dependencia              | Version     |
|--------------------------|-------------|
| Spring Boot              | 3.4.3       |
| Spring MVC               | 6.2.x       |
| Spring Security          | 6.4.x       |
| Spring Data JPA          | 3.4.x       |
| Hibernate ORM            | 6.6.x       |
| Thymeleaf                | 3.1.x       |
| Bootstrap                | 5.3.3       |
| Bootstrap Icons          | 1.11.x      |
| Maven                    | 3.9.9       |

---

## Estructura del Proyecto

```
inventario-almacen/
├── SCRIPTS/                          <- Scripts SQL para base de datos
│   ├── 01_SCHEMA_MYSQL.sql           <- DDL: Creacion de tablas (MySQL)
│   ├── 02_DATA_MYSQL.sql             <- DML: Datos iniciales (MySQL)
│   ├── SCRIPT_COMPLETO_MYSQL.sql     <- Script unificado completo (MySQL)
│   ├── 01_SCHEMA_SQLSERVER.sql       <- DDL: Creacion de tablas (SQL Server)
│   ├── 02_DATA_SQLSERVER.sql         <- DML: Datos iniciales (SQL Server)
│   └── SCRIPT_COMPLETO_SQLSERVER.sql <- Script unificado completo (SQL Server)
├── src/
│   ├── main/
│   │   ├── java/com/almacen/inventario/
│   │   │   ├── config/              <- Configuracion de seguridad y datos iniciales
│   │   │   ├── controller/          <- Controladores MVC
│   │   │   ├── dto/                 <- Objetos de transferencia de datos
│   │   │   ├── model/               <- Entidades JPA
│   │   │   ├── repository/          <- Interfaces Spring Data JPA
│   │   │   └── service/             <- Logica de negocio
│   │   └── resources/
│   │       ├── templates/           <- Vistas Thymeleaf (HTML)
│   │       ├── static/css/          <- Estilos personalizados
│   │       ├── application.properties           <- Configuracion H2 (defecto)
│   │       ├── application-mysql.properties     <- Configuracion MySQL
│   │       └── application-sqlserver.properties <- Configuracion SQL Server
│   └── test/                        <- Pruebas unitarias y de seguridad (JUnit 5)
└── pom.xml
```

---

## Modelo de Base de Datos

El sistema cuenta con 4 tablas principales:

| Tabla         | Descripcion                                                    |
|---------------|----------------------------------------------------------------|
| `roles`       | Catalogo de tipos de roles disponibles en el sistema           |
| `usuarios`    | Cuentas de acceso con FK hacia `roles`                         |
| `productos`   | Inventario de productos con estado activo/inactivo             |
| `movimientos` | Historico de entradas y salidas con FK a `productos` y `usuarios` |

---

## Roles del Sistema

| Rol                 | Nombre Visible | Descripcion                                                          |
|---------------------|----------------|----------------------------------------------------------------------|
| `ROLE_ADMINISTRADOR`| Administrador  | Acceso total: inventario, historial de movimientos y gestion de usuarios |
| `ROLE_ALMACENISTA`  | Almacenista    | Consulta de inventario y modulo de despacho/salida de productos      |

---

## Credenciales Iniciales

| Usuario   | Contrasena    | Rol             |
|-----------|---------------|-----------------|
| `admin`   | `admin123`    | Administrador   |
| `almacen` | `almacen123`  | Almacenista     |

---

## Pasos para Correr la Aplicacion

### Prerequisitos

Antes de iniciar, asegurese de tener instalado:
- JDK 23 (o Java 21 LTS como minimo): https://www.oracle.com/java/technologies/downloads/
- Maven 3.9+ (incluido en el proyecto como Maven Wrapper)
- Git (opcional, para clonar el repositorio)

---

### Opcion 1: Ejecucion con Base de Datos H2 en Memoria (Modo por Defecto)

Este modo NO requiere instalar ningun servidor de base de datos. Los datos se pierden al detener la aplicacion.

**Paso 1.** Abra una terminal (PowerShell o CMD) y ubiquese en la carpeta del proyecto:

```powershell
cd inventario-almacen
```

**Paso 2.** Ejecute la aplicacion con el Maven Wrapper incluido:

```powershell
.\mvnw.cmd spring-boot:run
```

**Paso 3.** Espere hasta ver en consola:

```
Started InventarioApplication in X.XXX seconds
```

**Paso 4.** Abra su navegador y acceda a:

- Aplicacion: http://localhost:8080/login
- Consola H2 (base de datos): http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:inventariodb`
  - User Name: `sa`
  - Password: *(dejar vacio)*

---

### Opcion 2: Ejecucion con MySQL 8.x

**Paso 1.** Cree la base de datos y las tablas ejecutando el script completo en MySQL Workbench o desde la terminal:

```bash
mysql -u root -p < SCRIPTS/SCRIPT_COMPLETO_MYSQL.sql
```

**Paso 2.** Edite el archivo `src/main/resources/application-mysql.properties` y configure sus credenciales de MySQL:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/inventario_db?useSSL=false&serverTimezone=America/Mexico_City
spring.datasource.username=root
spring.datasource.password=SU_CONTRASENA_MYSQL
```

**Paso 3.** Inicie la aplicacion con el perfil MySQL activo:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

**Paso 4.** Acceda a la aplicacion en: http://localhost:8080/login

---

### Opcion 3: Ejecucion con Microsoft SQL Server 2019/2022

**Paso 1.** Ejecute el script completo en SQL Server Management Studio (SSMS) o con sqlcmd:

```bash
sqlcmd -S localhost -U sa -P SU_CONTRASENA -i SCRIPTS/SCRIPT_COMPLETO_SQLSERVER.sql
```

**Paso 2.** Edite el archivo `src/main/resources/application-sqlserver.properties`:

```properties
spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=inventario_db;trustServerCertificate=true
spring.datasource.username=sa
spring.datasource.password=SU_CONTRASENA_SQLSERVER
```

**Paso 3.** Inicie la aplicacion con el perfil SQL Server activo:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=sqlserver
```

**Paso 4.** Acceda a la aplicacion en: http://localhost:8080/login

---

### Ejecutar Pruebas Automatizadas

Para correr la suite completa de pruebas (23 pruebas unitarias y de seguridad):

```powershell
.\mvnw.cmd clean test
```

Resultado esperado:

```
Tests run: 23, Failures: 0, Errors: 0, Skipped: 0
BUILD SUCCESS
```

---

## Funcionalidades por Modulo

### Modulo: Inventario de Productos
- Listado de productos con filtros: Todos, Activos, Inactivos.
- Registro de nuevos productos (stock inicial = 0 obligatoriamente).
- Aumento de inventario (Entrada) con validacion de cantidad positiva.
- Salida de productos por Almacenista con validacion stock suficiente.
- Baja y reactivacion de productos (soft delete, sin eliminacion fisica).

### Modulo: Historial de Movimientos (Administrador)
- Registro auditado de todas las entradas y salidas.
- Filtros por tipo: Todos, Entrada, Salida.
- Informacion de fecha/hora, usuario responsable y cantidad.

### Modulo: Gestion de Usuarios (Administrador)
- CRUD completo de cuentas de acceso.
- Asignacion de rol desde la tabla `roles` de base de datos.
- Activacion y desactivacion de cuentas.
- Los usuarios nuevos son Almacenistas por defecto.
- Solo el Administrador puede crear usuarios y asignar roles.
- No existe registro publico de usuarios.
#
