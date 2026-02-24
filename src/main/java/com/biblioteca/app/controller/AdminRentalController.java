package com.biblioteca.app.controller;

import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.dto.user.UserData;
import com.biblioteca.app.entity.Book;
import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.entity.RentalStatus;
import com.biblioteca.app.service.BookCopyService;
import com.biblioteca.app.service.ConfigurationService;
import com.biblioteca.app.service.RentalService;
import com.biblioteca.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para la gestión de alquileres en el panel de administración.
 * Maneja el CRUD completo de alquileres, incluyendo creación, devolución y cancelación.
 */
@Controller
@RequestMapping("/admin/alquileres")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRentalController {
    
    @Autowired
    private RentalService rentalService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private BookCopyService bookCopyService;
    
    @Autowired
    private ConfigurationService configurationService;
    
    /**
     * Muestra el listado de alquileres con paginación y filtros.
     */
    @GetMapping
    public String listRentals(
            @RequestParam(value = "p", defaultValue = "1") int page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "userId", required = false) String userId,
            @RequestParam(value = "rentalStatusId", required = false) String rentalStatusId,
            Model model) {
        
        try {
            int itemsPerPage = size != null ? size : configurationService.getIntValue("ItemsPerPage", 15);
            
            UUID userUuid = (userId != null && !userId.isEmpty()) ? UUID.fromString(userId) : null;
            UUID statusUuid = (rentalStatusId != null && !rentalStatusId.isEmpty()) 
                ? UUID.fromString(rentalStatusId) : null;
            
            PagedResult<Rental> rentalsResult = rentalService.getRegisteredRentals(
                page, itemsPerPage, search, userUuid, statusUuid);
            
            List<UserData> users = userService.getAllActiveUsers();
            List<Book> availableBooks = bookCopyService.findAvailableBooks();
            List<RentalStatus> rentalStatuses = rentalService.getAllRentalStatuses();
            
            long totalRentals = rentalService.count();
            long activeRentals = rentalService.countActiveRentals();
            long overdueRentals = rentalService.getOverdueRentalsCount();
            BigDecimal defaultDailyRate = BigDecimal.valueOf(rentalService.getDefaultDailyRate());
            
            model.addAttribute("rentalsResult", rentalsResult);
            model.addAttribute("users", users);
            model.addAttribute("availableBooks", availableBooks);
            model.addAttribute("rentalStatuses", rentalStatuses);
            model.addAttribute("totalRentals", totalRentals);
            model.addAttribute("activeRentals", activeRentals);
            model.addAttribute("overdueRentals", overdueRentals);
            model.addAttribute("defaultDailyRate", defaultDailyRate);
            
            model.addAttribute("searchValue", search != null ? search : "");
            model.addAttribute("userIdValue", userId != null ? userId : "");
            model.addAttribute("rentalStatusIdValue", rentalStatusId != null ? rentalStatusId : "");
            model.addAttribute("itemsPerPage", itemsPerPage);
            
            return "admin/alquileres";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar alquileres: " + e.getMessage());
            return "admin/alquileres";
        }
    }
    
    /**
     * Muestra los detalles de un alquiler (respuesta AJAX para modal).
     */
    @GetMapping("/detalle")
    public String getRentalDetail(
            @RequestParam("id") String rentalId,
            Model model) {

        try {
            UUID uuid = UUID.fromString(rentalId);
            Rental rental = rentalService.findById(uuid)
                    .orElseThrow(() -> new IllegalArgumentException("Alquiler no encontrado"));

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
     * Crea un nuevo alquiler.
     */
    @PostMapping("/crear")
    public String createRental(
            @RequestParam String userId,
            @RequestParam String bookId,
            @RequestParam int rentalDays,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (userId == null || userId.trim().isEmpty()) {
                throw new IllegalArgumentException("El usuario es requerido");
            }
            
            if (bookId == null || bookId.trim().isEmpty()) {
                throw new IllegalArgumentException("El libro es requerido");
            }
            
            if (rentalDays <= 0) {
                throw new IllegalArgumentException("Los días de alquiler deben ser mayor a 0");
            }
            
            UUID userUuid = UUID.fromString(userId);
            UUID bookUuid = UUID.fromString(bookId);
            
            rentalService.createRentalForAdmin(userUuid, bookUuid, rentalDays, notes);
            
            redirectAttributes.addFlashAttribute("success", "Alquiler creado exitosamente");
            return "redirect:/admin/alquileres";
            
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/alquileres";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear alquiler: " + e.getMessage());
            return "redirect:/admin/alquileres";
        }
    }
    
    /**
     * Marca un alquiler como devuelto.
     */
    @PostMapping("/{id}/devolver")
    public String markAsReturned(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            UUID rentalId = UUID.fromString(id);
            rentalService.markAsReturned(rentalId);
            redirectAttributes.addFlashAttribute("success", "Alquiler marcado como devuelto exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al marcar como devuelto: " + e.getMessage());
        }
        return "redirect:/admin/alquileres";
    }
    
    /**
     * Cancela un alquiler.
     */
    @PostMapping("/{id}/cancelar")
    public String cancelRental(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            UUID rentalId = UUID.fromString(id);
            rentalService.cancelRental(rentalId);
            redirectAttributes.addFlashAttribute("success", "Alquiler cancelado exitosamente");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cancelar alquiler: " + e.getMessage());
        }
        return "redirect:/admin/alquileres";
    }
}