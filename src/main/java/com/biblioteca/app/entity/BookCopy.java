package com.biblioteca.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "BookCopy")
public class BookCopy {

    @Id
    @Column(name = "BookCopyId")
    private Long bookCopyId;

    @Column(name = "BookId")
    private Long bookId;

	public Long getBookCopyId() {
		return bookCopyId;
	}

	public Long getBookId() {
		return bookId;
	}

	public void setBookCopyId(Long bookCopyId) {
		this.bookCopyId = bookCopyId;
	}

	public void setBookId(Long bookId) {
		this.bookId = bookId;
	}
    
    
}
