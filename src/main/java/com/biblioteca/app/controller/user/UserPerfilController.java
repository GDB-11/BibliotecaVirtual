package com.biblioteca.app.controller.user;

import java.security.Principal;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.biblioteca.app.entity.User;
import com.biblioteca.app.service.UserService;

@Controller
@RequestMapping("/user/perfil")
public class UserPerfilController {
    
    private final UserService userService;
    
    public UserPerfilController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping
    public String verPerfil(Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        model.addAttribute("user", user);
        return "user/perfil/ver";
    }
    
    @GetMapping("/editar")
    public String formEditarPerfil(Principal principal, Model model) {
        String email = principal.getName();
        User user = userService.getUserByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
        
        model.addAttribute("user", user);
        return "user/perfil/editar";
    }
    
    @PostMapping("/editar")
    public String actualizarPerfil(
            Principal principal,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {
        
        try {
            User user = userService.getUserByEmail(principal.getName()).orElseThrow();
            user.setEmail(email);
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        
        return "redirect:/user/perfil";
    }
    
    @GetMapping("/cambiar-password")
    public String formCambiarPassword() {
        return "user/perfil/cambiar-password";
    }
    
    @PostMapping("/cambiar-password")
    public String cambiarPassword(
            Principal principal,
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (!newPassword.equals(confirmPassword)) {
                throw new IllegalArgumentException("Las contraseñas no coinciden");
            }
            
            User user = userService.getUserByEmail(principal.getName()).orElseThrow();
            userService.changePassword(user.getUserId(), currentPassword, newPassword);
            
            redirectAttributes.addFlashAttribute("success", "Contraseña cambiada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }
        
        return "redirect:/user/perfil";
    }
}