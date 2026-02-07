package com.biblioteca.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BookStatus")
public class BookStatus {

    @Id
    @Column(name = "BookStatusId")
    private Long bookStatusId;

    @Column(name = "BookStatusName")
    private String bookStatusName;

    public Long getBookStatusId() {
        return bookStatusId;
    }

    public void setBookStatusId(Long bookStatusId) {
        this.bookStatusId = bookStatusId;
    }

    public String getBookStatusName() {
        return bookStatusName;
    }

    public void setBookStatusName(String bookStatusName) {
        this.bookStatusName = bookStatusName;
    }
}