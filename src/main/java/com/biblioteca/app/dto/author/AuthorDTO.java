package com.biblioteca.app.dto.author;

/**
 * DTO para exponer información de autores en la API.
 * Versión simplificada de la entidad Author sin relaciones JPA.
 */
public class AuthorDTO {
    
    private String authorId;
    private String fullName;
    private String pseudonym;
    private String countryName;
    private String countryCode;
    private String statusName;
    private Integer birthYear;
    private Integer deathYear;
    private String email;
    private String website;
    private String biography;
    private String photoUrl;

    // Constructores
    public AuthorDTO() {
    }

    public AuthorDTO(String authorId, String fullName, String countryName, String statusName) {
        this.authorId = authorId;
        this.fullName = fullName;
        this.countryName = countryName;
        this.statusName = statusName;
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

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Integer getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(Integer birthYear) {
        this.birthYear = birthYear;
    }

    public Integer getDeathYear() {
        return deathYear;
    }

    public void setDeathYear(Integer deathYear) {
        this.deathYear = deathYear;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
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
        return "AuthorDTO{" +
                "authorId='" + authorId + '\'' +
                ", fullName='" + fullName + '\'' +
                ", countryName='" + countryName + '\'' +
                '}';
    }
}