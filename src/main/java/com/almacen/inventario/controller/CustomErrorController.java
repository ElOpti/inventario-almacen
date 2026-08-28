package com.almacen.inventario.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @GetMapping("/error/403")
    public String accessDenied() {
        return "error/403";
    }

    @GetMapping("/error")
    public String handleError() {
        return "error/403";
    }
}
