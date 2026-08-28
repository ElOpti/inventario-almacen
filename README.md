# Sistema de Gestión de Inventario de Almacén

## Datos relevantes para el desarrollo

### IDE utilizado

* **IntelliJ IDEA 2024.x** (Community o Ultimate).
* También es compatible con **Visual Studio Code** con la extensión *Extension Pack for Java*.

### Versión del lenguaje de programación

* **Java 23 (JDK 23.0.1 o superior)**.
* Compatible con **Java 21 LTS**.
* Requisito mínimo de Spring Boot: **Java 17**.

### DBMS utilizado y versión

El proyecto puede ejecutarse utilizando los siguientes sistemas gestores de bases de datos:

| DBMS                     | Versión     | Perfil      |
| ------------------------ | ----------- | ----------- |
| **H2 Database**          | 2.3.232     | `default`   |
| **MySQL Server**         | 8.0 / 8.4   | `mysql`     |
| **Microsoft SQL Server** | 2019 / 2022 | `sqlserver` |

Para desarrollo y pruebas se recomienda utilizar **H2 Database**, ya que no requiere instalar un servidor de base de datos.

---

## Pasos para correr la aplicación

### 1. Clonar o descargar el repositorio

Si se utiliza Git:

```bash
git clone <URL_DEL_REPOSITORIO>
cd inventario-almacen
```

### 2. Ejecutar la aplicación

El proyecto incluye **Maven Wrapper**, por lo que no es necesario instalar Maven manualmente.

En Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

En Linux o macOS:

```bash
./mvnw spring-boot:run
```

### 3. Acceder a la aplicación

Una vez iniciada la aplicación, abrir en el navegador:

```text
http://localhost:8080/login
```

### 4. Uso de H2

Si se utiliza la configuración predeterminada, la aplicación empleará **H2 en memoria**.

La consola de H2 se encuentra disponible en:

```text
http://localhost:8080/h2-console
```

Datos de conexión:

```text
JDBC URL: jdbc:h2:mem:inventariodb
Usuario: sa
Contraseña: dejar vacío
```

### 5. Ejecución con MySQL

Para utilizar MySQL:

1. Crear la base de datos ejecutando el script:

```text
SCRIPTS/SCRIPT_COMPLETO_MYSQL.sql
```

2. Configurar las credenciales en:

```text
src/main/resources/application-mysql.properties
```

3. Ejecutar la aplicación con el perfil `mysql`:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=mysql
```

4. Abrir:

```text
http://localhost:8080/login
```

### 6. Ejecución con SQL Server

Para utilizar Microsoft SQL Server:

1. Crear la base de datos ejecutando:

```text
SCRIPTS/SCRIPT_COMPLETO_SQLSERVER.sql
```

2. Configurar las credenciales en:

```text
src/main/resources/application-sqlserver.properties
```

3. Ejecutar la aplicación con el perfil `sqlserver`:

```powershell
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=sqlserver
```

4. Abrir:

```text
http://localhost:8080/login
```
