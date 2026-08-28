package com.almacen.inventario.service;

import com.almacen.inventario.dto.EntradaStockDto;
import com.almacen.inventario.dto.ProductoDto;
import com.almacen.inventario.model.*;
import com.almacen.inventario.repository.MovimientoRepository;
import com.almacen.inventario.repository.ProductoRepository;
import com.almacen.inventario.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final MovimientoRepository movimientoRepository;
    private final UsuarioRepository usuarioRepository;

    public ProductoService(ProductoRepository productoRepository,
                           MovimientoRepository movimientoRepository,
                           UsuarioRepository usuarioRepository) {
        this.productoRepository = productoRepository;
        this.movimientoRepository = movimientoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorEstado(EstadoProducto estado) {
        if (estado == null) {
            return listarTodos();
        }
        return productoRepository.findByEstadoOrderByNombreAsc(estado);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepository.findByEstadoOrderByNombreAsc(EstadoProducto.ACTIVO);
    }

    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + id));
    }

    public Producto crearProducto(ProductoDto dto) {
        if (productoRepository.existsByCodigo(dto.getCodigo().trim())) {
            throw new IllegalArgumentException("Ya existe un producto registrado con el código: " + dto.getCodigo().trim());
        }

        Producto producto = new Producto();
        producto.setCodigo(dto.getCodigo().trim().toUpperCase());
        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : "");
        producto.setCategoria(dto.getCategoria().trim());
        
        // REGLA: Al agregar un producto, la cantidad inicial será 0
        producto.setStock(0);
        producto.setEstado(EstadoProducto.ACTIVO);
        producto.setFechaCreacion(LocalDateTime.now());
        producto.setFechaModificacion(LocalDateTime.now());

        return productoRepository.save(producto);
    }

    public Producto actualizarProducto(Long id, ProductoDto dto) {
        Producto producto = obtenerPorId(id);

        if (productoRepository.existsByCodigoAndIdNot(dto.getCodigo().trim(), id)) {
            throw new IllegalArgumentException("Ya existe otro producto con el código: " + dto.getCodigo().trim());
        }

        producto.setCodigo(dto.getCodigo().trim().toUpperCase());
        producto.setNombre(dto.getNombre().trim());
        producto.setDescripcion(dto.getDescripcion() != null ? dto.getDescripcion().trim() : "");
        producto.setCategoria(dto.getCategoria().trim());
        producto.setFechaModificacion(LocalDateTime.now());

        return productoRepository.save(producto);
    }

    public Producto cambiarEstado(Long id, EstadoProducto nuevoEstado) {
        Producto producto = obtenerPorId(id);
        producto.setEstado(nuevoEstado);
        producto.setFechaModificacion(LocalDateTime.now());
        return productoRepository.save(producto);
    }

    public Movimiento registrarEntrada(EntradaStockDto dto, String username) {
        if (dto.getCantidad() == null || dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("Error: La cantidad para aumentar inventario debe ser un número entero mayor a 0. No es posible disminuir o ingresar valores negativos.");
        }

        Producto producto = obtenerPorId(dto.getProductoId());

        if (EstadoProducto.INACTIVO.equals(producto.getEstado())) {
            throw new IllegalArgumentException("Error: No se puede aumentar inventario de un producto inactivo. Debe reactivarlo primero.");
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        int stockAnterior = producto.getStock();
        int stockResultante = stockAnterior + dto.getCantidad();

        producto.setStock(stockResultante);
        producto.setFechaModificacion(LocalDateTime.now());
        productoRepository.save(producto);

        String motivo = (dto.getMotivo() != null && !dto.getMotivo().trim().isEmpty())
                ? dto.getMotivo().trim()
                : "Entrada directa al almacén";

        Movimiento movimiento = new Movimiento(
                TipoMovimiento.ENTRADA,
                producto,
                dto.getCantidad(),
                stockAnterior,
                stockResultante,
                usuario,
                motivo
        );

        return movimientoRepository.save(movimiento);
    }
}
