package com.biblioteca.app.controller;

import com.biblioteca.app.dto.author.AuthorDTO;
import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Author;
import com.biblioteca.app.entity.Country;
import com.biblioteca.app.entity.Status;
import com.biblioteca.app.service.AuthorService;
import com.biblioteca.app.service.ConfigurationService;
import com.biblioteca.app.service.CountryService;
import com.biblioteca.app.service.StatusService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controlador para la gestión de autores en el panel de administración.
 * Maneja el CRUD completo de autores con paginación y filtros.
 */
@Controller
@RequestMapping("/admin/autores")
public class AdminAuthorController {
    
    private final AuthorService authorService;
    private final CountryService countryService;
    private final StatusService statusService;
    private final ConfigurationService configurationService;
    
    public AdminAuthorController(AuthorService authorService,
                                CountryService countryService,
                                StatusService statusService,
                                ConfigurationService configurationService) {
        this.authorService = authorService;
        this.countryService = countryService;
        this.statusService = statusService;
        this.configurationService = configurationService;
    }
    
    /**
     * Muestra el listado de autores con paginación y filtros.
     */
    @GetMapping
    public String listAuthors(
            @RequestParam(value = "p", defaultValue = "1") int page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "countryId", required = false) String countryId,
            @RequestParam(value = "statusId", required = false) String statusId,
            Model model) {
        
        try {
            int itemsPerPage = size != null ? size : configurationService.getIntValue("ItemsPerPage", 15);
            
            UUID countryUuid = (countryId != null && !countryId.isEmpty()) ? UUID.fromString(countryId) : null;
            UUID statusUuid = (statusId != null && !statusId.isEmpty()) ? UUID.fromString(statusId) : null;
            
            PagedResult<AuthorDTO> authorsResult = authorService.findAllPaginated(
                page, itemsPerPage, search, countryUuid, statusUuid);
            
            List<Country> countries = countryService.findAll();
            List<Status> statuses = statusService.findAll();
            
            long totalAuthors = authorService.count();
            // Contar autores con estado "Active"
            Status activeStatus = statusService.findByName("Active").orElse(null);
            long activeAuthorsCount = (activeStatus != null) ? 
                authorService.findByStatus(activeStatus.getStatusId()).size() : 0;
            long authorsWithBooksCount = authorService.countAuthorsWithBooks();
            
            model.addAttribute("authorsResult", authorsResult);
            model.addAttribute("countries", countries);
            model.addAttribute("statuses", statuses);
            model.addAttribute("totalAuthors", totalAuthors);
            model.addAttribute("activeAuthorsCount", activeAuthorsCount);
            model.addAttribute("authorsWithBooksCount", authorsWithBooksCount);
            
            model.addAttribute("searchValue", search != null ? search : "");
            model.addAttribute("countryIdValue", countryId != null ? countryId : "");
            model.addAttribute("statusIdValue", statusId != null ? statusId : "");
            model.addAttribute("itemsPerPage", itemsPerPage);
            
            return "admin/autores";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar autores: " + e.getMessage());
            return "admin/autores";
        }
    }
    
    /**
     * Muestra el formulario para crear un nuevo autor.
     */
    @GetMapping("/nuevo")
    public String newAuthorForm(Model model) {
        try {
            Author author = new Author();
            
            List<Country> countries = countryService.findAll();
            List<Status> statuses = statusService.findAll();
            
            model.addAttribute("author", author);
            model.addAttribute("countries", countries);
            model.addAttribute("statuses", statuses);
            model.addAttribute("isNew", true);
            model.addAttribute("isReadOnly", false);
            
            return "admin/autor-form";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/autores";
        }
    }
    
    /**
     * Muestra el formulario para editar un autor existente.
     */
    @GetMapping("/{id}/editar")
    public String editAuthorForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID authorId = UUID.fromString(id);
            Author author = authorService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            
            List<Country> countries = countryService.findAll();
            List<Status> statuses = statusService.findAll();
            
            int bookCount = authorService.countAuthorBooks(authorId);
            
            model.addAttribute("author", author);
            model.addAttribute("countries", countries);
            model.addAttribute("statuses", statuses);
            model.addAttribute("bookCount", bookCount);
            model.addAttribute("isNew", false);
            model.addAttribute("isReadOnly", false);
            
            return "admin/autor-form";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/autores";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el autor: " + e.getMessage());
            return "redirect:/admin/autores";
        }
    }
    
    /**
     * Muestra los detalles de un autor (solo lectura).
     */
    @GetMapping("/{id}")
    public String viewAuthor(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID authorId = UUID.fromString(id);
            Author author = authorService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            
            List<Country> countries = countryService.findAll();
            List<Status> statuses = statusService.findAll();
            
            int bookCount = authorService.countAuthorBooks(authorId);
            
            model.addAttribute("author", author);
            model.addAttribute("countries", countries);
            model.addAttribute("statuses", statuses);
            model.addAttribute("bookCount", bookCount);
            model.addAttribute("isNew", false);
            model.addAttribute("isReadOnly", true);
            
            return "admin/autor-form";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/autores";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el autor: " + e.getMessage());
            return "redirect:/admin/autores";
        }
    }
    
    /**
     * Crea un nuevo autor.
     */
    @PostMapping("/nuevo")
    public String createAuthor(
            @RequestParam String fullName,
            @RequestParam(required = false) String pseudonym,
            @RequestParam String countryId,
            @RequestParam(required = false) String biography,
            @RequestParam(required = false) Integer birthYear,
            @RequestParam(required = false) Integer deathYear,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String photoUrl,
            RedirectAttributes redirectAttributes) {
        
        try {
            Author author = new Author();
            author.setFullName(fullName);
            author.setPseudonym(pseudonym);
            author.setBiography(biography);
            author.setBirthYear(birthYear);
            author.setDeathYear(deathYear);
            author.setWebsite(website);
            author.setEmail(email);
            author.setPhotoUrl(photoUrl);
            
            Country country = countryService.findById(UUID.fromString(countryId))
                .orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
            author.setCountry(country);
            
            Status activeStatus = statusService.findByName("Active")
                .orElseThrow(() -> new IllegalArgumentException("Estado activo no encontrado"));
            author.setStatus(activeStatus);
            
            authorService.save(author);
            
            redirectAttributes.addFlashAttribute("success", "Autor creado exitosamente");
            return "redirect:/admin/autores";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el autor: " + e.getMessage());
            return "redirect:/admin/autores/nuevo";
        }
    }
    
    /**
     * Actualiza un autor existente.
     */
    @PostMapping("/{id}/editar")
    public String updateAuthor(
            @PathVariable String id,
            @RequestParam String fullName,
            @RequestParam(required = false) String pseudonym,
            @RequestParam String countryId,
            @RequestParam String statusId,
            @RequestParam(required = false) String biography,
            @RequestParam(required = false) Integer birthYear,
            @RequestParam(required = false) Integer deathYear,
            @RequestParam(required = false) String website,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String photoUrl,
            RedirectAttributes redirectAttributes) {
        
        try {
            UUID authorId = UUID.fromString(id);
            Author author = authorService.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            
            author.setFullName(fullName);
            author.setPseudonym(pseudonym);
            author.setBiography(biography);
            author.setBirthYear(birthYear);
            author.setDeathYear(deathYear);
            author.setWebsite(website);
            author.setEmail(email);
            author.setPhotoUrl(photoUrl);
            
            Country country = countryService.findById(UUID.fromString(countryId))
                .orElseThrow(() -> new IllegalArgumentException("País no encontrado"));
            author.setCountry(country);
            
            Status status = statusService.findById(UUID.fromString(statusId))
                .orElseThrow(() -> new IllegalArgumentException("Estado no encontrado"));
            author.setStatus(status);
            
            authorService.save(author);
            
            redirectAttributes.addFlashAttribute("success", "Autor actualizado exitosamente");
            return "redirect:/admin/autores";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el autor: " + e.getMessage());
            return "redirect:/admin/autores/" + id + "/editar";
        }
    }
    
    /**
     * Elimina un autor.
     */
    @PostMapping("/{id}/eliminar")
    public String deleteAuthor(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            UUID authorId = UUID.fromString(id);
            
            int bookCount = authorService.countAuthorBooks(authorId);
            if (bookCount > 0) {
                redirectAttributes.addFlashAttribute("error", 
                    "No se puede eliminar el autor porque tiene " + bookCount + " libro(s) registrado(s)");
                return "redirect:/admin/autores";
            }
            
            authorService.delete(authorId);
            redirectAttributes.addFlashAttribute("success", "Autor eliminado exitosamente");
            return "redirect:/admin/autores";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el autor: " + e.getMessage());
            return "redirect:/admin/autores";
        }
    }
}