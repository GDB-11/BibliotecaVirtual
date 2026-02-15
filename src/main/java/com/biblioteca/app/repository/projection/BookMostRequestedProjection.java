package com.biblioteca.app.repository.projection;

/**
 * Proyección para libros más pedidos con información de tendencias
 */
public interface BookMostRequestedProjection {
    
    String getBookId();
    
    String getTitle();
    
    String getIsbn();
    
    String getAuthorName();
    
    String getCategoryName();
    
    Integer getTotalRentals();
    
    Integer getYesterdayRentals();
    
    Integer getTodayRentals();
}