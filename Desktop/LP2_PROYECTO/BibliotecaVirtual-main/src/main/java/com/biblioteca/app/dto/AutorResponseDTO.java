package com.biblioteca.app.dto;

public interface AutorResponseDTO {
    Long getAuthorId();
    String getFullName();
    Integer getBirthYear();
    Integer getDeathYear();
    String getCountryName();
    String getStatusName();
}