package com.biblioteca.app.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.entity.User;
import com.biblioteca.app.helper.SecurityUtil;
import com.biblioteca.app.repository.UserRepository;

import java.util.Collection;

/**
 * Servicio personalizado para cargar detalles de usuario en Spring Security.
 * Implementa UserDetailsService para integración con el sistema de autenticación.
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Carga un usuario por su email (username en Spring Security)
     * 
     * @param email Email del usuario
     * @return UserDetails con la información del usuario
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
        
        if (!"Active".equalsIgnoreCase(user.getStatus().getStatusName())) {
            throw new UsernameNotFoundException("La cuenta no está activa: " + email);
        }
        
        Collection<? extends GrantedAuthority> authorities = SecurityUtil.getAuthorities(user);
        
        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getEmail())
            .password(user.getPassword())
            .authorities(authorities)
            .accountExpired(false)
            .accountLocked(false)
            .credentialsExpired(false)
            .disabled(!"Active".equalsIgnoreCase(user.getStatus().getStatusName()))
            .build();
    }

    /**
     * Carga un usuario completo (entidad) por email
     * Útil cuando necesitamos acceso a todos los datos del usuario
     * 
     * @param email Email del usuario
     * @return Entidad User completa
     * @throws UsernameNotFoundException si el usuario no existe
     */
    public User loadUserEntityByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
    }
}