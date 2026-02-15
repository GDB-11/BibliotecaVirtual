package com.biblioteca.app.controller.user;

import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.biblioteca.app.entity.User;
import com.biblioteca.app.service.RoleService;
import com.biblioteca.app.service.StatusService;
import com.biblioteca.app.service.UserService;

@Controller
public class UserAuthController {

    private final UserService userService;
    private final StatusService statusService;
    private final RoleService roleService;
    
    // ✅ CONSTRUCTOR CORREGIDO - Ahora incluye RoleService
    public UserAuthController(
            UserService userService, 
            StatusService statusService,
            RoleService roleService) {
        this.userService = userService;
        this.statusService = statusService;
        this.roleService = roleService;
    }
    
    @GetMapping("/user/login")
    public String loginPage() {
        return "user/login";
    }
    
    @GetMapping("/user/registro")
    public String registroPage() {
        return "user/registro";
    }
    
    @PostMapping("/user/registro")
    public String registrarUsuario(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validar que las contraseñas coincidan
            if (!password.equals(confirmPassword)) {
                redirectAttributes.addAttribute("error", "passwords");
                return "redirect:/user/registro?error";
            }
            
            // Buscar estado "Active"
            UUID activeStatusId = statusService.findByStatusName("Active")
                .orElseThrow(() -> new RuntimeException("Estado no encontrado"))
                .getStatusId();
            
            // Registrar usuario
            User nuevoUsuario = userService.register(email, password, activeStatusId);
            
            // Asignar rol USER automáticamente - ✅ CORREGIDO (usa findByName en lugar de findByRoleName)
            UUID roleUserId = roleService.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Rol USER no encontrado"))
                .getRoleId();
            userService.assignRole(nuevoUsuario.getUserId(), roleUserId);
            
            return "redirect:/user/login?success";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addAttribute("error", e.getMessage());
            return "redirect:/user/registro?error";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addAttribute("error", "Error en el registro");
            return "redirect:/user/registro?error";
        }
    }
}