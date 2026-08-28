package com.almacen.inventario.repository;

import com.almacen.inventario.model.EstadoProducto;
import com.almacen.inventario.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    List<Producto> findAllByOrderByNombreAsc();
    
    List<Producto> findByEstadoOrderByNombreAsc(EstadoProducto estado);
    
    Optional<Producto> findByCodigo(String codigo);
    
    boolean existsByCodigo(String codigo);
    
    boolean existsByCodigoAndIdNot(String codigo, Long id);
}
