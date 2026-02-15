package com.biblioteca.app.controller.user;

import java.security.Principal;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.biblioteca.app.entity.Book;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.entity.User;
import com.biblioteca.app.service.BookService;
import com.biblioteca.app.service.RentalService;
import com.biblioteca.app.service.UserService;

@Controller
@RequestMapping("/user")
public class UserDashboardController {
    
    private final BookService bookService;
    private final RentalService rentalService;
    private final UserService userService;
    
    public UserDashboardController(BookService bookService, RentalService rentalService, UserService userService) {
        this.bookService = bookService;
        this.rentalService = rentalService;
        this.userService = userService;
    }
    
    @GetMapping("/dashboard")
    public String dashboard(Principal principal, Model model) {
        // Obtener usuario actual
        User currentUser = userService.getUserByEmail(principal.getName()).orElseThrow();
        
        // Libros más populares (para mostrar en dashboard)
        List<Book> librosPopulares = bookService.getMostRequestedBooks(8);
        model.addAttribute("librosPopulares", librosPopulares);
        
        // Últimos libros agregados
        List<Book> ultimosLibros = bookService.findLastAdded(4);
        model.addAttribute("ultimosLibros", ultimosLibros);
        
        // Estadísticas del usuario
        long totalAlquileres = rentalService.countByUser(currentUser.getUserId());
        long alquileresActivos = rentalService.countActiveByUser(currentUser.getUserId());
        long alquileresVencidos = rentalService.countOverdueByUser(currentUser.getUserId());
        
        model.addAttribute("totalAlquileres", totalAlquileres);
        model.addAttribute("alquileresActivos", alquileresActivos);
        model.addAttribute("alquileresVencidos", alquileresVencidos);
        
        // Últimos 5 alquileres del usuario
        List<Rental> ultimosAlquileres = rentalService.findLastByUser(currentUser.getUserId(), 5);
        model.addAttribute("ultimosAlquileres", ultimosAlquileres);
        
        return "user/dashboard";
    }
    
    @GetMapping("")
    public String home() {
        return "redirect:/user/dashboard";
    }
}