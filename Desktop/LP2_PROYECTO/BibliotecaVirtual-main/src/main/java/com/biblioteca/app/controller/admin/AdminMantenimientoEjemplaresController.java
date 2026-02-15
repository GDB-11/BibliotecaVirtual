package com.biblioteca.app.controller.admin;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.biblioteca.app.entity.Book;
import com.biblioteca.app.entity.BookCopy;
import com.biblioteca.app.entity.BookCopyStatus;
import com.biblioteca.app.service.BookCopyService;
import com.biblioteca.app.service.BookService;
import com.biblioteca.app.service.BookCopyStatusService;

@Controller
@RequestMapping("/admin/mantenimiento")
public class AdminMantenimientoEjemplaresController {
    
    private final BookCopyService bookCopyService;
    private final BookService bookService;
    private final BookCopyStatusService bookCopyStatusService;
    
    public AdminMantenimientoEjemplaresController(
            BookCopyService bookCopyService,
            BookService bookService,
            BookCopyStatusService bookCopyStatusService) {
        this.bookCopyService = bookCopyService;
        this.bookService = bookService;
        this.bookCopyStatusService = bookCopyStatusService;
    }
    
    /**
     * Listado de ejemplares con filtros.
     */
    @GetMapping("/ejemplares")
    public String listarEjemplares(
            @RequestParam(defaultValue = "1") int p,  
            @RequestParam(defaultValue = "15") int tamano,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID bookId,
            @RequestParam(required = false) String statusId,
            Model model) {
        
        // DEBUG - Ver qué parámetros llegan
        System.out.println("=== FILTROS RECIBIDOS EN EJEMPLARES ===");
        System.out.println("p: " + p);
        System.out.println("tamano: " + tamano);
        System.out.println("search: " + search);
        System.out.println("bookId: " + bookId);
        System.out.println("statusId: " + statusId);
        
        // Obtener todos los ejemplares
        List<BookCopy> todosEjemplares = bookCopyService.findAll();
        System.out.println("Total ejemplares sin filtrar: " + todosEjemplares.size());
        
        // Aplicar filtros
        List<BookCopy> ejemplaresFiltrados = todosEjemplares.stream()
            .filter(copy -> filtrarPorBusqueda(copy, search))
            .filter(copy -> filtrarPorLibro(copy, bookId))
            .filter(copy -> filtrarPorEstado(copy, statusId))
            .collect(Collectors.toList());
        
        System.out.println("Total ejemplares después de filtrar: " + ejemplaresFiltrados.size());
        
        // Calcular estadísticas
        long disponibles = ejemplaresFiltrados.stream()
            .filter(c -> c.getBookCopyStatus() != null && 
                         "Disponible".equals(c.getBookCopyStatus().getBookCopyStatusName()))
            .count();
        
        long alquilados = ejemplaresFiltrados.stream()
            .filter(c -> c.getBookCopyStatus() != null && 
                         "Alquilado".equals(c.getBookCopyStatus().getBookCopyStatusName()))
            .count();
        
        // Paginación simple (en memoria)
        int totalItems = ejemplaresFiltrados.size();
        int start = (p - 1) * tamano;
        int end = Math.min(start + tamano, totalItems);
        
        List<BookCopy> ejemplaresPaginados;
        if (totalItems == 0 || start >= totalItems) {
            ejemplaresPaginados = new ArrayList<>();
        } else {
            ejemplaresPaginados = ejemplaresFiltrados.subList(start, end);
        }
        
        int totalPaginas = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / tamano);
        
        model.addAttribute("ejemplares", ejemplaresPaginados);
        model.addAttribute("libros", bookService.findAll());
        model.addAttribute("estados", bookCopyStatusService.findAll());
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("disponibles", disponibles);
        model.addAttribute("alquilados", alquilados);
        model.addAttribute("paginaActual", p);
        model.addAttribute("totalPaginas", totalPaginas);
        
        // Mantener valores de filtros
        model.addAttribute("searchValue", search != null ? search : "");
        model.addAttribute("bookIdValue", bookId != null ? bookId.toString() : "");
        model.addAttribute("statusIdValue", statusId != null ? statusId : "");
        
        return "admin/mantenimiento/ejemplares/lista";
    }
    
    private boolean filtrarPorBusqueda(BookCopy copy, String search) {
        if (search == null || search.trim().isEmpty()) {
            return true;
        }
        String searchLower = search.toLowerCase().trim();
        
        boolean coincideTitulo = copy.getBook() != null && 
                                 copy.getBook().getTitle() != null && 
                                 copy.getBook().getTitle().toLowerCase().contains(searchLower);
        
        boolean coincideId = copy.getBookCopyId() != null && 
                             copy.getBookCopyId().toString().toLowerCase().contains(searchLower);
        
        boolean coincideNotas = copy.getNotes() != null && 
                                copy.getNotes().toLowerCase().contains(searchLower);
        
        return coincideTitulo || coincideId || coincideNotas;
    }
    
    private boolean filtrarPorLibro(BookCopy copy, UUID bookId) {
        if (bookId == null) {
            return true;
        }
        return copy.getBook() != null && bookId.equals(copy.getBook().getBookId());
    }
    
    private boolean filtrarPorEstado(BookCopy copy, String statusId) {
        if (statusId == null || statusId.trim().isEmpty()) {
            return true;
        }
        if (copy.getBookCopyStatus() == null) {
            return false;
        }
        String statusName = copy.getBookCopyStatus().getBookCopyStatusName();
        switch (statusId) {
            case "disponible":
                return "Disponible".equals(statusName);
            case "alquilado":
                return "Alquilado".equals(statusName);
            case "mantenimiento":
                return "Mantenimiento".equals(statusName);
            case "perdido":
                return "Perdido".equals(statusName);
            default:
                return true;
        }
    }
    
    /**
     * Formulario para nuevo ejemplar.
     */
    @GetMapping("/ejemplares/nuevo")
    public String nuevoEjemplarForm(Model model) {
        model.addAttribute("ejemplar", new BookCopy());
        model.addAttribute("libros", bookService.findAll());
        model.addAttribute("estados", bookCopyStatusService.findAll());
        model.addAttribute("esNuevo", true);
        return "admin/mantenimiento/ejemplares/form";
    }
    
    /**
     * Guardar nuevo ejemplar.
     */
    @PostMapping("/ejemplares/guardar")
    public String guardarEjemplar(
            @RequestParam UUID bookId,
            @RequestParam UUID bookCopyStatusId,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            BookCopy ejemplar = new BookCopy();
            ejemplar.setBookCopyId(UUID.randomUUID()); // Nuevo ID
            
            Book libro = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            ejemplar.setBook(libro);
            
            BookCopyStatus status = bookCopyStatusService.findById(bookCopyStatusId)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
            ejemplar.setBookCopyStatus(status);
            
            ejemplar.setNotes(notes);
            
            bookCopyService.save(ejemplar);
            redirectAttributes.addFlashAttribute("success", "Ejemplar guardado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento/ejemplares";
    }

    /**
     * Actualizar ejemplar existente
     */
    @PostMapping("/ejemplares/{id}/editar")
    public String actualizarEjemplar(
            @PathVariable UUID id,
            @RequestParam UUID bookId,
            @RequestParam UUID bookCopyStatusId,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            BookCopy ejemplar = bookCopyService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejemplar no encontrado"));
            
            // Guardar el ID original
            UUID originalId = ejemplar.getBookCopyId();
            
            Book libro = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            ejemplar.setBook(libro);
            
            BookCopyStatus status = bookCopyStatusService.findById(bookCopyStatusId)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
            ejemplar.setBookCopyStatus(status);
            
            ejemplar.setNotes(notes);
            
            // Asegurar que el ID no cambie
            ejemplar.setBookCopyId(originalId);
            
            // Usar update que usa saveAndFlush
            bookCopyService.update(ejemplar);
            
            redirectAttributes.addFlashAttribute("success", "Ejemplar actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento/ejemplares";
    }
    
    /**
     * Ver detalles de un ejemplar.
     */
    @GetMapping("/ejemplares/{id}")
    public String verEjemplar(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BookCopy ejemplar = bookCopyService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejemplar no encontrado"));
            
            model.addAttribute("ejemplar", ejemplar);
            model.addAttribute("libros", bookService.findAll());
            model.addAttribute("estados", bookCopyStatusService.findAll());
            model.addAttribute("esSoloLectura", true);
            
            return "admin/mantenimiento/ejemplares/ver";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/mantenimiento/ejemplares";
        }
    }
    
    /**
     * Formulario para editar ejemplar.
     */
    @GetMapping("/ejemplares/{id}/editar")
    public String editarEjemplarForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BookCopy ejemplar = bookCopyService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ejemplar no encontrado"));
            
            model.addAttribute("ejemplar", ejemplar);
            model.addAttribute("libros", bookService.findAll());
            model.addAttribute("estados", bookCopyStatusService.findAll());
            model.addAttribute("esNuevo", false);
            
            return "admin/mantenimiento/ejemplares/form";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/mantenimiento/ejemplares";
        }
    }
    
    /**
     * Eliminar ejemplar - VERSIÓN MEJORADA
     */
    @PostMapping("/ejemplares/{id}/eliminar")
    public String eliminarEjemplar(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            // Verificar si tiene alquileres antes de eliminar
            if (bookCopyService.hasRentals(id)) {
                long rentalCount = bookCopyService.countRentals(id);
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar el ejemplar porque tiene " + rentalCount + " alquileres registrados");
                return "redirect:/admin/mantenimiento/ejemplares";
            }
            
            bookCopyService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Ejemplar eliminado correctamente");
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento/ejemplares";
    }
}