package com.almacen.inventario.model;

public enum TipoMovimiento {
    ENTRADA("Entrada de Inventario"),
    SALIDA("Salida de Inventario");

    private final String descripcion;

    TipoMovimiento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
