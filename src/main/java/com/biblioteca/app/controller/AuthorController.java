package com.biblioteca.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.app.dto.author.AuthorActiveDTO;
import com.biblioteca.app.service.AuthorService;

@RestController
@RequestMapping("/api/authors")
public class AuthorController {

    private final AuthorService authorService;

    public AuthorController(AuthorService authorService) {
        this.authorService = authorService;
    }

    @GetMapping("/active")
    public List<AuthorActiveDTO> getActiveAuthors() {
        return authorService.getActiveAuthors();
    }
}
