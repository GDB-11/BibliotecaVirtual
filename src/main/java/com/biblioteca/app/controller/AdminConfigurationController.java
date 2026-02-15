package com.biblioteca.app.controller;

import com.biblioteca.app.entity.Configuration;
import com.biblioteca.app.entity.enums.ConfigType;
import com.biblioteca.app.service.ConfigurationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controlador para la gestión de configuraciones del sistema.
 */
@Controller
@RequestMapping("/admin/configuracion")
public class AdminConfigurationController {
    
    private final ConfigurationService configurationService;
    
    public AdminConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }
    
    /**
     * Muestra la página de configuración del sistema.
     *
     * @param model El modelo para la vista
     * @return La vista de configuración
     */
    @GetMapping
    public String showConfigurationPage(Model model) {
        try {
            List<Configuration> configurations = configurationService.getAllConfigurations();
            model.addAttribute("configurations", configurations);
            return "admin/configuracion";
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar las configuraciones: " + e.getMessage());
            return "admin/configuracion";
        }
    }
    
    /**
     * Actualiza las configuraciones del sistema.
     *
     * @param configIds Array de IDs de configuración (como strings)
     * @param configKeys Array de claves de configuración
     * @param configValues Array de valores de configuración
     * @param configTypes Array de tipos de configuración (como strings)
     * @param displayNames Array de nombres de visualización
     * @param descriptions Array de descripciones
     * @param redirectAttributes Atributos para redirección
     * @return Redirección a la página de configuración
     */
    @PostMapping
    public String updateConfigurations(
            @RequestParam(value = "configId", required = false) String[] configIds,
            @RequestParam(value = "configKey", required = false) String[] configKeys,
            @RequestParam(value = "configValue", required = false) String[] configValues,
            @RequestParam(value = "configType", required = false) String[] configTypes,
            @RequestParam(value = "displayName", required = false) String[] displayNames,
            @RequestParam(value = "description", required = false) String[] descriptions,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (configIds == null || configIds.length == 0) {
                redirectAttributes.addFlashAttribute("error", "No se encontraron configuraciones para actualizar");
                return "redirect:/admin/configuracion";
            }
            
            for (int i = 0; i < configIds.length; i++) {
                Configuration config = new Configuration();
                config.setConfigurationId(UUID.fromString(configIds[i]));
                config.setConfigKey(configKeys[i]);
                config.setConfigValue(configValues[i]);
                config.setConfigType(ConfigType.valueOf(configTypes[i]));
                config.setDisplayName(displayNames[i]);
                config.setDescription(descriptions[i]);
                
                configurationService.updateConfiguration(config);
            }
            
            redirectAttributes.addFlashAttribute("success", "Configuraciones actualizadas correctamente");
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error de validación: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar las configuraciones: " + e.getMessage());
        }
        
        return "redirect:/admin/configuracion";
    }
}