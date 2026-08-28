package com.almacen.inventario.repository;

import com.almacen.inventario.model.Movimiento;
import com.almacen.inventario.model.TipoMovimiento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovimientoRepository extends JpaRepository<Movimiento, Long> {

    List<Movimiento> findAllByOrderByFechaHoraDesc();

    List<Movimiento> findByTipoOrderByFechaHoraDesc(TipoMovimiento tipo);

    List<Movimiento> findByProductoIdOrderByFechaHoraDesc(Long productoId);
}
