package com.almacen.inventario;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class InventarioWebSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Seguridad: Usuario no autenticado es redirigido al login")
    void testAccesoAnonimoRedirigeALogin() throws Exception {
        mockMvc.perform(get("/inventario"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @DisplayName("Seguridad: Administrador puede ver inventario")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMINISTRADOR"})
    void testAdminPuedeVerInventario() throws Exception {
        mockMvc.perform(get("/inventario"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/lista"));
    }

    @Test
    @DisplayName("Seguridad: Administrador puede ver formulario de nuevo producto")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMINISTRADOR"})
    void testAdminPuedeVerNuevoProducto() throws Exception {
        mockMvc.perform(get("/inventario/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/nuevo"));
    }

    @Test
    @DisplayName("Seguridad: Administrador puede ver histórico de movimientos")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMINISTRADOR"})
    void testAdminPuedeVerHistorial() throws Exception {
        mockMvc.perform(get("/historial"))
                .andExpect(status().isOk())
                .andExpect(view().name("historial/lista"));
    }

    @Test
    @DisplayName("Seguridad: Administrador puede acceder a la Gestión de Usuarios")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMINISTRADOR"})
    void testAdminPuedeAccederAUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/lista"));

        mockMvc.perform(get("/usuarios/nuevo"))
                .andExpect(status().isOk())
                .andExpect(view().name("usuarios/formulario"));
    }

    @Test
    @DisplayName("Seguridad: Administrador NO puede acceder al módulo de salidas (403/Forbidden)")
    @WithMockUser(username = "admin", authorities = {"ROLE_ADMINISTRADOR"})
    void testAdminNoPuedeAccederASalidas() throws Exception {
        mockMvc.perform(get("/salidas"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Seguridad: Almacenista puede ver inventario")
    @WithMockUser(username = "almacen", authorities = {"ROLE_ALMACENISTA"})
    void testAlmacenistaPuedeVerInventario() throws Exception {
        mockMvc.perform(get("/inventario"))
                .andExpect(status().isOk())
                .andExpect(view().name("inventario/lista"));
    }

    @Test
    @DisplayName("Seguridad: Almacenista puede ver y registrar salidas de inventario")
    @WithMockUser(username = "almacen", authorities = {"ROLE_ALMACENISTA"})
    void testAlmacenistaPuedeAccederASalidas() throws Exception {
        mockMvc.perform(get("/salidas"))
                .andExpect(status().isOk())
                .andExpect(view().name("salidas/registro"));
    }

    @Test
    @DisplayName("Seguridad: Almacenista NO puede acceder a crear nuevo producto (403/Forbidden)")
    @WithMockUser(username = "almacen", authorities = {"ROLE_ALMACENISTA"})
    void testAlmacenistaNoPuedeCrearProducto() throws Exception {
        mockMvc.perform(get("/inventario/nuevo"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Seguridad: Almacenista NO puede acceder al histórico de movimientos (403/Forbidden)")
    @WithMockUser(username = "almacen", authorities = {"ROLE_ALMACENISTA"})
    void testAlmacenistaNoPuedeVerHistorial() throws Exception {
        mockMvc.perform(get("/historial"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Seguridad: Almacenista NO puede acceder a la Gestión de Usuarios (403/Forbidden)")
    @WithMockUser(username = "almacen", authorities = {"ROLE_ALMACENISTA"})
    void testAlmacenistaNoPuedeAccederAUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios"))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/usuarios/nuevo"))
                .andExpect(status().isForbidden());
    }
}
