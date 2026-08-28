package com.almacen.inventario.config;

import com.almacen.inventario.model.*;
import com.almacen.inventario.repository.MovimientoRepository;
import com.almacen.inventario.repository.ProductoRepository;
import com.almacen.inventario.repository.RolRepository;
import com.almacen.inventario.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final RolRepository rolRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(RolRepository rolRepository,
                           UsuarioRepository usuarioRepository,
                           ProductoRepository productoRepository,
                           MovimientoRepository movimientoRepository,
                           PasswordEncoder passwordEncoder) {
        this.rolRepository = rolRepository;
        this.usuarioRepository = usuarioRepository;
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // 1. Inicializar Catálogo de Roles en Base de Datos
        Rol rolAdmin = rolRepository.findByNombre("ROLE_ADMINISTRADOR")
                .orElseGet(() -> rolRepository.save(new Rol(
                        "ROLE_ADMINISTRADOR",
                        "Administrador",
                        "Acceso total a inventario, historial de movimientos y gestión de usuarios"
                )));

        Rol rolAlmacen = rolRepository.findByNombre("ROLE_ALMACENISTA")
                .orElseGet(() -> rolRepository.save(new Rol(
                        "ROLE_ALMACENISTA",
                        "Almacenista",
                        "Acceso a consulta de inventario y módulo de despacho/salida de productos"
                )));

        // 2. Inicializar Usuarios
        if (usuarioRepository.count() == 0) {
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setNombreCompleto("Carlos Morales (Administrador General)");
            admin.setRol(rolAdmin);
            admin.setActivo(true);
            admin.setFechaRegistro(LocalDateTime.now().minusDays(30));
            usuarioRepository.save(admin);

            Usuario almacenista = new Usuario();
            almacenista.setUsername("almacen");
            almacenista.setPassword(passwordEncoder.encode("almacen123"));
            almacenista.setNombreCompleto("Juan Pérez (Encargado de Almacén)");
            almacenista.setRol(rolAlmacen);
            almacenista.setActivo(true);
            almacenista.setFechaRegistro(LocalDateTime.now().minusDays(30));
            usuarioRepository.save(almacenista);

            // 3. Inicializar Productos de Ejemplo
            if (productoRepository.count() == 0) {
                // Producto 1
                Producto p1 = new Producto();
                p1.setCodigo("LAP-001");
                p1.setNombre("Laptop Dell Latitude 3420");
                p1.setDescripcion("Intel Core i5, 16GB RAM, SSD 512GB NVMe");
                p1.setCategoria("Cómputo");
                p1.setStock(20);
                p1.setEstado(EstadoProducto.ACTIVO);
                p1.setFechaCreacion(LocalDateTime.now().minusDays(20));
                p1.setFechaModificacion(LocalDateTime.now().minusDays(10));
                productoRepository.save(p1);

                // Movimiento inicial de entrada para p1
                Movimiento m1 = new Movimiento(
                        TipoMovimiento.ENTRADA,
                        p1,
                        20,
                        0,
                        20,
                        admin,
                        "Carga inicial por compra a proveedor #OC-4589"
                );
                m1.setFechaHora(LocalDateTime.now().minusDays(20));
                movimientoRepository.save(m1);

                // Producto 2
                Producto p2 = new Producto();
                p2.setCodigo("MON-002");
                p2.setNombre("Monitor LG 27'' IPS Full HD");
                p2.setDescripcion("Pantalla 75Hz, HDMI/VGA, diseño sin bordes");
                p2.setCategoria("Pantallas");
                p2.setStock(12);
                p2.setEstado(EstadoProducto.ACTIVO);
                p2.setFechaCreacion(LocalDateTime.now().minusDays(15));
                p2.setFechaModificacion(LocalDateTime.now().minusDays(5));
                productoRepository.save(p2);

                Movimiento m2 = new Movimiento(
                        TipoMovimiento.ENTRADA,
                        p2,
                        15,
                        0,
                        15,
                        admin,
                        "Ingreso de lote mayorista"
                );
                m2.setFechaHora(LocalDateTime.now().minusDays(15));
                movimientoRepository.save(m2);

                Movimiento m3 = new Movimiento(
                        TipoMovimiento.SALIDA,
                        p2,
                        3,
                        15,
                        12,
                        almacenista,
                        "Despacho a sucursal norte - Guía #0084"
                );
                m3.setFechaHora(LocalDateTime.now().minusDays(5));
                movimientoRepository.save(m3);

                // Producto 3: Teclado con stock
                Producto p3 = new Producto();
                p3.setCodigo("TEC-003");
                p3.setNombre("Teclado Mecánico Logitech G413");
                p3.setDescripcion("Switches Romer-G, chasis de aluminio, retroiluminado");
                p3.setCategoria("Accesorios");
                p3.setStock(35);
                p3.setEstado(EstadoProducto.ACTIVO);
                p3.setFechaCreacion(LocalDateTime.now().minusDays(10));
                p3.setFechaModificacion(LocalDateTime.now().minusDays(2));
                productoRepository.save(p3);

                Movimiento m4 = new Movimiento(
                        TipoMovimiento.ENTRADA,
                        p3,
                        35,
                        0,
                        35,
                        admin,
                        "Recepción de importación"
                );
                m4.setFechaHora(LocalDateTime.now().minusDays(10));
                movimientoRepository.save(m4);

                // Producto 4: Nuevo producto con stock inicial 0
                Producto p4 = new Producto();
                p4.setCodigo("MOU-004");
                p4.setNombre("Mouse Inalámbrico Logitech MX Master 3S");
                p4.setDescripcion("Sensor 8000 DPI, scroll electromagnético, Bluetooth");
                p4.setCategoria("Accesorios");
                p4.setStock(0); // Recién creado, en 0
                p4.setEstado(EstadoProducto.ACTIVO);
                p4.setFechaCreacion(LocalDateTime.now().minusHours(4));
                p4.setFechaModificacion(LocalDateTime.now().minusHours(4));
                productoRepository.save(p4);

                // Producto 5: Producto dado de baja (Inactivo)
                Producto p5 = new Producto();
                p5.setCodigo("CAB-005");
                p5.setNombre("Cable VGA a VGA 1.8m (Modelo Antiguo)");
                p5.setDescripcion("Cable de video analógico blindado");
                p5.setCategoria("Cables");
                p5.setStock(8);
                p5.setEstado(EstadoProducto.INACTIVO); // Inactivo (dado de baja)
                p5.setFechaCreacion(LocalDateTime.now().minusDays(40));
                p5.setFechaModificacion(LocalDateTime.now().minusDays(1));
                productoRepository.save(p5);
            }
        }
    }
}
