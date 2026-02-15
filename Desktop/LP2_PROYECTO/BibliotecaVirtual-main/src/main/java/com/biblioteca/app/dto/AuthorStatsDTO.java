package com.biblioteca.app.dto;

public class AuthorStatsDTO {
    private String authorId;
    private String fullName;
    private String pseudonym;
    private String photoUrl;
    private String countryName;
    private Long totalBooks;
    private Long totalCopies;
    private Long availableCopies;
    private Long totalRentals;
    
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
    
    public Long getTotalBooks() {
        return totalBooks;
    }
    
    public void setTotalBooks(Long totalBooks) {
        this.totalBooks = totalBooks;
    }
    
    public Long getTotalCopies() {
        return totalCopies;
    }
    
    public void setTotalCopies(Long totalCopies) {
        this.totalCopies = totalCopies;
    }
    
    public Long getAvailableCopies() {
        return availableCopies;
    }
    
    public void setAvailableCopies(Long availableCopies) {
        this.availableCopies = availableCopies;
    }
    
    public Long getTotalRentals() {
        return totalRentals;
    }
    
    public void setTotalRentals(Long totalRentals) {
        this.totalRentals = totalRentals;
    }
}