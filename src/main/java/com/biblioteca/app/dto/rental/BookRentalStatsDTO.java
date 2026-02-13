package com.biblioteca.app.dto.rental;

/**
 * DTO para estadísticas de alquiler de libros
 */
public class BookRentalStatsDTO {
    
    private String bookId;
    private String title;
    private String isbn;
    private String authorName;
    private String categoryName;
    private Integer rentalCount;
    private Integer activeRentals;

    // Constructor vacío
    public BookRentalStatsDTO() {
    }

    // Constructor completo
    public BookRentalStatsDTO(String bookId, String title, String isbn, String authorName, 
                             String categoryName, Integer rentalCount, Integer activeRentals) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.authorName = authorName;
        this.categoryName = categoryName;
        this.rentalCount = rentalCount;
        this.activeRentals = activeRentals;
    }

    // Getters y Setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getRentalCount() {
        return rentalCount;
    }

    public void setRentalCount(Integer rentalCount) {
        this.rentalCount = rentalCount;
    }

    public Integer getActiveRentals() {
        return activeRentals;
    }

    public void setActiveRentals(Integer activeRentals) {
        this.activeRentals = activeRentals;
    }

    @Override
    public String toString() {
        return "BookRentalStatsDTO{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", rentalCount=" + rentalCount +
                '}';
    }
}