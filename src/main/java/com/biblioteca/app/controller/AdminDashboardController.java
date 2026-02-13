package com.biblioteca.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.biblioteca.app.dto.rental.BookRentalStatsDTO;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.service.AuthorService;
import com.biblioteca.app.service.BookCopyService;
import com.biblioteca.app.service.BookService;
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
}