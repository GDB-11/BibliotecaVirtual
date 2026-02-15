package com.biblioteca.app.controller.admin;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
import com.biblioteca.app.entity.Country;
import com.biblioteca.app.entity.Status;
import com.biblioteca.app.service.AuthorService;
import com.biblioteca.app.service.CountryService;
import com.biblioteca.app.service.StatusService;

/**
 * Controlador para mantenimiento de autores.
 */
@Controller
@RequestMapping("/admin/mantenimiento")
public class AdminMantenimientoAutoresController {
    
    private final AuthorService authorService;
    private final CountryService countryService;
    private final StatusService statusService;
    
    public AdminMantenimientoAutoresController(
            AuthorService authorService,
            CountryService countryService,
            StatusService statusService) {
        this.authorService = authorService;
        this.countryService = countryService;
        this.statusService = statusService;
    }
    
    /**
     * Listado paginado de autores con filtros.
     */
    @GetMapping("/autores")
    public String listarAutores(
            @RequestParam(defaultValue = "1") int p,
            @RequestParam(defaultValue = "15") int tamano,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) UUID countryId,
            @RequestParam(required = false) UUID statusId,
            Model model) {
        
        // DEBUG - Ver qué parámetros llegan
        System.out.println("=== FILTROS RECIBIDOS EN AUTORES ===");
        System.out.println("p: " + p);
        System.out.println("tamano: " + tamano);
        System.out.println("search: " + search);
        System.out.println("countryId: " + countryId);
        System.out.println("statusId: " + statusId);
        
        // Obtener todos los autores
        List<Author> todosAutores = authorService.findAll();
        System.out.println("Total autores sin filtrar: " + todosAutores.size());
        
        // Aplicar filtros
        List<Author> autoresFiltrados = todosAutores.stream()
            .filter(author -> filtrarPorBusqueda(author, search))
            .filter(author -> filtrarPorPais(author, countryId))
            .filter(author -> filtrarPorEstado(author, statusId))
            .collect(Collectors.toList());
        
        System.out.println("Total autores después de filtrar: " + autoresFiltrados.size());
        
        // Calcular estadísticas
        long activos = autoresFiltrados.stream()
            .filter(a -> a.getStatus() != null && "Active".equals(a.getStatus().getStatusName()))
            .count();
        
        // Paginación simple (en memoria)
        int totalItems = autoresFiltrados.size();
        int start = (p - 1) * tamano;
        int end = Math.min(start + tamano, totalItems);
        
        List<Author> autoresPaginados;
        if (totalItems == 0 || start >= totalItems) {
            autoresPaginados = new ArrayList<>();
        } else {
            autoresPaginados = autoresFiltrados.subList(start, end);
        }
        
        // Obtener conteo de libros para cada autor en la página actual
        Map<UUID, Integer> librosPorAutor = new HashMap<>();
        for (Author autor : autoresPaginados) {
            int count = authorService.countBooksByAuthorId(autor.getAuthorId());
            librosPorAutor.put(autor.getAuthorId(), count);
        }
        
        int totalPaginas = totalItems == 0 ? 1 : (int) Math.ceil((double) totalItems / tamano);
        
        model.addAttribute("autores", autoresPaginados);
        model.addAttribute("librosPorAutor", librosPorAutor);
        model.addAttribute("paises", countryService.findAll());
        model.addAttribute("estados", statusService.findAll());
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("autoresActivosCount", activos);
        model.addAttribute("paginaActual", p);
        model.addAttribute("totalPaginas", totalPaginas);
        
        // Mantener valores de filtros
        model.addAttribute("searchValue", search != null ? search : "");
        model.addAttribute("countryIdValue", countryId != null ? countryId.toString() : "");
        model.addAttribute("statusIdValue", statusId != null ? statusId.toString() : "");
        
        return "admin/mantenimiento/autores/lista";
    }
    
    private boolean filtrarPorBusqueda(Author author, String search) {
        if (search == null || search.trim().isEmpty()) {
            return true;
        }
        String searchLower = search.toLowerCase().trim();
        return author.getFullName() != null && 
               author.getFullName().toLowerCase().contains(searchLower);
    }
    
    private boolean filtrarPorPais(Author author, UUID countryId) {
        if (countryId == null) {
            return true;
        }
        return author.getCountry() != null && countryId.equals(author.getCountry().getCountryId());
    }
    
    private boolean filtrarPorEstado(Author author, UUID statusId) {
        if (statusId == null) {
            return true;
        }
        return author.getStatus() != null && statusId.equals(author.getStatus().getStatusId());
    }
    
    /**
     * Lista de autores activos.
     */
    @GetMapping("/autores/activos")
    public String autoresActivos(Model model) {
        model.addAttribute("autoresActivos", authorService.getActiveAuthors());
        return "admin/mantenimiento/autores/activos";
    }
    
    /**
     * Formulario para nuevo autor.
     */
    @GetMapping("/autores/nuevo")
    public String nuevoAutorForm(Model model) {
        model.addAttribute("autor", new Author());
        model.addAttribute("paises", countryService.findAll());
        model.addAttribute("estados", statusService.findAll());
        model.addAttribute("esNuevo", true);
        return "admin/mantenimiento/autores/form";
    }
    
    /**
     * Guardar nuevo autor - CORREGIDO
     */
    @PostMapping("/autores/guardar")
    public String guardarAutor(
            @RequestParam String fullName,
            @RequestParam(required = false) Integer birthYear,
            @RequestParam(required = false) Integer deathYear,
            @RequestParam UUID countryId,
            @RequestParam UUID statusId,
            @RequestParam(required = false) String biography,
            RedirectAttributes redirectAttributes) {
        
        try {
            Author autor = new Author();
            autor.setAuthorId(UUID.randomUUID());
            autor.setFullName(fullName);
            autor.setBirthYear(birthYear);
            autor.setDeathYear(deathYear);
            autor.setBiography(biography);
            
            // Buscar y asignar país
            Country country = countryService.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
            autor.setCountry(country);
            
            // Buscar y asignar estado
            Status status = statusService.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
            autor.setStatus(status);
            
            authorService.save(autor);
            redirectAttributes.addFlashAttribute("success", "Autor guardado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al guardar: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento/autores";
    }
    
    /**
     * Ver detalles de un autor.
     */
    @GetMapping("/autores/{id}")
    public String verAutor(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Author autor = authorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            
            // Obtener conteo de libros para mostrar en detalles
            int librosCount = authorService.countBooksByAuthorId(id);
            
            model.addAttribute("autor", autor);
            model.addAttribute("librosCount", librosCount);
            model.addAttribute("paises", countryService.findAll());
            model.addAttribute("estados", statusService.findAll());
            model.addAttribute("esSoloLectura", true);
            
            return "admin/mantenimiento/autores/ver";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/mantenimiento/autores";
        }
    }
    
    /**
     * Formulario para editar autor.
     */
    @GetMapping("/autores/{id}/editar")
    public String editarAutorForm(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Author autor = authorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            
            model.addAttribute("autor", autor);
            model.addAttribute("paises", countryService.findAll());
            model.addAttribute("estados", statusService.findAll());
            model.addAttribute("esNuevo", false);
            
            return "admin/mantenimiento/autores/form";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/mantenimiento/autores";
        }
    }
    
    /**
     * Actualizar autor existente
     */
    @PostMapping("/autores/{id}/editar")
    public String actualizarAutor(
            @PathVariable UUID id,
            @RequestParam String fullName,
            @RequestParam(required = false) Integer birthYear,
            @RequestParam(required = false) Integer deathYear,
            @RequestParam UUID countryId,
            @RequestParam UUID statusId,
            @RequestParam(required = false) String biography,
            RedirectAttributes redirectAttributes) {
        
        try {
            Author autor = authorService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            
            autor.setFullName(fullName);
            autor.setBirthYear(birthYear);
            autor.setDeathYear(deathYear);
            autor.setBiography(biography);
            
            Country country = countryService.findById(countryId)
                .orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
            autor.setCountry(country);
            
            Status status = statusService.findById(statusId)
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
            autor.setStatus(status);
            
            authorService.save(autor);
            redirectAttributes.addFlashAttribute("success", "Autor actualizado correctamente");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento/autores";
    }
    
    /**
     * Eliminar autor.
     */
    @PostMapping("/autores/{id}/eliminar")
    public String eliminarAutor(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        try {
            // Verificar si tiene libros antes de eliminar
            int librosAsociados = authorService.countBooksByAuthorId(id);
            if (librosAsociados > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar porque tiene " + librosAsociados + " libros asociados");
                return "redirect:/admin/mantenimiento/autores";
            }
            
            authorService.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Autor eliminado correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/mantenimiento/autores";
    }
}