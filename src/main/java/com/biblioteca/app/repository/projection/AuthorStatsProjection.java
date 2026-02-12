package com.biblioteca.app.repository.projection;

/**
 * Proyección para las estadísticas de autores
 * Utilizada en consultas nativas que retornan datos agregados
 */
public interface AuthorStatsProjection {
    
    String getAuthorId();
    
    String getFullName();
    
    String getPseudonym();
    
    String getPhotoUrl();
    
    String getCountryName();
    
    Integer getTotalBooks();
    
    Integer getTotalCopies();
    
    Integer getAvailableCopies();
    
    Integer getTotalRentals();
}