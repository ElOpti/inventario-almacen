package com.almacen.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProductoDto {

    private Long id;

    @NotBlank(message = "El código o SKU del producto es obligatorio")
    @Size(max = 50, message = "El código no debe superar los 50 caracteres")
    private String codigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(max = 150, message = "El nombre no debe superar los 150 caracteres")
    private String nombre;

    @Size(max = 500, message = "La descripción no debe superar los 500 caracteres")
    private String descripcion;

    @NotBlank(message = "La categoría es obligatoria")
    @Size(max = 100, message = "La categoría no debe superar los 100 caracteres")
    private String categoria;

    public ProductoDto() {
    }

    public ProductoDto(Long id, String codigo, String nombre, String descripcion, String categoria) {
        this.id = id;
        this.codigo = codigo;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.categoria = categoria;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}
