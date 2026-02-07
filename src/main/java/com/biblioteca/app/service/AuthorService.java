package com.biblioteca.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.biblioteca.app.dto.AuthorActiveDTO;
import com.biblioteca.app.repository.AuthorRepository;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    public List<AuthorActiveDTO> getActiveAuthors() {
        return authorRepository.findActiveAuthors();
    }
}
