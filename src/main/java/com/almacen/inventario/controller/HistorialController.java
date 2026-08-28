package com.almacen.inventario.controller;

import com.almacen.inventario.model.Movimiento;
import com.almacen.inventario.model.TipoMovimiento;
import com.almacen.inventario.service.MovimientoService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/historial")
@PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
public class HistorialController {

    private final MovimientoService movimientoService;

    public HistorialController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @GetMapping
    public String verHistorial(@RequestParam(value = "tipo", defaultValue = "TODOS") String tipo,
                              Model model) {
        TipoMovimiento tipoFiltro = null;
        if ("ENTRADA".equalsIgnoreCase(tipo)) {
            tipoFiltro = TipoMovimiento.ENTRADA;
        } else if ("SALIDA".equalsIgnoreCase(tipo)) {
            tipoFiltro = TipoMovimiento.SALIDA;
        }

        List<Movimiento> movimientos = movimientoService.listarHistorial(tipoFiltro);
        model.addAttribute("movimientos", movimientos);
        model.addAttribute("filtroActual", tipo.toUpperCase());

        return "historial/lista";
    }
}
