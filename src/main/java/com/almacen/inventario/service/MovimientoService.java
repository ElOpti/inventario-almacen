package com.almacen.inventario.service;

import com.almacen.inventario.dto.SalidaStockDto;
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
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimientoService(MovimientoRepository movimientoRepository,
                             ProductoRepository productoRepository,
                             UsuarioRepository usuarioRepository) {
        this.movimientoRepository = movimientoRepository;
        this.productoRepository = productoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public Movimiento registrarSalida(SalidaStockDto dto, String username) {
        if (dto.getCantidad() == null || dto.getCantidad() <= 0) {
            throw new IllegalArgumentException("Error: La cantidad a retirar debe ser un número entero mayor a 0.");
        }

        Producto producto = productoRepository.findById(dto.getProductoId())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado con ID: " + dto.getProductoId()));

        // REGLA: Solo se pueden despachar productos activos
        if (EstadoProducto.INACTIVO.equals(producto.getEstado())) {
            throw new IllegalArgumentException("Error: No es posible realizar salidas de un producto dado de baja (Inactivo).");
        }

        // REGLA: No se puede sacar una cantidad mayor de un producto de la que está en inventario
        if (dto.getCantidad() > producto.getStock()) {
            throw new IllegalArgumentException(String.format(
                    "Error de Stock: No se puede sacar una cantidad mayor a la existencia actual en el almacén. " +
                    "Cantidad solicitada: %d | Stock disponible actualmente: %d unidades de '%s'.",
                    dto.getCantidad(), producto.getStock(), producto.getNombre()
            ));
        }

        Usuario usuario = usuarioRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        int stockAnterior = producto.getStock();
        int stockResultante = stockAnterior - dto.getCantidad();

        producto.setStock(stockResultante);
        producto.setFechaModificacion(LocalDateTime.now());
        productoRepository.save(producto);

        String motivo = (dto.getMotivo() != null && !dto.getMotivo().trim().isEmpty())
                ? dto.getMotivo().trim()
                : "Salida ordinaria de almacén";

        Movimiento movimiento = new Movimiento(
                TipoMovimiento.SALIDA,
                producto,
                dto.getCantidad(),
                stockAnterior,
                stockResultante,
                usuario,
                motivo
        );

        return movimientoRepository.save(movimiento);
    }

    @Transactional(readOnly = true)
    public List<Movimiento> listarHistorial(TipoMovimiento tipoFiltro) {
        if (tipoFiltro == null) {
            return movimientoRepository.findAllByOrderByFechaHoraDesc();
        }
        return movimientoRepository.findByTipoOrderByFechaHoraDesc(tipoFiltro);
    }
}
