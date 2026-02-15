package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.biblioteca.app.entity.BookCopyStatus;
import com.biblioteca.app.repository.BookCopyStatusRepository;

@Service
public class BookCopyStatusService {
    
    private final BookCopyStatusRepository bookCopyStatusRepository;
    
    public BookCopyStatusService(BookCopyStatusRepository bookCopyStatusRepository) {
        this.bookCopyStatusRepository = bookCopyStatusRepository;
    }
    
    public List<BookCopyStatus> findAll() {
        return bookCopyStatusRepository.findAll();
    }
    
    public Optional<BookCopyStatus> findById(UUID id) {
        return bookCopyStatusRepository.findById(id);
    }
    
    public Optional<BookCopyStatus> findByBookCopyStatusName(String name) {
        return bookCopyStatusRepository.findByBookCopyStatusName(name);
    }
    
    public BookCopyStatus save(BookCopyStatus bookCopyStatus) {
        return bookCopyStatusRepository.save(bookCopyStatus);
    }
    
    public void deleteById(UUID id) {
        bookCopyStatusRepository.deleteById(id);
    }
    
    public long count() {
        return bookCopyStatusRepository.count();
    }
    
    public boolean existsByName(String name) {
        return bookCopyStatusRepository.existsByBookCopyStatusName(name);
    }
}