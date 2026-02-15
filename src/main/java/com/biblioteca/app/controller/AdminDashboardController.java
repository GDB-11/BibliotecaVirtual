package com.biblioteca.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.app.dto.rental.BookRentalStatsDTO;
import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.service.AuthorService;
import com.biblioteca.app.service.BookCopyService;
import com.biblioteca.app.service.BookService;
import com.biblioteca.app.service.ConfigurationService;
import com.biblioteca.app.service.RentalService;

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
}