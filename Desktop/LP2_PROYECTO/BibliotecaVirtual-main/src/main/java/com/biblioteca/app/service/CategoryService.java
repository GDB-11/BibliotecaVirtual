package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.biblioteca.app.entity.Category;
import com.biblioteca.app.repository.CategoryRepository;

@Service
public class CategoryService {
    
    private final CategoryRepository categoryRepository;
    
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    
    public Optional<Category> findById(UUID id) {
        return categoryRepository.findById(id);
    }
    
    public Optional<Category> findByCategoryName(String name) {
        return categoryRepository.findByCategoryName(name);
    }
    
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    public void deleteById(UUID id) {
        categoryRepository.deleteById(id);
    }
    
    public long count() {
        return categoryRepository.count();
    }
    
    public boolean existsByName(String name) {
        return categoryRepository.existsByCategoryName(name);
    }
}