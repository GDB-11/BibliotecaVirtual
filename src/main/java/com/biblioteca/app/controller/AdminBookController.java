package com.biblioteca.app.controller;

import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Author;
import com.biblioteca.app.entity.Book;
import com.biblioteca.app.entity.BookStatus;
import com.biblioteca.app.entity.Category;
import com.biblioteca.app.service.AuthorService;
import com.biblioteca.app.service.BookService;
import com.biblioteca.app.service.CategoryService;
import com.biblioteca.app.service.ConfigurationService;
import com.biblioteca.app.service.StatusService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

/**
 * Controlador para la gestión de libros en el panel de administración.
 * Maneja el CRUD completo de libros con paginación y filtros.
 */
@Controller
@RequestMapping("/admin/libros")
public class AdminBookController {
    
    private final BookService bookService;
    private final AuthorService authorService;
    private final CategoryService categoryService;
    private final StatusService statusService;
    private final ConfigurationService configurationService;
    
    public AdminBookController(BookService bookService,
                              AuthorService authorService,
                              CategoryService categoryService,
                              StatusService statusService,
                              ConfigurationService configurationService) {
        this.bookService = bookService;
        this.authorService = authorService;
        this.categoryService = categoryService;
        this.statusService = statusService;
        this.configurationService = configurationService;
    }
    
    /**
     * Muestra el listado de libros con paginación y filtros.
     */
    @GetMapping
    public String listBooks(
            @RequestParam(value = "p", defaultValue = "1") int page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "authorId", required = false) String authorId,
            @RequestParam(value = "categoryId", required = false) String categoryId,
            @RequestParam(value = "bookStatusId", required = false) String bookStatusId,
            Model model) {
        
        try {
            int itemsPerPage = size != null ? size : configurationService.getIntValue("ItemsPerPage", 15);
            
            UUID authorUuid = (authorId != null && !authorId.isEmpty()) ? UUID.fromString(authorId) : null;
            UUID categoryUuid = (categoryId != null && !categoryId.isEmpty()) ? UUID.fromString(categoryId) : null;
            UUID bookStatusUuid = (bookStatusId != null && !bookStatusId.isEmpty()) ? UUID.fromString(bookStatusId) : null;
            
            PagedResult<Book> booksResult = bookService.findAllPaginated(
                page, itemsPerPage, search, authorUuid, categoryUuid, bookStatusUuid);
            
            List<Author> authors = authorService.findAll();
            List<Category> categories = categoryService.findAll();
            List<BookStatus> bookStatuses = statusService.findAllBookStatuses();
            
            long totalBooks = bookService.count();
            long activeBooksCount = bookService.countActiveBooks();
            long booksWithActiveRentals = bookService.countBooksWithActiveRentals();
            
            model.addAttribute("booksResult", booksResult);
            model.addAttribute("authors", authors);
            model.addAttribute("categories", categories);
            model.addAttribute("bookStatuses", bookStatuses);
            model.addAttribute("totalBooks", totalBooks);
            model.addAttribute("activeBooksCount", activeBooksCount);
            model.addAttribute("booksWithActiveRentals", booksWithActiveRentals);
            
            model.addAttribute("searchValue", search != null ? search : "");
            model.addAttribute("authorIdValue", authorId != null ? authorId : "");
            model.addAttribute("categoryIdValue", categoryId != null ? categoryId : "");
            model.addAttribute("bookStatusIdValue", bookStatusId != null ? bookStatusId : "");
            model.addAttribute("itemsPerPage", itemsPerPage);
            
            return "admin/libros";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar libros: " + e.getMessage());
            return "admin/libros";
        }
    }
    
    /**
     * Muestra el formulario para crear un nuevo libro.
     */
    @GetMapping("/nuevo")
    public String newBookForm(Model model) {
        try {
            Book book = new Book();
            
            List<Author> authors = authorService.findAll();
            List<Category> categories = categoryService.findAll();
            List<BookStatus> bookStatuses = statusService.findAllBookStatuses();
            
            model.addAttribute("book", book);
            model.addAttribute("authors", authors);
            model.addAttribute("categories", categories);
            model.addAttribute("bookStatuses", bookStatuses);
            model.addAttribute("isNew", true);
            model.addAttribute("isReadOnly", false);
            
            return "admin/libro-form";
            
        } catch (Exception e) {
            model.addAttribute("error", "Error al cargar el formulario: " + e.getMessage());
            return "redirect:/admin/libros";
        }
    }
    
    /**
     * Muestra el formulario para editar un libro existente.
     */
    @GetMapping("/{id}/editar")
    public String editBookForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID bookId = UUID.fromString(id);
            Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            
            List<Author> authors = authorService.findAll();
            List<Category> categories = categoryService.findAll();
            List<BookStatus> bookStatuses = statusService.findAllBookStatuses();
            
            model.addAttribute("book", book);
            model.addAttribute("authors", authors);
            model.addAttribute("categories", categories);
            model.addAttribute("bookStatuses", bookStatuses);
            model.addAttribute("isNew", false);
            model.addAttribute("isReadOnly", false);
            
            return "admin/libro-form";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/libros";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el libro: " + e.getMessage());
            return "redirect:/admin/libros";
        }
    }
    
    /**
     * Muestra los detalles de un libro (solo lectura).
     */
    @GetMapping("/{id}")
    public String viewBook(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        try {
            UUID bookId = UUID.fromString(id);
            Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            
            List<Author> authors = authorService.findAll();
            List<Category> categories = categoryService.findAll();
            List<BookStatus> bookStatuses = statusService.findAllBookStatuses();
            
            model.addAttribute("book", book);
            model.addAttribute("authors", authors);
            model.addAttribute("categories", categories);
            model.addAttribute("bookStatuses", bookStatuses);
            model.addAttribute("isNew", false);
            model.addAttribute("isReadOnly", true);
            
            return "admin/libro-form";
            
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/admin/libros";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al cargar el libro: " + e.getMessage());
            return "redirect:/admin/libros";
        }
    }
    
    /**
     * Crea un nuevo libro.
     */
    @PostMapping("/nuevo")
    public String createBook(
            @RequestParam String isbn,
            @RequestParam String title,
            @RequestParam String authorId,
            @RequestParam String categoryId,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) Integer publicationYear,
            @RequestParam(required = false) Integer pages,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String coverImageUrl,
            @RequestParam String bookStatusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            // Validar que no exista un libro con el mismo ISBN
            if (bookService.existsByIsbn(isbn)) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un libro con ese ISBN");
                return "redirect:/admin/libros/nuevo";
            }
            
            Book book = new Book();
            book.setIsbn(isbn);
            book.setTitle(title);
            
            // Configurar autor
            Author author = authorService.findById(UUID.fromString(authorId))
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            book.setAuthor(author);
            
            // Configurar categoría
            Category category = categoryService.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            book.setCategory(category);
            
            // Configurar estado del libro
            BookStatus bookStatus = statusService.findBookStatusById(UUID.fromString(bookStatusId))
                .orElseThrow(() -> new IllegalArgumentException("Estado del libro no encontrado"));
            book.setBookStatus(bookStatus);
            
            book.setPublisher(publisher);
            book.setPublicationYear(publicationYear);
            book.setPages(pages);
            book.setLanguage(language != null && !language.isEmpty() ? language : "Español");
            book.setDescription(description);
            book.setCoverImageUrl(coverImageUrl);
            
            bookService.save(book);
            
            redirectAttributes.addFlashAttribute("success", "Libro creado exitosamente");
            return "redirect:/admin/libros";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear el libro: " + e.getMessage());
            return "redirect:/admin/libros/nuevo";
        }
    }
    
    /**
     * Actualiza un libro existente.
     */
    @PostMapping("/{id}/editar")
    public String updateBook(
            @PathVariable String id,
            @RequestParam String isbn,
            @RequestParam String title,
            @RequestParam String authorId,
            @RequestParam String categoryId,
            @RequestParam(required = false) String publisher,
            @RequestParam(required = false) Integer publicationYear,
            @RequestParam(required = false) Integer pages,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String coverImageUrl,
            @RequestParam String bookStatusId,
            RedirectAttributes redirectAttributes) {
        
        try {
            UUID bookId = UUID.fromString(id);
            Book book = bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            
            // Validar ISBN si cambió
            if (!book.getIsbn().equals(isbn) && bookService.existsByIsbn(isbn)) {
                redirectAttributes.addFlashAttribute("error", "Ya existe un libro con ese ISBN");
                return "redirect:/admin/libros/" + id + "/editar";
            }
            
            book.setIsbn(isbn);
            book.setTitle(title);
            
            // Actualizar autor
            Author author = authorService.findById(UUID.fromString(authorId))
                .orElseThrow(() -> new IllegalArgumentException("Autor no encontrado"));
            book.setAuthor(author);
            
            // Actualizar categoría
            Category category = categoryService.findById(UUID.fromString(categoryId))
                .orElseThrow(() -> new IllegalArgumentException("Categoría no encontrada"));
            book.setCategory(category);
            
            // Actualizar estado del libro
            BookStatus bookStatus = statusService.findBookStatusById(UUID.fromString(bookStatusId))
                .orElseThrow(() -> new IllegalArgumentException("Estado del libro no encontrado"));
            book.setBookStatus(bookStatus);
            
            book.setPublisher(publisher);
            book.setPublicationYear(publicationYear);
            book.setPages(pages);
            book.setLanguage(language != null && !language.isEmpty() ? language : "Español");
            book.setDescription(description);
            book.setCoverImageUrl(coverImageUrl);
            
            bookService.save(book);
            
            redirectAttributes.addFlashAttribute("success", "Libro actualizado exitosamente");
            return "redirect:/admin/libros";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el libro: " + e.getMessage());
            return "redirect:/admin/libros/" + id + "/editar";
        }
    }
    
    /**
     * Elimina un libro.
     */
    @PostMapping("/{id}/eliminar")
    public String deleteBook(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            UUID bookId = UUID.fromString(id);
            
            bookService.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
            
            bookService.delete(bookId);
            
            redirectAttributes.addFlashAttribute("success", "Libro eliminado exitosamente");
            return "redirect:/admin/libros";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al eliminar el libro: " + e.getMessage());
            return "redirect:/admin/libros";
        }
    }
}