package com.biblioteca.app.repository.projection;

/**
 * Proyección para estadísticas de alquiler de libros
 */
public interface BookRentalStatsProjection {
    
    String getBookId();
    
    String getTitle();
    
    String getIsbn();
    
    String getAuthorName();
    
    String getCategoryName();
    
    Integer getRentalCount();
    
    Integer getActiveRentals();
}