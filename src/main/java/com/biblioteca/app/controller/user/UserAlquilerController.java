package com.biblioteca.app.controller.user;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.biblioteca.app.entity.Book;
import com.biblioteca.app.entity.BookCopy;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.entity.User;
import com.biblioteca.app.service.BookCopyService;
import com.biblioteca.app.service.BookService;
import com.biblioteca.app.service.RentalService;
import com.biblioteca.app.service.UserService;

@Controller
@RequestMapping("/user/alquiler")
public class UserAlquilerController {
    
    private final RentalService rentalService;
    private final UserService userService;
    private final BookCopyService bookCopyService;
    private final BookService bookService;  // ✅ AGREGADO
    
    public UserAlquilerController(
            RentalService rentalService, 
            UserService userService, 
            BookCopyService bookCopyService,
            BookService bookService) {  // ✅ AGREGADO EN CONSTRUCTOR
        this.rentalService = rentalService;
        this.userService = userService;
        this.bookCopyService = bookCopyService;
        this.bookService = bookService;
    }
    
    @GetMapping("/nuevo/{bookId}")
    public String nuevoAlquilerForm(@PathVariable UUID bookId, Model model) {
        // Buscar el libro
        Book libro = bookService.findById(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        // Buscar ejemplares disponibles - ESTO DEBE FUNCIONAR
        List<BookCopy> ejemplaresDisponibles = bookCopyService.findAvailableByBookId(bookId);
        
        // DEBUG - Ver cuántos ejemplares encuentra
        System.out.println("Libro: " + libro.getTitle());
        System.out.println("Ejemplares disponibles encontrados: " + ejemplaresDisponibles.size());
        
        model.addAttribute("libro", libro);
        model.addAttribute("ejemplaresDisponibles", ejemplaresDisponibles);
        
        return "user/alquiler/nuevo";
    }
    
    
    @GetMapping("/test/{bookCopyId}")
    @ResponseBody
    public String testAlquiler(@PathVariable UUID bookCopyId, Principal principal) {
        try {
            User user = userService.getUserByEmail(principal.getName()).orElseThrow();
            Rental rental = rentalService.createRentalEmergency(user.getUserId(), bookCopyId);
            return "Alquiler creado: " + rental.getRentalId();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    
    @PostMapping("/crear")
    public String crearAlquiler(
            Principal principal,
            @RequestParam UUID bookCopyId,
            RedirectAttributes redirectAttributes) {
        
        System.out.println("=== INICIANDO PROCESO DE ALQUILER ===");
        System.out.println("BookCopyId recibido: " + bookCopyId);
        
        try {
            // 1. Obtener usuario
            User user = userService.getUserByEmail(principal.getName())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));
            System.out.println("Usuario encontrado: " + user.getEmail());
            
            // 2. Obtener ejemplar
            BookCopy bookCopy = bookCopyService.findById(bookCopyId)
                .orElseThrow(() -> new IllegalArgumentException("Ejemplar no encontrado"));
            System.out.println("Ejemplar encontrado: " + bookCopy.getBookCopyId());
            System.out.println("Libro: " + bookCopy.getBook().getTitle());
            
            // 3. Verificar que el ejemplar esté disponible
            if (!"Disponible".equals(bookCopy.getBookCopyStatus().getBookCopyStatusName())) {
                throw new IllegalStateException("El ejemplar no está disponible");
            }
            
            // 4. Crear alquiler
            Rental nuevoAlquiler = rentalService.createRental(user, bookCopy);
            System.out.println("Alquiler creado con ID: " + nuevoAlquiler.getRentalId());
            
            redirectAttributes.addFlashAttribute("success", "Alquiler realizado correctamente");
            return "redirect:/user/historial";
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al alquilar: " + e.getMessage());
            return "redirect:/user/catalogo";
        }
    }
}