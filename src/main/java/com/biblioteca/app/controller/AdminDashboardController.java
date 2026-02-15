package com.biblioteca.app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.app.dto.rental.BookMostRequestedDTO;
import com.biblioteca.app.dto.rental.BookRentalStatsDTO;
import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Category;
import com.biblioteca.app.entity.Country;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.entity.Status;
import com.biblioteca.app.service.AuthorService;
import com.biblioteca.app.service.BookCopyService;
import com.biblioteca.app.service.BookService;
import com.biblioteca.app.service.CategoryService;
import com.biblioteca.app.service.ConfigurationService;
import com.biblioteca.app.service.CountryService;
import com.biblioteca.app.service.RentalService;
import com.biblioteca.app.service.StatusService;

/**
 * Controlador para el dashboard del panel de administración
 */
@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCopyService bookCopyService;

    @Autowired
    private RentalService rentalService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CountryService countryService;

    @Autowired
    private StatusService statusService;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Muestra el dashboard principal del admin
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Estadísticas generales
        long totalBooks = bookService.count();
        long totalAuthors = authorService.count();
        long rentedCopies = bookCopyService.countRentedCopies();
        long availableCopies = bookCopyService.countAvailableCopies();
        
        // Alquileres recientes (últimos 10)
        List<Rental> recentRentals = rentalService.getRecentRentals(10);
        
        // Top 5 libros más pedidos
        List<BookRentalStatsDTO> topBooks = rentalService.getTopRequestedBooks(5);
        
        // Agregar atributos al modelo
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalAuthors", totalAuthors);
        model.addAttribute("rentedCopies", rentedCopies);
        model.addAttribute("availableCopies", availableCopies);
        model.addAttribute("recentRentals", recentRentals);
        model.addAttribute("topBooks", topBooks);
        
        return "admin/dashboard";
    }

    /**
     * Muestra el reporte de libros actualmente en alquiler
     */
    @GetMapping("/libros-alquiler")
    public String showActiveRentalsReport(
            @RequestParam(value = "p", defaultValue = "1") int page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "status", required = false) String statusFilter,
            @RequestParam(value = "dateFrom", required = false) String dateFrom,
            @RequestParam(value = "dateTo", required = false) String dateTo,
            Model model) {
        
        try {
            int itemsPerPage = size != null ? size : configurationService.getIntValue("ItemsPerPage", 15);
            int dueSoonDays = configurationService.getIntValue("RentalRiskDays", 1);
            
            PagedResult<Rental> activeRentals = rentalService.findActiveRentals(
                page, itemsPerPage, search, statusFilter, dueSoonDays, dateFrom, dateTo);
            
            int totalActiveRentals = rentalService.getActiveRentalsCount();
            int onTimeRentals = rentalService.getOnTimeRentalsCount(dueSoonDays);
            int dueSoonRentals = rentalService.getDueSoonRentalsCount(dueSoonDays);
            int overdueRentals = rentalService.getOverdueRentalsCount();
            
            model.addAttribute("activeRentals", activeRentals);
            model.addAttribute("totalActiveRentals", totalActiveRentals);
            model.addAttribute("onTimeRentals", onTimeRentals);
            model.addAttribute("dueSoonRentals", dueSoonRentals);
            model.addAttribute("overdueRentals", overdueRentals);
            model.addAttribute("dueSoonDays", dueSoonDays);
            
            model.addAttribute("searchValue", search != null ? search : "");
            model.addAttribute("statusValue", statusFilter != null ? statusFilter : "");
            model.addAttribute("dateFromValue", dateFrom != null ? dateFrom : "");
            model.addAttribute("dateToValue", dateTo != null ? dateTo : "");
            
            return "admin/libros-alquiler";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el reporte: " + e.getMessage());
            return "admin/libros-alquiler";
        }
    }

    /**
     * Obtiene los detalles de un alquiler para el modal
     */
    @GetMapping("/libros-alquiler/detalle")
    public String getRentalDetail(
            @RequestParam("id") String rentalId,
            Model model) {
        
        try {
            java.util.UUID uuid = java.util.UUID.fromString(rentalId);
            Rental rental = rentalService.findById(uuid)
                .orElseThrow(() -> new IllegalArgumentException("Alquiler no encontrado"));
            
            // Obtener tarifa de penalidad desde configuracion
            double penaltyRate = configurationService.getDecimalValue("ReturnDelayDailyPenalty", 12.5);
            
            model.addAttribute("rental", rental);
            model.addAttribute("penaltyRate", penaltyRate);
            
            return "admin/fragments/rental-detail :: rental-detail";
            
        } catch (IllegalArgumentException e) {
            model.addAttribute("rental", null);
            model.addAttribute("error", e.getMessage());
            return "admin/fragments/rental-detail :: rental-detail";
        } catch (Exception e) {
            model.addAttribute("rental", null);
            model.addAttribute("error", "Error al cargar los detalles: " + e.getMessage());
            return "admin/fragments/rental-detail :: rental-detail";
        }
    }

    /**
     * Muestra el reporte de libros más pedidos
     */
    @GetMapping("/libros-pedidos")
    public String showMostRequestedBooksReport(
            @RequestParam(value = "p", defaultValue = "1") int page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            Model model) {
        
        try {
            int itemsPerPage = size != null ? size : configurationService.getIntValue("ItemsPerPage", 20);
            
            // Convertir string vacío a null para el filtro
            String categoryFilter = (categoryId != null && !categoryId.trim().isEmpty()) ? categoryId : null;
            
            // Obtener libros más pedidos paginados
            PagedResult<BookMostRequestedDTO> booksStats = rentalService.getMostRequestedBooks(
                page, itemsPerPage, categoryFilter);
            
            // Obtener todas las categorías para el filtro
            List<Category> categories = categoryService.findAll();
            
            // Agregar atributos al modelo
            model.addAttribute("booksStats", booksStats);
            model.addAttribute("categories", categories);
            model.addAttribute("categoryIdValue", categoryId != null ? categoryId : "");
            model.addAttribute("pageSizeValue", itemsPerPage);
            
            return "admin/libros-pedidos";
            
        } catch (Exception e) {
            // Crear un PagedResult vacío para evitar errores en la vista
            PagedResult<BookMostRequestedDTO> emptyResult = new PagedResult<>(new ArrayList<>(), 1, size != null ? size : 20, 0);
            
            model.addAttribute("booksStats", emptyResult);
            model.addAttribute("categories", new ArrayList<Category>());
            model.addAttribute("categoryIdValue", categoryId != null ? categoryId : "");
            model.addAttribute("pageSizeValue", size != null ? size : 20);
            model.addAttribute("error", "Error al cargar el reporte: " + e.getMessage());
            
            return "admin/libros-pedidos";
        }
    }

    /**
     * Muestra el reporte de autores más pedidos
     */
    @GetMapping("/autores-pedidos")
    public String showMostRequestedAuthorsReport(
            @RequestParam(value = "countryId", required = false) String countryId,
            @RequestParam(value = "statusId", required = false) String statusId,
            @RequestParam(value = "top", defaultValue = "20") int top,
            Model model) {
        
        try {
            // Validar límite
            if (top < 10) top = 10;
            if (top > 100) top = 100;
            
            // Convertir strings vacíos a null
            String countryFilter = (countryId != null && !countryId.trim().isEmpty()) ? countryId : null;
            String statusFilter = (statusId != null && !statusId.trim().isEmpty()) ? statusId : null;
            
            // Convertir a UUIDs si no son null
            UUID countryUuid = null;
            UUID statusUuid = null;
            
            if (countryFilter != null) {
                countryUuid = UUID.fromString(countryFilter);
            }
            if (statusFilter != null) {
                statusUuid = UUID.fromString(statusFilter);
            }
            
            // Obtener autores más pedidos
            List<com.biblioteca.app.dto.author.AuthorStatsDTO> topAuthors = 
                authorService.getMostRequestedAuthors(countryUuid, statusUuid, top);
            
            // Obtener listas para filtros
            List<Country> countries = countryService.findAll();
            List<Status> statuses = statusService.findAll();
            
            // Estadísticas generales
            long totalAuthorsWithRentals = authorService.countAuthorsWithRentals();
            long totalRentals = authorService.getTotalAuthorsRentals();
            long totalAuthors = authorService.count();
            
            double avgRentalsPerAuthor = totalAuthorsWithRentals > 0 
                ? (double) totalRentals / totalAuthorsWithRentals 
                : 0.0;
            
            // Agregar atributos al modelo
            model.addAttribute("topAuthors", topAuthors);
            model.addAttribute("countries", countries);
            model.addAttribute("statuses", statuses);
            model.addAttribute("totalAuthorsWithRentals", totalAuthorsWithRentals);
            model.addAttribute("totalRentals", totalRentals);
            model.addAttribute("totalAuthors", totalAuthors);
            model.addAttribute("avgRentalsPerAuthor", avgRentalsPerAuthor);
            model.addAttribute("countryIdValue", countryId != null ? countryId : "");
            model.addAttribute("statusIdValue", statusId != null ? statusId : "");
            model.addAttribute("topValue", top);
            
            return "admin/autores-pedidos";
            
        } catch (Exception e) {
            // Manejo de errores
            model.addAttribute("topAuthors", new java.util.ArrayList<>());
            model.addAttribute("countries", new java.util.ArrayList<>());
            model.addAttribute("statuses", new java.util.ArrayList<>());
            model.addAttribute("totalAuthorsWithRentals", 0L);
            model.addAttribute("totalRentals", 0L);
            model.addAttribute("totalAuthors", 0L);
            model.addAttribute("avgRentalsPerAuthor", 0.0);
            model.addAttribute("countryIdValue", countryId != null ? countryId : "");
            model.addAttribute("statusIdValue", statusId != null ? statusId : "");
            model.addAttribute("topValue", top);
            model.addAttribute("error", "Error al cargar el reporte: " + e.getMessage());
            
            return "admin/autores-pedidos";
        }
    }
}