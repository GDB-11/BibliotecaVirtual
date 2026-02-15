package com.biblioteca.app.controller.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.service.RentalService;

/**
 * Controlador para la gestión de alquileres en el panel de administración.
 */
@Controller
@RequestMapping("/admin")
public class AdminAlquilerController {
    
    private final RentalService rentalService;
    
    public AdminAlquilerController(RentalService rentalService) {
        this.rentalService = rentalService;
    }
    
    /**
     * Muestra la lista completa de alquileres con filtros.
     */
    @GetMapping("/alquileres")
    public String listarAlquileres(
            @RequestParam(value = "search", required = false, defaultValue = "") String search,
            @RequestParam(value = "estado", required = false, defaultValue = "") String estado,
            @RequestParam(value = "fechaDesde", required = false, defaultValue = "") String fechaDesde,
            Model model) {
        
        // Obtener todos los alquileres
        List<RentalCompleteDTO> todosAlquileres = rentalService.getAllRentals();
        
        // Aplicar filtros
        List<RentalCompleteDTO> alquileresFiltrados = todosAlquileres.stream()
            .filter(rental -> filtrarPorBusqueda(rental, search))
            .filter(rental -> filtrarPorEstado(rental, estado))
            .filter(rental -> filtrarPorFecha(rental, fechaDesde))
            .collect(Collectors.toList());
        
        // Calcular estadísticas
        long activos = alquileresFiltrados.stream()
            .filter(r -> "En Proceso".equals(r.getEstadoAlquiler()))
            .count();
        
        long porVencer = alquileresFiltrados.stream()
            .filter(r -> "En Proceso".equals(r.getEstadoAlquiler()))
            .filter(r -> r.getFechaVencimiento() != null && 
                         r.getFechaVencimiento().isBefore(LocalDateTime.now().plusDays(3)))
            .count();
        
        // Agregar atributos al modelo
        model.addAttribute("alquileres", alquileresFiltrados);
        model.addAttribute("totalAlquileres", alquileresFiltrados.size());
        model.addAttribute("activos", activos);
        model.addAttribute("porVencer", porVencer);
        
        // Mantener los valores de los filtros en el formulario
        model.addAttribute("searchValue", search);
        model.addAttribute("estadoValue", estado);
        model.addAttribute("fechaDesdeValue", fechaDesde);
        
        return "admin/alquileres/lista";
    }
    
    /**
     * Filtra por búsqueda en usuario o libro
     */
    private boolean filtrarPorBusqueda(RentalCompleteDTO rental, String search) {
        if (search == null || search.trim().isEmpty()) {
            return true;
        }
        String searchLower = search.toLowerCase().trim();
        return (rental.getUsuario() != null && rental.getUsuario().toLowerCase().contains(searchLower)) ||
               (rental.getLibro() != null && rental.getLibro().toLowerCase().contains(searchLower));
    }
    
    /**
     * Filtra por estado del alquiler
     */
    private boolean filtrarPorEstado(RentalCompleteDTO rental, String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return true;
        }
        
        String estadoAlquiler = rental.getEstadoAlquiler();
        if (estadoAlquiler == null) return false;
        
        switch (estado) {
            case "activo":
                return "En Proceso".equals(estadoAlquiler);
            case "devuelto":
                return "Devuelto".equals(estadoAlquiler);
            case "cancelado":
                return "Cancelado".equals(estadoAlquiler);
            default:
                return true;
        }
    }
    
    /**
     * Filtra por fecha desde
     */
    private boolean filtrarPorFecha(RentalCompleteDTO rental, String fechaDesde) {
        if (fechaDesde == null || fechaDesde.trim().isEmpty()) {
            return true;
        }
        
        try {
            LocalDate fechaFiltro = LocalDate.parse(fechaDesde);
            LocalDate fechaAlquiler = rental.getFechaAlquiler().toLocalDate();
            return !fechaAlquiler.isBefore(fechaFiltro);
        } catch (Exception e) {
            return true;
        }
    }
}