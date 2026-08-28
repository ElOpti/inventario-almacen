package com.almacen.inventario.controller;

import com.almacen.inventario.dto.SalidaStockDto;
import com.almacen.inventario.model.Producto;
import com.almacen.inventario.service.MovimientoService;
import com.almacen.inventario.service.ProductoService;
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
@RequestMapping("/salidas")
@PreAuthorize("hasAuthority('ROLE_ALMACENISTA')")
public class SalidaController {

    private final ProductoService productoService;
    private final MovimientoService movimientoService;

    public SalidaController(ProductoService productoService, MovimientoService movimientoService) {
        this.productoService = productoService;
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public String formularioSalida(Model model) {
        // REGLA: Solo se pueden ver y seleccionar productos activos
        List<Producto> productosActivos = productoService.listarActivos();
        
        if (!model.containsAttribute("salidaDto")) {
            model.addAttribute("salidaDto", new SalidaStockDto());
        }
        model.addAttribute("productosActivos", productosActivos);
        return "salidas/registro";
    }

    @PostMapping("/guardar")
    public String registrarSalida(@Valid @ModelAttribute("salidaDto") SalidaStockDto salidaDto,
                                  BindingResult bindingResult,
                                  Principal principal,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.salidaDto", bindingResult);
            redirectAttributes.addFlashAttribute("salidaDto", salidaDto);
            return "redirect:/salidas";
        }

        try {
            movimientoService.registrarSalida(salidaDto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Salida de almacén registrada con éxito (- " + salidaDto.getCantidad() + " unidades retiradas del inventario).");
            return "redirect:/salidas";
        } catch (IllegalArgumentException e) {
            // REGLA: Si se intenta sacar una cantidad mayor al inventario actual, muestra mensaje de error
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("salidaDto", salidaDto);
            return "redirect:/salidas";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error inesperado al procesar la salida: " + e.getMessage());
            redirectAttributes.addFlashAttribute("salidaDto", salidaDto);
            return "redirect:/salidas";
        }
    }
}
