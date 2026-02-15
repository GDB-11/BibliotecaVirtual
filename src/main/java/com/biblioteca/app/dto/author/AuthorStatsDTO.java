package com.biblioteca.app.dto.author;

/**
 * DTO para las estadísticas de autores
 * Contiene información agregada sobre libros, copias y alquileres
 */
public class AuthorStatsDTO {
    
    private String authorId;
    private String fullName;
    private String pseudonym;
    private String photoUrl;
    private String countryName;
    private Integer totalBooks;
    private Integer totalCopies;
    private Integer availableCopies;
    private Integer totalRentals;

    // Constructor vacío
    public AuthorStatsDTO() {
    }

    // Constructor completo
    public AuthorStatsDTO(String authorId, String fullName, String pseudonym, String photoUrl,
                         String countryName, Integer totalBooks, Integer totalCopies,
                         Integer availableCopies, Integer totalRentals) {
        this.authorId = authorId;
        this.fullName = fullName;
        this.pseudonym = pseudonym;
        this.photoUrl = photoUrl;
        this.countryName = countryName;
        this.totalBooks = totalBooks;
        this.totalCopies = totalCopies;
        this.availableCopies = availableCopies;
        this.totalRentals = totalRentals;
    }

    // Getters y Setters
    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPseudonym() {
        return pseudonym;
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public Integer getTotalBooks() {
        return totalBooks;
    }

    public void setTotalBooks(Integer totalBooks) {
        this.totalBooks = totalBooks;
    }

    public Integer getTotalCopies() {
        return totalCopies;
    }

    public void setTotalCopies(Integer totalCopies) {
        this.totalCopies = totalCopies;
    }

    public Integer getAvailableCopies() {
        return availableCopies;
    }

    public void setAvailableCopies(Integer availableCopies) {
        this.availableCopies = availableCopies;
    }

    public Integer getTotalRentals() {
        return totalRentals;
    }

    public void setTotalRentals(Integer totalRentals) {
        this.totalRentals = totalRentals;
    }

    /**
     * Calcula el promedio de alquileres por libro
     */
    public Double getAvgRentalsPerBook() {
        if (totalBooks == null || totalBooks == 0) {
            return 0.0;
        }
        if (totalRentals == null) {
            return 0.0;
        }
        return (double) totalRentals / totalBooks;
    }

    /**
     * Calcula la tasa de disponibilidad (porcentaje de copias disponibles)
     */
    public Double getAvailabilityRate() {
        if (totalCopies == null || totalCopies == 0) {
            return 0.0;
        }
        if (availableCopies == null) {
            return 0.0;
        }
        return ((double) availableCopies / totalCopies) * 100.0;
    }

    /**
     * Nombre a mostrar (pseudónimo si existe, sino nombre completo)
     */
    public String getDisplayName() {
        return (pseudonym != null && !pseudonym.trim().isEmpty()) 
            ? pseudonym 
            : fullName;
    }

    @Override
    public String toString() {
        return "AuthorStatsDTO{" +
                "authorId='" + authorId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", totalBooks=" + totalBooks +
                ", totalRentals=" + totalRentals +
                '}';
    }
}