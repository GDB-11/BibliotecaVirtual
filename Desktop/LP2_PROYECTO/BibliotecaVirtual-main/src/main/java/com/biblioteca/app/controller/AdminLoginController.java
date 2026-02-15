package com.biblioteca.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.app.helper.SecurityUtil;

/**
 * Controlador para el login del panel de administración.
 * Spring Security maneja la autenticación, este controlador solo muestra la vista.
 */
@Controller
@RequestMapping("/admin")
public class AdminLoginController {

    /**
     * Muestra la página de login del admin.
     * Si ya está autenticado como admin, redirige al dashboard.
     * 
     * @param error Parámetro que indica si hubo error de autenticación
     * @param logout Parámetro que indica si se cerró sesión exitosamente
     * @param unauthorized Parámetro que indica acceso no autorizado
     * @param model Modelo para pasar datos a la vista
     * @return Vista de login o redirección al dashboard
     */
    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "unauthorized", required = false) String unauthorized,
            @RequestParam(value = "forbidden", required = false) String forbidden,
            Model model) {
        
        if (SecurityUtil.isAuthenticated() && SecurityUtil.isAdmin()) {
            return "redirect:/admin/dashboard";
        }
        
        if (error != null) {
            if ("true".equals(error)) {
                model.addAttribute("error", "Email o contraseña incorrectos");
            } else if ("unauthorized".equals(error)) {
                model.addAttribute("error", "No tiene permisos de administrador");
            }
        }
        
        if (logout != null && "success".equals(logout)) {
            model.addAttribute("success", "Sesión cerrada correctamente");
        }
        
        if (unauthorized != null) {
            model.addAttribute("warning", "No tiene permisos de administrador. Su sesión ha sido cerrada.");
        }
        
        if (forbidden != null) {
            model.addAttribute("warning", "No tiene credenciales para ingresar al panel. Acceso prohibido.");
        }
        
        return "admin/login";
    }
}