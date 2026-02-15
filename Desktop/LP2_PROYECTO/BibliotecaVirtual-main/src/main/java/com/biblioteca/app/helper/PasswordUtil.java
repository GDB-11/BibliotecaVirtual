package com.biblioteca.app.helper;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Utilidad para hash y verificación de contraseñas.
 * Usa BCrypt para seguridad.
 */
public class PasswordUtil {

    private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Genera un hash seguro de la contraseña usando BCrypt
     * 
     * @param plainPassword Contraseña en texto plano
     * @return Hash de la contraseña
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía");
        }
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Verifica si una contraseña coincide con su hash
     * 
     * @param plainPassword Contraseña en texto plano
     * @param hashedPassword Hash almacenado en la base de datos
     * @return true si coinciden, false si no
     */
    public static boolean verifyPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null) {
            return false;
        }
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }

    /**
     * Obtiene la instancia del PasswordEncoder
     * 
     * @return PasswordEncoder configurado
     */
    public static PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }
}