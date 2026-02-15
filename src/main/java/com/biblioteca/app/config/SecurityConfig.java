package com.biblioteca.app.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.biblioteca.app.helper.PasswordUtil;
import com.biblioteca.app.service.CustomUserDetailsService;

/**
 * Configuración de seguridad de Spring Security.
 * Define dos cadenas de filtros: una para admin y otra para clientes.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    /**
     * Password encoder para BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordUtil.getPasswordEncoder();
    }

    /**
     * Security Filter Chain para el área de ADMINISTRACIÓN
     * Tiene mayor prioridad (Order 1)
     */
    @Bean
    @Order(1)
    public SecurityFilterChain adminSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/admin/**")
<<<<<<< HEAD
            .userDetailsService(userDetailsService)
=======
            .userDetailsService(userDetailsService)  // ✅ AÑADE ESTO
>>>>>>> 41bd2a27dfbd5dbd952243f53e161ae61b1b837d
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/admin/login", "/admin/css/**", "/admin/js/**", "/admin/images/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/admin/login")
                .loginProcessingUrl("/admin/login")
                .defaultSuccessUrl("/admin/dashboard", true)
                .failureUrl("/admin/login?error=true")
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/admin/logout")
                .logoutSuccessUrl("/admin/login?logout=success")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.sendRedirect("/admin/login?error=unauthorized");
                })
            )
            .sessionManagement(session -> session
<<<<<<< HEAD
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
=======
                .sessionConcurrency(concurrency -> concurrency
                    .maximumSessions(1)
                    .maxSessionsPreventsLogin(false)
                )
>>>>>>> 41bd2a27dfbd5dbd952243f53e161ae61b1b837d
            );

        return http.build();
    }

    /**
     * Security Filter Chain para el área de CLIENTES
     * Tiene menor prioridad (Order 2)
     */
    @Bean
    @Order(2)
    public SecurityFilterChain clientSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/**")
<<<<<<< HEAD
            .userDetailsService(userDetailsService)
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas
                .requestMatchers("/", "/login", "/user/login", "/register", "/registro", 
                               "/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                .requestMatchers("/books/**", "/authors/**", "/catalogo/**").permitAll()
                .requestMatchers("/user/registro", "/user/register").permitAll()
                
                // Rutas de usuario autenticado
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .requestMatchers("/rentals/**", "/profile/**").hasAnyRole("USER", "ADMIN")
                
                // Cualquier otra ruta requiere autenticación
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/user/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/user/dashboard", true)
                .failureUrl("/user/login?error=true")
=======
            .userDetailsService(userDetailsService)  // ✅ AÑADE ESTO
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/books/**", "/authors/**").permitAll()
                .requestMatchers("/rentals/**", "/profile/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/", true)
                .failureUrl("/login?error=true")
>>>>>>> 41bd2a27dfbd5dbd952243f53e161ae61b1b837d
                .usernameParameter("email")
                .passwordParameter("password")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
<<<<<<< HEAD
                .logoutSuccessUrl("/user/login?logout=success")
=======
                .logoutSuccessUrl("/login?logout=success")
>>>>>>> 41bd2a27dfbd5dbd952243f53e161ae61b1b837d
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
<<<<<<< HEAD
                .maximumSessions(2)
                .maxSessionsPreventsLogin(false)
=======
                .sessionConcurrency(concurrency -> concurrency
                    .maximumSessions(2)
                    .maxSessionsPreventsLogin(false)
                )
>>>>>>> 41bd2a27dfbd5dbd952243f53e161ae61b1b837d
            );

        return http.build();
    }
}