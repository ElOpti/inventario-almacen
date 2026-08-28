package com.almacen.inventario.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(@RequestParam(value = "error", required = false) String error,
                        @RequestParam(value = "logout", required = false) String logout,
                        Authentication authentication,
                        Model model) {
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            return "redirect:/dashboard";
        }

        if (error != null) {
            model.addAttribute("errorMessage", "Usuario o contraseña incorrectos. Por favor verifique sus datos.");
        }
        if (logout != null) {
            model.addAttribute("logoutMessage", "Ha cerrado sesión exitosamente.");
        }

        return "login";
    }

    @GetMapping({"/", "/dashboard"})
    public String dashboard(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return "redirect:/login";
        }

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        boolean isAlmacenista = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ALMACENISTA"));

        if (isAdmin) {
            return "redirect:/inventario";
        } else if (isAlmacenista) {
            return "redirect:/salidas";
        }

        return "redirect:/inventario";
    }
}
