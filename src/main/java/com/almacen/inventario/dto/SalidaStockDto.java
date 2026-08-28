package com.almacen.inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SalidaStockDto {

    @NotNull(message = "Debe seleccionar un producto activo")
    private Long productoId;

    @NotNull(message = "Debe ingresar una cantidad a retirar")
    @Min(value = 1, message = "La cantidad a retirar debe ser mayor a 0")
    private Integer cantidad;

    @Size(max = 500, message = "El motivo o referencia no puede exceder los 500 caracteres")
    private String motivo;

    public SalidaStockDto() {
    }

    public SalidaStockDto(Long productoId, Integer cantidad, String motivo) {
        this.productoId = productoId;
        this.cantidad = cantidad;
        this.motivo = motivo;
    }

    public Long getProductoId() {
        return productoId;
    }

    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
