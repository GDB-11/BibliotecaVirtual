package com.biblioteca.app.controller.admin;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.biblioteca.app.entity.Author;
import com.biblioteca.app.entity.Book;
import com.biblioteca.app.entity.BookStatus;  // ← IMPORTANTE: BookStatus, no Status
import com.biblioteca.app.entity.Category;
import com.biblioteca.app.service.BookService;
import com.biblioteca.app.service.AuthorService;
import com.biblioteca.app.service.CategoryService;
import com.biblioteca.app.service.BookStatusService;  // ← Cambiar a BookStatusService

/**
 * Controlador para mantenimiento de libros.
 */
@Controller
@RequestMapping("/admin/mantenimiento")
public class AdminMantenimientoLibrosController {
    
    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final BookStatusService bookStatusService;  // ← Cambiado
    
    public AdminMantenimientoLibrosController(
            BookService bookService,
            AuthorService authorService,
            CategoryService categoryService,
            BookStatusService bookStatusService) {  // ← Cambiado
        this.bookService = bookService;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.bookStatusService = bookStatusService;  // ← Cambiado
    }
    
    /**
     * Listado paginado de libros.
     */
    @GetMapping("/libros")
    public String listarLibros(
            @RequestParam(defaultValue = "1") int pagina,
            @RequestParam(defaultValue = "15") int tamano,
            Model model) {
        
        model.addAttribute("libros", bookService.findAll());
        model.addAttribute("paginaActual", 1);
        model.addAttribute("totalPaginas", 1);
        model.addAttribute("totalItems", bookService.count());
        model.addAttribute("itemsPerPage", tamano);
        
        return "admin/mantenimiento/libros/lista";
    }
    
    /**
     * Lista de libros activos.
     */
    @GetMapping("/libros/activos")
    public String librosActivos(Model model) {
        model.addAttribute("librosActivos", bookService.getActiveBooks());
        return "admin/mantenimiento/libros/activos";
    }
    
    /**
     * Formulario para nuevo libro.
     */
    @GetMapping("/libros/nuevo")
    public String nuevoLibroForm(Model model) {
        model.addAttribute("libro", new Book());
        model.addAttribute("autores", authorService.findAll());
        model.addAttribute("categorias", categoryService.findAll());
        model.addAttribute("estados", bookStatusService.findAll());  // ← Cambiado
        model.addAttribute("esNuevo", true);
        return "admin/mantenimiento/libros/form";
    }
    
	    /**
	     * Guardar nuevo libro - VERSIÓN CORREGIDA
	     */
	    @PostMapping("/libros/guardar")
	    public String guardarLibro(
	            @RequestParam String title,
	            @RequestParam(required = false) String isbn,
	            @RequestParam(required = false) Integer publicationYear,
	            @RequestParam UUID authorId,
	            @RequestParam(required = false) UUID categoryId,
	            @RequestParam UUID bookStatusId,
	            @RequestParam(required = false) String synopsis,
	            RedirectAttributes redirectAttributes) {
	        
	        try {
	            Book libro = new Book();

	            
	            libro.setTitle(title);
	            libro.setIsbn(isbn);
	            libro.setPublicationYear(publicationYear);
	            libro.setDescription(synopsis);
	            
	            // Buscar y asignar autor
	            Author autor = authorService.findById(authorId)
	                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
	            libro.setAuthor(autor);
	            
	            // Buscar y asignar categoría 
	            if (categoryId != null) {
	                Category categoria = categoryService.findById(categoryId)
	                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
	                libro.setCategory(categoria);
	            }
	            
	            // Buscar y asignar estado
	            BookStatus status = bookStatusService.findById(bookStatusId)
	                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
	            libro.setBookStatus(status);
	            

	            
	            bookService.save(libro);
	            redirectAttributes.addFlashAttribute("success", "Libro guardado correctamente");
	        } catch (Exception e) {
	            e.printStackTrace();
	            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
	        }
	        return "redirect:/admin/mantenimiento/libros";
	    }
    
    /**
     * Ver detalles de un libro.
     */
    @GetMapping("/libros/{id}")
    public String verLibro(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Book libro = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            
            model.addAttribute("libro", libro);
            model.addAttribute("autores", authorService.findAll());
            model.addAttribute("categorias", categoryService.findAll());
            model.addAttribute("estados", bookStatusService.findAll());  // ← Cambiado
            model.addAttribute("esSoloLectura", true);
            
            return "admin/mantenimiento/libros/ver";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/mantenimiento/libros";
        }
    }
    
    /**
     * Formulario para editar libro.
     */
    @GetMapping("/libros/{id}/editar")
    public String editarLibroForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Book libro = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            
            model.addAttribute("libro", libro);
            model.addAttribute("autores", authorService.findAll());
            model.addAttribute("categorias", categoryService.findAll());
            model.addAttribute("estados", bookStatusService.findAll());  // ← Cambiado
            model.addAttribute("esNuevo", false);
            
            return "admin/mantenimiento/libros/form";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/mantenimiento/libros";
        }
    }
    
    /**
     * Actualizar libro existente.
     */
    @PostMapping("/libros/{id}/editar")
    public String actualizarLibro(
            @PathVariable UUID id,
            @RequestParam String title,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) Integer publicationYear,
            @RequestParam UUID authorId,
            @RequestParam(required = false) UUID categoryId,
            @RequestParam UUID bookStatusId,
            @RequestParam(required = false) String synopsis,
            RedirectAttributes redirectAttributes) {
        
        try {
            Book libro = bookService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            
            libro.setTitle(title);
            libro.setIsbn(isbn);
            libro.setPublicationYear(publicationYear);
            libro.setDescription(synopsis);
            
            // Buscar y asignar autor
            Author autor = authorService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            libro.setAuthor(autor);
            
            // Buscar y asignar categoría (si se proporcionó)
            if (categoryId != null) {
                Category categoria = categoryService.findById(categoryId)
                    .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
                libro.setCategory(categoria);
            }
            
            // Buscar y asignar estado - AHORA USA BookStatusService
            BookStatus status = bookStatusService.findById(bookStatusId)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
            libro.setBookStatus(status);
            
            libro.setUpdatedAt(LocalDateTime.now());
            
            bookService.save(libro);
            redirectAttributes.addFlashAttribute("success", "Libro actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento/libros";
    }
    
    /**
     * Eliminar libro.
     */
    @PostMapping("/libros/{id}/eliminar")
    public String eliminarLibro(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            bookService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Libro eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento/libros";
    }
}