package com.biblioteca.app.controller.user;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.app.entity.Book;
import com.biblioteca.app.service.BookCopyService;
import com.biblioteca.app.service.BookService;
import com.biblioteca.app.service.CategoryService;

@Controller
@RequestMapping("/user/catalogo")
public class UserCatalogoController {
    
    private final BookService bookService;
    private final BookCopyService bookCopyService;
    private final CategoryService categoryService;
    
    public UserCatalogoController(BookService bookService, BookCopyService bookCopyService, CategoryService categoryService) {
        this.bookService = bookService;
        this.bookCopyService = bookCopyService;
        this.categoryService = categoryService;
    }
    
    @GetMapping
    public String listarLibros(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID categoryId,
            Model model) {
        
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Book> librosPage = bookService.findAvailableBooks(search, categoryId, pageable);
        
        model.addAttribute("libros", librosPage.getContent());
        model.addAttribute("categorias", categoryService.findAll());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", librosPage.getTotalPages());
        model.addAttribute("totalItems", librosPage.getTotalElements());
        model.addAttribute("search", search);
        model.addAttribute("categoryId", categoryId);
        
        return "user/catalogo/lista";
    }
    
    @GetMapping("/{id}")
    public String verLibro(@PathVariable UUID id, Model model) {
        Book libro = bookService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        
        model.addAttribute("libro", libro);
        model.addAttribute("ejemplaresDisponibles", bookCopyService.countAvailableCopiesByBookId(id));
        
        return "user/catalogo/detalle";
    }
}