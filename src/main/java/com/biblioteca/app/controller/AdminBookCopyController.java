package com.biblioteca.app.controller;

import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Book;
import com.biblioteca.app.entity.BookCopy;
import com.biblioteca.app.entity.BookCopyStatus;
import com.biblioteca.app.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Controlador para la gestión de ejemplares de libros.
 * Incluye funcionalidad especial: inserción en batch, cambio de estado masivo.
 */
@Controller
@RequestMapping("/admin/ejemplares")
public class AdminBookCopyController {

    @Autowired
    private BookCopyService bookCopyService;

    @Autowired
    private BookService bookService;

    @Autowired
    private BookCopyStatusService bookCopyStatusService;

    @Autowired
    private ConfigurationService configurationService;

    /**
     * Muestra el listado de ejemplares con filtros y paginación.
     */
    @GetMapping
    public String listBookCopies(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String bookId,
            @RequestParam(required = false) String statusId,
            Model model) {

        int itemsPerPage = size != null ? size : configurationService.getIntValue("ItemsPerPage", 15);

        PagedResult<BookCopy> copiesResult = bookCopyService.getRegisteredBookCopies(
                page, itemsPerPage, search, bookId, statusId);

        List<Book> books = bookService.findAll();
        List<BookCopyStatus> bookCopyStatuses = bookCopyStatusService.findAll();

        // Obtener contadores de estados
        String availableStatusId = bookCopyService.getAvailableStatusId();
        String rentedStatusId = bookCopyService.getRentedStatusId();
        String maintenanceStatusId = bookCopyService.getMaintenanceStatusId();

        int availableCopiesCount = bookCopyService.findByStatus(availableStatusId).size();
        int rentedCopiesCount = bookCopyService.findByStatus(rentedStatusId).size();
        int maintenanceCopiesCount = bookCopyService.findByStatus(maintenanceStatusId).size();

        model.addAttribute("copiesResult", copiesResult);
        model.addAttribute("books", books);
        model.addAttribute("bookCopyStatuses", bookCopyStatuses);
        model.addAttribute("availableCopiesCount", availableCopiesCount);
        model.addAttribute("rentedCopiesCount", rentedCopiesCount);
        model.addAttribute("maintenanceCopiesCount", maintenanceCopiesCount);

        model.addAttribute("searchValue", search != null ? search : "");
        model.addAttribute("bookIdValue", bookId != null ? bookId : "");
        model.addAttribute("statusIdValue", statusId != null ? statusId : "");

        return "admin/ejemplares";
    }

    /**
     * Muestra los detalles de un ejemplar (solo lectura).
     */
    @GetMapping("/{id}")
    public String viewBookCopy(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            BookCopy bookCopy = bookCopyService.findById(id);

            if (bookCopy == null) {
                redirectAttributes.addFlashAttribute("error", "Ejemplar no encontrado");
                return "redirect:/admin/ejemplares";
            }

            List<BookCopyStatus> bookCopyStatuses = bookCopyStatusService.findAll();

            model.addAttribute("bookCopy", bookCopy);
            model.addAttribute("bookCopyStatuses", bookCopyStatuses);
            model.addAttribute("isEdit", false);
            model.addAttribute("isReadOnly", true);
            model.addAttribute("isNew", false);

            return "admin/ejemplar-form";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el ejemplar: " + e.getMessage());
            return "redirect:/admin/ejemplares";
        }
    }

    /**
     * Muestra el formulario para agregar un nuevo ejemplar (batch).
     */
    @GetMapping("/nuevo")
    public String showAddForm(Model model) {
        List<Book> books = bookService.findAll();
        model.addAttribute("books", books);
        model.addAttribute("isEdit", false);
        model.addAttribute("isReadOnly", false);
        model.addAttribute("isNew", true);
        return "admin/ejemplar-form";
    }

    /**
     * Crea múltiples ejemplares en batch.
     */
    @PostMapping("/crear-batch")
    public String createBatch(
            @RequestParam String bookId,
            @RequestParam int quantity,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {

        try {
            if (quantity < 1 || quantity > 100) {
                redirectAttributes.addFlashAttribute("error", "La cantidad debe estar entre 1 y 100");
                return "redirect:/admin/ejemplares/nuevo";
            }

            bookCopyService.saveBatch(bookId, quantity, notes);

            redirectAttributes.addFlashAttribute("success", 
                quantity + " ejemplar(es) creado(s) exitosamente");
            return "redirect:/admin/ejemplares";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear ejemplares: " + e.getMessage());
            return "redirect:/admin/ejemplares/nuevo";
        }
    }

    /**
     * Muestra el formulario de edición de un ejemplar.
     */
    @GetMapping("/editar/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        BookCopy bookCopy = bookCopyService.findById(id);

        if (bookCopy == null) {
            redirectAttributes.addFlashAttribute("error", "Ejemplar no encontrado");
            return "redirect:/admin/ejemplares";
        }

        // Verificar si está alquilado
        boolean isRented = bookCopy.getBookCopyStatus() != null &&
                "Alquilado".equals(bookCopy.getBookCopyStatus().getBookCopyStatusName());

        if (isRented) {
            redirectAttributes.addFlashAttribute("error",
                    "No se puede editar un ejemplar que está actualmente alquilado");
            return "redirect:/admin/ejemplares";
        }

        List<BookCopyStatus> bookCopyStatuses = bookCopyStatusService.findAll();

        model.addAttribute("bookCopy", bookCopy);
        model.addAttribute("bookCopyStatuses", bookCopyStatuses);
        model.addAttribute("isEdit", true);
        model.addAttribute("isReadOnly", false);

        return "admin/ejemplar-form";
    }

    /**
     * Actualiza un ejemplar existente.
     */
    @PostMapping("/actualizar")
    public String updateBookCopy(
            @RequestParam String bookCopyId,
            @RequestParam String bookCopyStatusId,
            @RequestParam(required = false) String notes,
            RedirectAttributes redirectAttributes) {

        try {
            BookCopy bookCopy = bookCopyService.findById(bookCopyId);

            if (bookCopy == null) {
                redirectAttributes.addFlashAttribute("error", "Ejemplar no encontrado");
                return "redirect:/admin/ejemplares";
            }

            Optional<BookCopyStatus> newStatus = bookCopyStatusService.findById(UUID.fromString(bookCopyStatusId));

            if (newStatus.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Estado de ejemplar no encontrado");
                return "redirect:/admin/ejemplares";
            }

            // Verificar si está alquilado
            boolean isRented = bookCopy.getBookCopyStatus() != null &&
                    "Alquilado".equals(bookCopy.getBookCopyStatus().getBookCopyStatusName());

            if (isRented) {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede actualizar un ejemplar que está actualmente alquilado");
                return "redirect:/admin/ejemplares";
            }

            bookCopy.setBookCopyStatus(newStatus.get());
            bookCopy.setNotes(notes != null && !notes.trim().isEmpty() ? notes : null);

            bookCopyService.update(bookCopy);

            redirectAttributes.addFlashAttribute("success", "Ejemplar actualizado exitosamente");
            return "redirect:/admin/ejemplares";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar ejemplar: " + e.getMessage());
            return "redirect:/admin/ejemplares/editar/" + bookCopyId;
        }
    }

    /**
     * Actualiza el estado de múltiples ejemplares.
     */
    @PostMapping("/actualizar-estado-batch")
    public String updateStatusBatch(
            @RequestParam(value = "selectedCopies", required = false) List<String> selectedCopies,
            @RequestParam String newStatusId,
            RedirectAttributes redirectAttributes) {

        try {
            if (selectedCopies == null || selectedCopies.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un ejemplar");
                return "redirect:/admin/ejemplares";
            }

            bookCopyService.updateStatusBatch(selectedCopies, newStatusId);

            redirectAttributes.addFlashAttribute("success",
                    selectedCopies.size() + " ejemplar(es) actualizado(s) exitosamente");
            return "redirect:/admin/ejemplares";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", 
                "Error al actualizar ejemplares: " + e.getMessage());
            return "redirect:/admin/ejemplares";
        }
    }

    /**
     * Elimina un ejemplar.
     */
    @PostMapping("/eliminar/{id}")
    public String deleteBookCopy(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            BookCopy bookCopy = bookCopyService.findById(id);

            if (bookCopy == null) {
                redirectAttributes.addFlashAttribute("error", "Ejemplar no encontrado");
                return "redirect:/admin/ejemplares";
            }

            // Verificar si está alquilado
            boolean isRented = bookCopy.getBookCopyStatus() != null &&
                    "Alquilado".equals(bookCopy.getBookCopyStatus().getBookCopyStatusName());

            if (isRented) {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede eliminar un ejemplar que está actualmente alquilado");
                return "redirect:/admin/ejemplares";
            }

            bookCopyService.delete(id);

            redirectAttributes.addFlashAttribute("success", "Ejemplar eliminado exitosamente");
            return "redirect:/admin/ejemplares";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar ejemplar: " + e.getMessage());
            return "redirect:/admin/ejemplares";
        }
    }
}