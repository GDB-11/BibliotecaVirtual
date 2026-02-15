package com.biblioteca.app.controller;

import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.dto.user.UserData;
import com.biblioteca.app.dto.user.UserDTO;
import com.biblioteca.app.entity.Role;
import com.biblioteca.app.entity.Status;
import com.biblioteca.app.entity.User;
import com.biblioteca.app.service.ConfigurationService;
import com.biblioteca.app.service.RoleService;
import com.biblioteca.app.service.StatusService;
import com.biblioteca.app.service.UserService;
import com.biblioteca.app.helper.PasswordUtil;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controlador para la gestión de usuarios en el panel de administración.
 * Maneja el CRUD completo de usuarios, asignación de roles y gestión de estados.
 */
@Controller
@RequestMapping("/admin/usuarios")
public class AdminUserController {
    
    private final UserService userService;
    private final RoleService roleService;
    private final StatusService statusService;
    private final ConfigurationService configurationService;
    
    public AdminUserController(UserService userService, 
                              RoleService roleService,
                              StatusService statusService,
                              ConfigurationService configurationService) {
        this.userService = userService;
        this.roleService = roleService;
        this.statusService = statusService;
        this.configurationService = configurationService;
    }
    
    /**
     * Muestra el listado de usuarios con paginación y filtros.
     */
    @GetMapping
    public String listUsers(
            @RequestParam(value = "p", defaultValue = "1") int page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "roleId", required = false) String roleId,
            @RequestParam(value = "statusId", required = false) String statusId,
            Model model) {
        
        try {
            int itemsPerPage = size != null ? size : configurationService.getIntValue("ItemsPerPage", 15);
            
            UUID roleUuid = (roleId != null && !roleId.isEmpty()) ? UUID.fromString(roleId) : null;
            UUID statusUuid = (statusId != null && !statusId.isEmpty()) ? UUID.fromString(statusId) : null;
            
            PagedResult<UserData> usersResult = userService.getRegisteredUsers(
                page, itemsPerPage, search, roleUuid, statusUuid);
            
            List<Role> roles = roleService.findAll();
            List<Status> statuses = statusService.findAll();
            
            long totalUsers = userService.getTotalUsersCount();
            long activeUsersCount = userService.getActiveUsersCount();            
            long adminCount = userService.getActiveAdminsCount();
            
            model.addAttribute("usersResult", usersResult);
            model.addAttribute("roles", roles);
            model.addAttribute("statuses", statuses);
            model.addAttribute("totalUsers", totalUsers);
            model.addAttribute("activeUsersCount", activeUsersCount);
            model.addAttribute("adminCount", adminCount);
            
            model.addAttribute("searchValue", search != null ? search : "");
            model.addAttribute("roleIdValue", roleId != null ? roleId : "");
            model.addAttribute("statusIdValue", statusId != null ? statusId : "");
            model.addAttribute("itemsPerPage", itemsPerPage);
            
            return "admin/usuarios";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar usuarios: " + e.getMessage());
            return "admin/usuarios";
        }
    }
    
    /**
     * Muestra el formulario para crear un nuevo usuario.
     */
    @GetMapping("/nuevo")
    public String newUserForm(Model model) {
        try {
            UserDTO userDTO = new UserDTO();
            
            List<Role> roles = roleService.findAll();
            List<Status> statuses = statusService.findAll();
            
            model.addAttribute("user", userDTO);
            model.addAttribute("roles", roles);
            model.addAttribute("statuses", statuses);
            model.addAttribute("isNew", true);
            model.addAttribute("isReadOnly", false);
            
            return "admin/usuario-form";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }
    
    /**
     * Muestra el formulario para editar un usuario existente.
     */
    @GetMapping("/{id}/editar")
    public String editUserForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID userId = UUID.fromString(id);
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            UserDTO userDTO = userService.mapToDTO(user);
            
            List<Role> roles = roleService.findAll();
            List<Status> statuses = statusService.findAll();
            
            List<UUID> assignedRoleIds = user.getRoles().stream()
                .map(Role::getRoleId)
                .toList();
            
            model.addAttribute("user", userDTO);
            model.addAttribute("userEntity", user);
            model.addAttribute("roles", roles);
            model.addAttribute("statuses", statuses);
            model.addAttribute("assignedRoleIds", assignedRoleIds);
            model.addAttribute("isNew", false);
            model.addAttribute("isReadOnly", false);
            
            return "admin/usuario-form";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }
    
    /**
     * Muestra los detalles de un usuario (solo lectura).
     */
    @GetMapping("/{id}")
    public String viewUser(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID userId = UUID.fromString(id);
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            UserDTO userDTO = userService.mapToDTO(user);
            
            List<Role> roles = roleService.findAll();
            List<Status> statuses = statusService.findAll();
            
            List<UUID> assignedRoleIds = user.getRoles().stream()
                .map(Role::getRoleId)
                .toList();
            
            model.addAttribute("user", userDTO);
            model.addAttribute("userEntity", user);
            model.addAttribute("roles", roles);
            model.addAttribute("statuses", statuses);
            model.addAttribute("assignedRoleIds", assignedRoleIds);
            model.addAttribute("isNew", false);
            model.addAttribute("isReadOnly", true);
            
            return "admin/usuario-form";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el usuario: " + e.getMessage());
            return "redirect:/admin/usuarios";
        }
    }
    
    /**
     * Crea un nuevo usuario.
     */
    @PostMapping
    public String createUser(
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String statusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("El email es requerido");
            }
            
            if (password == null || password.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }
            
            if (userService.existsByEmail(email)) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
            
            UUID statusUuid;
            if (statusId != null && !statusId.isEmpty()) {
                statusUuid = UUID.fromString(statusId);
            } else {
                statusUuid = statusService.getActiveStatusId();
            }
            
            userService.register(email.trim(), password, statusUuid);
            
            redirectAttributes.addFlashAttribute("success", "Usuario creado exitosamente");
            return "redirect:/admin/usuarios";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear usuario: " + e.getMessage());
            return "redirect:/admin/usuarios/nuevo";
        }
    }
    
    /**
     * Actualiza un usuario existente.
     */
    @PostMapping("/{id}")
    public String updateUser(
            @PathVariable String id,
            @RequestParam String email,
            @RequestParam String statusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            UUID userId = UUID.fromString(id);
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("El email es requerido");
            }
            
            if (!email.equals(user.getEmail()) && userService.existsByEmail(email)) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
            
            user.setEmail(email.trim());
            
            if (statusId != null && !statusId.isEmpty()) {
                UUID statusUuid = UUID.fromString(statusId);
                Status status = statusService.findById(statusUuid)
                    .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
                user.setStatus(status);
            }
            
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Usuario actualizado exitosamente");
            return "redirect:/admin/usuarios";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar usuario: " + e.getMessage());
            return "redirect:/admin/usuarios/" + id + "/editar";
        }
    }
    
    /**
     * Elimina un usuario.
     */
    @PostMapping("/{id}/eliminar")
    public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            UUID userId = UUID.fromString(id);
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("success", "Usuario eliminado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar usuario: " + e.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
    
    /**
     * Cambia la contraseña de un usuario.
     */
    @PostMapping("/{id}/cambiar-password")
    public String changePassword(
            @PathVariable String id,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (newPassword == null || newPassword.length() < 6) {
                throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
            }
            
            UUID userId = UUID.fromString(id);
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            
            user.setPassword(PasswordUtil.hashPassword(newPassword));
            userService.updateUser(user);
            
            redirectAttributes.addFlashAttribute("success", "Contraseña actualizada exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cambiar contraseña: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios/" + id + "/editar";
    }
    
    /**
     * Asigna un rol a un usuario.
     */
    @PostMapping("/{id}/asignar-rol")
    public String assignRole(
            @PathVariable String id,
            @RequestParam String roleId,
            RedirectAttributes redirectAttributes) {
        
        try {
            UUID userId = UUID.fromString(id);
            UUID roleUuid = UUID.fromString(roleId);
            userService.assignRole(userId, roleUuid);
            redirectAttributes.addFlashAttribute("success", "Rol asignado exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al asignar rol: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios/" + id + "/editar";
    }
    
    /**
     * Remueve un rol de un usuario.
     */
    @PostMapping("/{id}/remover-rol")
    public String removeRole(
            @PathVariable String id,
            @RequestParam String roleId,
            RedirectAttributes redirectAttributes) {
        
        try {
            UUID userId = UUID.fromString(id);
            UUID roleUuid = UUID.fromString(roleId);
            userService.removeRole(userId, roleUuid);
            redirectAttributes.addFlashAttribute("success", "Rol removido exitosamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al remover rol: " + e.getMessage());
        }
        
        return "redirect:/admin/usuarios/" + id + "/editar";
    }
}