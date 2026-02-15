package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.biblioteca.app.entity.BookStatus;
import com.biblioteca.app.repository.BookStatusRepository;

@Service
public class BookStatusService {
    
    private final BookStatusRepository bookStatusRepository;
    
    public BookStatusService(BookStatusRepository bookStatusRepository) {
        this.bookStatusRepository = bookStatusRepository;
    }
    
    public List<BookStatus> findAll() {
        return bookStatusRepository.findAll();
    }
    
    public Optional<BookStatus> findById(UUID id) {
        return bookStatusRepository.findById(id);
    }
    
    public Optional<BookStatus> findByBookStatusName(String name) {
        return bookStatusRepository.findByBookStatusName(name);
    }
    
    public BookStatus save(BookStatus bookStatus) {
        return bookStatusRepository.save(bookStatus);
    }
    
    public void deleteById(UUID id) {
        bookStatusRepository.deleteById(id);
    }
    
    public long count() {
        return bookStatusRepository.count();
    }
    
    public boolean existsByName(String name) {
        return bookStatusRepository.existsByBookStatusName(name);
    }
}