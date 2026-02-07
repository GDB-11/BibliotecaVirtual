package com.biblioteca.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "Author")
public class Author {

    @Id
    @Column(name = "AuthorId")
    private Long authorId;

    @Column(name = "FullName")
    private String fullName;

    @Column(name = "BirthYear")
    private Integer birthYear;

    @Column(name = "DeathYear")
    private Integer deathYear;

    @Column(name = "CountryId")
    private Long countryId;

    @Column(name = "StatusId")
    private Long statusId;

    // ===== GETTERS & SETTERS =====

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Long authorId) {
        this.authorId = authorId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public Long getCountryId() {
        return countryId;
    }

    public void setCountryId(Long countryId) {
        this.countryId = countryId;
    }

    public Long getStatusId() {
        return statusId;
    }

    public void setStatusId(Long statusId) {
        this.statusId = statusId;
    }
}