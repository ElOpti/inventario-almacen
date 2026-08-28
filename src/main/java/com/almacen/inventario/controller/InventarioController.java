package com.almacen.inventario.controller;

import com.almacen.inventario.dto.EntradaStockDto;
import com.almacen.inventario.dto.ProductoDto;
import com.almacen.inventario.model.EstadoProducto;
import com.almacen.inventario.model.Producto;
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
@RequestMapping("/inventario")
public class InventarioController {

    private final ProductoService productoService;

    public InventarioController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public String listar(@RequestParam(value = "filtro", defaultValue = "TODOS") String filtro,
                         Model model) {
        List<Producto> productos;
        if ("ACTIVOS".equalsIgnoreCase(filtro)) {
            productos = productoService.listarPorEstado(EstadoProducto.ACTIVO);
        } else if ("INACTIVOS".equalsIgnoreCase(filtro)) {
            productos = productoService.listarPorEstado(EstadoProducto.INACTIVO);
        } else {
            productos = productoService.listarTodos();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("filtroActual", filtro.toUpperCase());
        model.addAttribute("entradaDto", new EntradaStockDto());
        return "inventario/lista";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @GetMapping("/nuevo")
    public String nuevoProductoForm(Model model) {
        if (!model.containsAttribute("productoDto")) {
            model.addAttribute("productoDto", new ProductoDto());
        }
        return "inventario/nuevo";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @PostMapping("/guardar")
    public String guardarProducto(@Valid @ModelAttribute("productoDto") ProductoDto productoDto,
                                  BindingResult bindingResult,
                                  RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "inventario/nuevo";
        }

        try {
            Producto guardado = productoService.crearProducto(productoDto);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Producto '" + guardado.getNombre() + "' (" + guardado.getCodigo() + ") registrado exitosamente con stock inicial en 0.");
            return "redirect:/inventario";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("productoDto", productoDto);
            return "redirect:/inventario/nuevo";
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @PostMapping("/entrada")
    public String registrarEntrada(@ModelAttribute("entradaDto") EntradaStockDto entradaDto,
                                   Principal principal,
                                   RedirectAttributes redirectAttributes) {
        if (entradaDto.getCantidad() == null || entradaDto.getCantidad() <= 0) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Error de Entrada: La cantidad debe ser un número positivo mayor a 0. No es posible disminuir stock en este módulo.");
            return "redirect:/inventario";
        }

        try {
            productoService.registrarEntrada(entradaDto, principal.getName());
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Entrada de inventario registrada con éxito (+ " + entradaDto.getCantidad() + " unidades agregadas al stock).");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ocurrió un error inesperado al registrar la entrada: " + e.getMessage());
        }

        return "redirect:/inventario";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @PostMapping("/estado/{id}")
    public String cambiarEstado(@PathVariable("id") Long id,
                                @RequestParam("nuevoEstado") EstadoProducto nuevoEstado,
                                RedirectAttributes redirectAttributes) {
        try {
            Producto producto = productoService.cambiarEstado(id, nuevoEstado);
            String accion = EstadoProducto.ACTIVO.equals(nuevoEstado) ? "reactivado" : "dado de baja (inactivo)";
            redirectAttributes.addFlashAttribute("successMessage", 
                    "El producto '" + producto.getNombre() + "' ha sido " + accion + " correctamente.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/inventario";
    }
}
