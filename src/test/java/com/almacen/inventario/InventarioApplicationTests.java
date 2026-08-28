package com.almacen.inventario;

import com.almacen.inventario.dto.EntradaStockDto;
import com.almacen.inventario.dto.ProductoDto;
import com.almacen.inventario.dto.SalidaStockDto;
import com.almacen.inventario.dto.UsuarioDto;
import com.almacen.inventario.model.*;
import com.almacen.inventario.repository.MovimientoRepository;
import com.almacen.inventario.repository.ProductoRepository;
import com.almacen.inventario.repository.RolRepository;
import com.almacen.inventario.repository.UsuarioRepository;
import com.almacen.inventario.service.MovimientoService;
import com.almacen.inventario.service.ProductoService;
import com.almacen.inventario.service.UsuarioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class InventarioApplicationTests {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private MovimientoService movimientoService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProductoRepository productoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private MovimientoRepository movimientoRepository;

    private Usuario adminUser;
    private Usuario almacenistaUser;
    private Rol rolAdmin;
    private Rol rolAlmacen;

    @BeforeEach
    void setUp() {
        rolAdmin = rolRepository.findByNombre("ROLE_ADMINISTRADOR").orElse(null);
        rolAlmacen = rolRepository.findByNombre("ROLE_ALMACENISTA").orElse(null);
        adminUser = usuarioRepository.findByUsername("admin").orElse(null);
        almacenistaUser = usuarioRepository.findByUsername("almacen").orElse(null);
    }

    @Test
    @DisplayName("Context loads, roles table is seeded and initial test users exist")
    void contextLoads() {
        assertNotNull(rolAdmin, "El rol ROLE_ADMINISTRADOR debe existir en la tabla roles");
        assertNotNull(rolAlmacen, "El rol ROLE_ALMACENISTA debe existir en la tabla roles");
        assertNotNull(adminUser, "El usuario admin debe existir");
        assertNotNull(almacenistaUser, "El usuario almacenista debe existir");
        assertEquals("ROLE_ADMINISTRADOR", adminUser.getRol().getNombre());
        assertEquals("ROLE_ALMACENISTA", almacenistaUser.getRol().getNombre());
    }

    @Test
    @DisplayName("Regla 1: Al crear un nuevo producto, el stock inicial DEBE ser 0")
    void testCrearProductoStockInicialCero() {
        ProductoDto dto = new ProductoDto(null, "TEST-SKU-99", "Impresora Láser HP", "Impresora multifuncional", "Oficina");
        Producto creado = productoService.crearProducto(dto);

        assertNotNull(creado.getId());
        assertEquals("TEST-SKU-99", creado.getCodigo());
        assertEquals(0, creado.getStock(), "El stock inicial debe ser obligatoriamente 0");
        assertEquals(EstadoProducto.ACTIVO, creado.getEstado());
        assertTrue(creado.isActivo());
    }

    @Test
    @DisplayName("Regla 2: Aumentar inventario (Entrada) con cantidad positiva actualiza stock y genera movimiento ENTRADA")
    void testAumentarInventarioExitoso() {
        ProductoDto dto = new ProductoDto(null, "TEST-ENTRADA-01", "Tablet Samsung", "Tablet 10 pulgadas", "Cómputo");
        Producto producto = productoService.crearProducto(dto);
        assertEquals(0, producto.getStock());

        EntradaStockDto entradaDto = new EntradaStockDto(producto.getId(), 25, "Llegada de pedido #PO-8812");
        Movimiento mov = productoService.registrarEntrada(entradaDto, adminUser.getUsername());

        assertNotNull(mov.getId());
        assertEquals(TipoMovimiento.ENTRADA, mov.getTipo());
        assertEquals(25, mov.getCantidad());
        assertEquals(0, mov.getStockAnterior());
        assertEquals(25, mov.getStockResultante());
        assertEquals("admin", mov.getUsuario().getUsername());
        assertNotNull(mov.getFechaHora());

        Producto actualizado = productoService.obtenerPorId(producto.getId());
        assertEquals(25, actualizado.getStock());
    }

    @Test
    @DisplayName("Regla 3: Aumentar inventario con cantidad menor o igual a 0 debe lanzar error")
    void testAumentarInventarioCantidadInvalida() {
        ProductoDto dto = new ProductoDto(null, "TEST-ERR-01", "Hub USB-C", "Hub multipuerto", "Accesorios");
        Producto producto = productoService.crearProducto(dto);

        // Cantidad 0
        EntradaStockDto entradaCero = new EntradaStockDto(producto.getId(), 0, "Prueba");
        assertThrows(IllegalArgumentException.class, () -> {
            productoService.registrarEntrada(entradaCero, adminUser.getUsername());
        });

        // Cantidad negativa
        EntradaStockDto entradaNegativa = new EntradaStockDto(producto.getId(), -5, "Intento de restar");
        assertThrows(IllegalArgumentException.class, () -> {
            productoService.registrarEntrada(entradaNegativa, adminUser.getUsername());
        });
    }

    @Test
    @DisplayName("Regla 4: Dar de baja producto cambia su estatus a INACTIVO sin borrarlo, y permite reactivarlo")
    void testBajaYReactivacionProducto() {
        ProductoDto dto = new ProductoDto(null, "TEST-BAJA-01", "Cámara Web HD", "Webcam 1080p", "Accesorios");
        Producto producto = productoService.crearProducto(dto);
        Long id = producto.getId();

        // 1. Dar de baja
        Producto inactivo = productoService.cambiarEstado(id, EstadoProducto.INACTIVO);
        assertEquals(EstadoProducto.INACTIVO, inactivo.getEstado());
        assertFalse(inactivo.isActivo());

        // Verificar que sigue existiendo en BD
        assertTrue(productoRepository.findById(id).isPresent());

        // 2. Reactivar
        Producto reactivado = productoService.cambiarEstado(id, EstadoProducto.ACTIVO);
        assertEquals(EstadoProducto.ACTIVO, reactivado.getEstado());
        assertTrue(reactivado.isActivo());
    }

    @Test
    @DisplayName("Regla 5: Salida de inventario exitosa cuando cantidad <= stock, genera movimiento SALIDA")
    void testSalidaInventarioExitosa() {
        ProductoDto dto = new ProductoDto(null, "TEST-SAL-01", "Disco SSD 1TB", "NVMe M.2", "Almacenamiento");
        Producto producto = productoService.crearProducto(dto);

        // Ingresar 10 unidades primero
        productoService.registrarEntrada(new EntradaStockDto(producto.getId(), 10, "Stock inicial"), adminUser.getUsername());

        // Almacenista realiza salida de 4 unidades
        SalidaStockDto salidaDto = new SalidaStockDto(producto.getId(), 4, "Despacho a sucursal");
        Movimiento movSalida = movimientoService.registrarSalida(salidaDto, almacenistaUser.getUsername());

        assertNotNull(movSalida.getId());
        assertEquals(TipoMovimiento.SALIDA, movSalida.getTipo());
        assertEquals(4, movSalida.getCantidad());
        assertEquals(10, movSalida.getStockAnterior());
        assertEquals(6, movSalida.getStockResultante());
        assertEquals("almacen", movSalida.getUsuario().getUsername());

        Producto actualizado = productoService.obtenerPorId(producto.getId());
        assertEquals(6, actualizado.getStock());
    }

    @Test
    @DisplayName("Regla 6: No se puede sacar una cantidad mayor a la del stock actual (debe lanzar error)")
    void testSalidaInventarioCantidadMayorAStock() {
        ProductoDto dto = new ProductoDto(null, "TEST-OVER-01", "Memoria RAM 16GB", "DDR4 3200MHz", "Componentes");
        Producto producto = productoService.crearProducto(dto);

        // Stock disponible = 5
        productoService.registrarEntrada(new EntradaStockDto(producto.getId(), 5, "Stock inicial"), adminUser.getUsername());

        // Intentar retirar 6 unidades (mayor a 5)
        SalidaStockDto salidaExcesiva = new SalidaStockDto(producto.getId(), 6, "Retiro mayor al disponible");
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            movimientoService.registrarSalida(salidaExcesiva, almacenistaUser.getUsername());
        });

        assertTrue(excepcion.getMessage().contains("No se puede sacar una cantidad mayor"));
        
        // Stock debe mantenerse intacto en 5
        Producto productoFinal = productoService.obtenerPorId(producto.getId());
        assertEquals(5, productoFinal.getStock());
    }

    @Test
    @DisplayName("Regla 7: No se puede despachar un producto dado de baja (Inactivo)")
    void testSalidaProductoInactivoFalla() {
        ProductoDto dto = new ProductoDto(null, "TEST-INACT-01", "Cable Red Cat5", "Cable antiguo", "Redes");
        Producto producto = productoService.crearProducto(dto);
        productoService.registrarEntrada(new EntradaStockDto(producto.getId(), 10, "Stock"), adminUser.getUsername());
        
        // Dar de baja
        productoService.cambiarEstado(producto.getId(), EstadoProducto.INACTIVO);

        SalidaStockDto salidaDto = new SalidaStockDto(producto.getId(), 2, "Intento de salida de inactivo");
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> {
            movimientoService.registrarSalida(salidaDto, almacenistaUser.getUsername());
        });

        assertTrue(excepcion.getMessage().contains("Inactivo") || excepcion.getMessage().contains("activos"));
    }

    @Test
    @DisplayName("Regla 8: El historial de movimientos permite filtrar por ENTRADA y SALIDA")
    void testHistorialFiltros() {
        ProductoDto dto = new ProductoDto(null, "TEST-HIST-01", "Router WiFi 6", "Router AX3000", "Redes");
        Producto producto = productoService.crearProducto(dto);

        productoService.registrarEntrada(new EntradaStockDto(producto.getId(), 15, "Entrada 1"), adminUser.getUsername());
        movimientoService.registrarSalida(new SalidaStockDto(producto.getId(), 5, "Salida 1"), almacenistaUser.getUsername());

        List<Movimiento> todas = movimientoService.listarHistorial(null);
        List<Movimiento> soloEntradas = movimientoService.listarHistorial(TipoMovimiento.ENTRADA);
        List<Movimiento> soloSalidas = movimientoService.listarHistorial(TipoMovimiento.SALIDA);

        assertFalse(todas.isEmpty());
        assertTrue(soloEntradas.stream().allMatch(m -> m.getTipo() == TipoMovimiento.ENTRADA));
        assertTrue(soloSalidas.stream().allMatch(m -> m.getTipo() == TipoMovimiento.SALIDA));
    }

    @Test
    @DisplayName("Regla 9: El administrador puede crear usuarios asignando rol de Administrador o Almacenista mediante la tabla roles")
    void testCreacionUsuarioAdminYAlmacenista() {
        // 1. Crear nuevo Almacenista
        UsuarioDto dtoAlm = new UsuarioDto(null, "usuario_alm1", "Pedro Gómez", rolAlmacen.getId(), true);
        dtoAlm.setPassword("clave1234");
        Usuario creadoAlm = usuarioService.crearUsuario(dtoAlm);

        assertNotNull(creadoAlm.getId());
        assertEquals("usuario_alm1", creadoAlm.getUsername());
        assertEquals("ROLE_ALMACENISTA", creadoAlm.getRol().getNombre());
        assertTrue(creadoAlm.isActivo());

        // 2. Crear nuevo Administrador
        UsuarioDto dtoAdm = new UsuarioDto(null, "usuario_adm2", "María Delgado", rolAdmin.getId(), true);
        dtoAdm.setPassword("clave1234");
        Usuario creadoAdm = usuarioService.crearUsuario(dtoAdm);

        assertNotNull(creadoAdm.getId());
        assertEquals("usuario_adm2", creadoAdm.getUsername());
        assertEquals("ROLE_ADMINISTRADOR", creadoAdm.getRol().getNombre());
        assertTrue(creadoAdm.isActivo());
    }

    @Test
    @DisplayName("Regla 10: Validación de nombre de usuario duplicado y contraseñas cortas")
    void testValidacionCreacionUsuario() {
        UsuarioDto dto = new UsuarioDto(null, "admin", "Intento Duplicado", rolAlmacen.getId(), true);
        dto.setPassword("pass123");

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario(dto);
        });

        UsuarioDto dtoCorto = new UsuarioDto(null, "nuevo_user", "Nombre", rolAlmacen.getId(), true);
        dtoCorto.setPassword("12"); // Menor a 4 caracteres

        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.crearUsuario(dtoCorto);
        });
    }

    @Test
    @DisplayName("Regla 11: Administrador no puede desactivar su propia cuenta en sesión")
    void testAdminNoPuedeDesactivarseASiMismo() {
        assertThrows(IllegalArgumentException.class, () -> {
            usuarioService.cambiarEstado(adminUser.getId(), false, "admin");
        });
    }
}
