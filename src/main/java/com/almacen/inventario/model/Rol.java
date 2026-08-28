package com.almacen.inventario.model;

import jakarta.persistence.*;

@Entity
@Table(name = "roles")
public class Rol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre; // 'ROLE_ADMINISTRADOR', 'ROLE_ALMACENISTA'

    @Column(name = "nombre_visible", nullable = false, length = 50)
    private String nombreVisible; // 'Administrador', 'Almacenista'

    @Column(length = 200)
    private String descripcion;

    public Rol() {
    }

    public Rol(String nombre, String nombreVisible, String descripcion) {
        this.nombre = nombre;
        this.nombreVisible = nombreVisible;
        this.descripcion = descripcion;
    }

    public Rol(Long id, String nombre, String nombreVisible, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.nombreVisible = nombreVisible;
        this.descripcion = descripcion;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNombreVisible() {
        return nombreVisible;
    }

    public void setNombreVisible(String nombreVisible) {
        this.nombreVisible = nombreVisible;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
