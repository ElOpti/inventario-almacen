package com.almacen.inventario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioDto {

    private Long id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 50, message = "El nombre de usuario debe tener entre 3 y 50 caracteres")
    private String username;

    private String password;

    @NotBlank(message = "El nombre completo es obligatorio")
    @Size(max = 100, message = "El nombre completo no debe superar los 100 caracteres")
    private String nombreCompleto;

    @NotNull(message = "Debe seleccionar un rol para el usuario")
    private Long rolId;

    private String rolNombre;
    private String rolNombreVisible;

    private boolean activo = true;

    public UsuarioDto() {
    }

    public UsuarioDto(Long id, String username, String nombreCompleto, Long rolId, boolean activo) {
        this.id = id;
        this.username = username;
        this.nombreCompleto = nombreCompleto;
        this.rolId = rolId;
        this.activo = activo;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public Long getRolId() {
        return rolId;
    }

    public void setRolId(Long rolId) {
        this.rolId = rolId;
    }

    public String getRolNombre() {
        return rolNombre;
    }

    public void setRolNombre(String rolNombre) {
        this.rolNombre = rolNombre;
    }

    public String getRolNombreVisible() {
        return rolNombreVisible;
    }

    public void setRolNombreVisible(String rolNombreVisible) {
        this.rolNombreVisible = rolNombreVisible;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }
}
