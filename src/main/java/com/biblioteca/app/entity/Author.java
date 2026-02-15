package com.biblioteca.app.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "Author")
public class Author {

    @Id
    @UuidGenerator
    @Column(name = "AuthorId", columnDefinition = "BINARY(16)", nullable = false)
    private UUID authorId;

    @Column(name = "FullName", length = 255, nullable = false)
    private String fullName;

    @Column(name = "Pseudonym", length = 255)
    private String pseudonym;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "CountryId", nullable = false, foreignKey = @ForeignKey(name = "fk_author_country"))
    private Country country;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "StatusId", nullable = false, foreignKey = @ForeignKey(name = "fk_author_status"))
    private Status status;

    @Column(name = "Biography", columnDefinition = "TEXT")
    private String biography;

    @Column(name = "BirthYear")
    private Integer birthYear;

    @Column(name = "DeathYear")
    private Integer deathYear;

    @Column(name = "Website", length = 500)
    private String website;

    @Column(name = "Email", length = 255)
    private String email;

    @Column(name = "PhotoUrl", length = 500)
    private String photoUrl;

    @Column(name = "CreatedAt", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UpdatedAt")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructores
    public Author() {
    }

    public Author(UUID authorId, String fullName, Country country, Status status) {
        this.authorId = authorId;
        this.fullName = fullName;
        this.country = country;
        this.status = status;
    }

    // Getters y Setters
    public UUID getAuthorId() {
        return authorId;
    }

    public void setAuthorId(UUID authorId) {
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

    public Country getCountry() {
        return country;
    }

    public void setCountry(Country country) {
        this.country = country;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getBiography() {
        return biography;
    }

    public void setBiography(String biography) {
        this.biography = biography;
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

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return fullName;
    }
}