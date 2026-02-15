package com.biblioteca.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Spring MVC.
 * Define rutas de recursos estáticos y view controllers.
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * Configura manejadores de recursos estáticos (CSS, JS, imágenes)
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Recursos estáticos generales
        registry.addResourceHandler("/css/**")
            .addResourceLocations("classpath:/static/css/");
        
        registry.addResourceHandler("/js/**")
            .addResourceLocations("classpath:/static/js/");
        
        registry.addResourceHandler("/images/**")
            .addResourceLocations("classpath:/static/images/");
        
        registry.addResourceHandler("/fonts/**")
            .addResourceLocations("classpath:/static/fonts/");
        
        // Recursos estáticos del admin
        registry.addResourceHandler("/admin/css/**")
            .addResourceLocations("classpath:/static/admin/css/");
        
        registry.addResourceHandler("/admin/js/**")
            .addResourceLocations("classpath:/static/admin/js/");
        
        registry.addResourceHandler("/admin/images/**")
            .addResourceLocations("classpath:/static/admin/images/");
    }

    /**
     * Configura view controllers para páginas simples sin lógica
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // Página principal cliente
        registry.addViewController("/").setViewName("client/index");
        
        // Páginas de error personalizadas
        registry.addViewController("/error/403").setViewName("error/403");
        registry.addViewController("/error/404").setViewName("error/404");
        registry.addViewController("/error/500").setViewName("error/500");
    }
}