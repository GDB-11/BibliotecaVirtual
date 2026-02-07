package com.biblioteca.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.biblioteca.app.dto.BookActiveDTO;
import com.biblioteca.app.repository.BookRepository;

@Service
public class BookService {

    private final BookRepository bookRepository;

    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    public List<BookActiveDTO> getActiveBooks() {
        return bookRepository.findActiveBooks();
    }
}