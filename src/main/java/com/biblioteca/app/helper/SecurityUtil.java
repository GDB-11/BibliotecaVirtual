package com.biblioteca.app.helper;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.biblioteca.app.entity.User;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Utilidades de seguridad para manejo de autenticación y autorización.
 */
public class SecurityUtil {

    /**
     * Obtiene el usuario autenticado actualmente
     * 
     * @return Authentication actual o null si no hay usuario autenticado
     */
    public static Authentication getCurrentAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * Verifica si hay un usuario autenticado
     * 
     * @return true si hay un usuario autenticado, false si no
     */
    public static boolean isAuthenticated() {
        Authentication auth = getCurrentAuthentication();
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal());
    }

    /**
     * Obtiene el email del usuario autenticado
     * 
     * @return Email del usuario o null si no está autenticado
     */
    public static String getCurrentUserEmail() {
        Authentication auth = getCurrentAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetails) {
            return ((UserDetails) auth.getPrincipal()).getUsername();
        }
        return null;
    }

    /**
     * Verifica si el usuario actual tiene un rol específico
     * 
     * @param roleName Nombre del rol
     * @return true si tiene el rol, false si no
     */
    public static boolean hasRole(String roleName) {
        Authentication auth = getCurrentAuthentication();
        if (auth == null || auth.getAuthorities() == null) {
            return false;
        }
        
        String roleToCheck = roleName.startsWith("ROLE_") ? roleName : "ROLE_" + roleName;
        return auth.getAuthorities().stream()
            .anyMatch(authority -> authority.getAuthority().equals(roleToCheck));
    }

    /**
     * Verifica si el usuario actual es administrador
     * 
     * @return true si es administrador, false si no
     */
    public static boolean isAdmin() {
        return hasRole("ADMIN");
    }

    /**
     * Verifica si el usuario actual es cliente (User)
     * 
     * @return true si es cliente, false si no
     */
    public static boolean isUser() {
        return hasRole("USER");
    }

    /**
     * Convierte los roles de un usuario a GrantedAuthorities de Spring Security
     * 
     * @param user Usuario del que obtener los roles
     * @return Colección de GrantedAuthorities
     */
    public static Collection<? extends GrantedAuthority> getAuthorities(User user) {
        if (user == null || user.getUserRoles() == null) {
            return java.util.Collections.emptyList();
        }
        
        return user.getUserRoles().stream()
            .map(userRole -> new SimpleGrantedAuthority("ROLE_" + userRole.getRole().getRoleName().toUpperCase()))
            .collect(Collectors.toList());
    }

    /**
     * Crea un token de autenticación para un usuario
     * 
     * @param userDetails Detalles del usuario
     * @param authorities Autoridades del usuario
     * @return Token de autenticación
     */
    public static Authentication createAuthenticationToken(UserDetails userDetails, 
                                                           Collection<? extends GrantedAuthority> authorities) {
        return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
    }

    /**
     * Limpia el contexto de seguridad
     */
    public static void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }
}