package com.almacen.inventario.controller;

import com.almacen.inventario.dto.UsuarioDto;
import com.almacen.inventario.model.Rol;
import com.almacen.inventario.model.Usuario;
import com.almacen.inventario.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/usuarios")
@PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR', 'ADMINISTRADOR')")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String listarUsuarios(Model model) {
        List<Usuario> usuarios = usuarioService.listarTodos();
        model.addAttribute("usuarios", usuarios);
        return "usuarios/lista";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuarioForm(Model model) {
        List<Rol> roles = usuarioService.listarRoles();
        if (!model.containsAttribute("usuarioDto")) {
            UsuarioDto dto = new UsuarioDto();
            // Rol predeterminado: Almacenista
            roles.stream()
                    .filter(r -> "ROLE_ALMACENISTA".equalsIgnoreCase(r.getNombre()))
                    .findFirst()
                    .ifPresent(r -> dto.setRolId(r.getId()));
            dto.setActivo(true);
            model.addAttribute("usuarioDto", dto);
        }
        model.addAttribute("roles", roles);
        model.addAttribute("esEdicion", false);
        return "usuarios/formulario";
    }

    @PostMapping("/guardar")
    public String guardarUsuario(@Valid @ModelAttribute("usuarioDto") UsuarioDto usuarioDto,
                                 BindingResult bindingResult,
                                 Model model,
                                 RedirectAttributes redirectAttributes) {
        if (usuarioDto.getPassword() == null || usuarioDto.getPassword().trim().isEmpty()) {
            bindingResult.rejectValue("password", "error.password", "La contraseña es requerida para un nuevo usuario.");
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", usuarioService.listarRoles());
            model.addAttribute("esEdicion", false);
            return "usuarios/formulario";
        }

        try {
            Usuario nuevo = usuarioService.crearUsuario(usuarioDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Usuario '" + nuevo.getUsername() + "' registrado exitosamente con rol " + nuevo.getRol().getNombreVisible() + ".");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", usuarioService.listarRoles());
            model.addAttribute("esEdicion", false);
            return "usuarios/formulario";
        }
    }

    @GetMapping("/editar/{id}")
    public String editarUsuarioForm(@PathVariable("id") Long id, Model model) {
        Usuario usuario = usuarioService.obtenerPorId(id);
        UsuarioDto dto = new UsuarioDto(
                usuario.getId(), 
                usuario.getUsername(), 
                usuario.getNombreCompleto(), 
                usuario.getRol().getId(), 
                usuario.isActivo()
        );
        dto.setRolNombre(usuario.getRol().getNombre());
        dto.setRolNombreVisible(usuario.getRol().getNombreVisible());
        
        model.addAttribute("usuarioDto", dto);
        model.addAttribute("roles", usuarioService.listarRoles());
        model.addAttribute("esEdicion", true);
        return "usuarios/formulario";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizarUsuario(@PathVariable("id") Long id,
                                    @Valid @ModelAttribute("usuarioDto") UsuarioDto usuarioDto,
                                    BindingResult bindingResult,
                                    Model model,
                                    RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", usuarioService.listarRoles());
            model.addAttribute("esEdicion", true);
            return "usuarios/formulario";
        }

        try {
            Usuario actualizado = usuarioService.actualizarUsuario(id, usuarioDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Usuario '" + actualizado.getUsername() + "' actualizado correctamente.");
            return "redirect:/usuarios";
        } catch (IllegalArgumentException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("roles", usuarioService.listarRoles());
            model.addAttribute("esEdicion", true);
            return "usuarios/formulario";
        }
    }

    @PostMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable("id") Long id,
                                @RequestParam("activo") boolean activo,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {
        try {
            Usuario usuario = usuarioService.cambiarEstado(id, activo, principal.getName());
            String estadoTxt = usuario.isActivo() ? "activado" : "desactivado";
            redirectAttributes.addFlashAttribute("successMessage", 
                    "El usuario '" + usuario.getUsername() + "' ha sido " + estadoTxt + " exitosamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/usuarios";
    }
}
