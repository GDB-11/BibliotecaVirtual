package com.biblioteca.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    /**
     * Busca una categoría por su nombre
     */
    Optional<Category> findByCategoryName(String categoryName);
    
    /**
     * Verifica si existe una categoría con el nombre dado
     */
    boolean existsByCategoryName(String categoryName);
}