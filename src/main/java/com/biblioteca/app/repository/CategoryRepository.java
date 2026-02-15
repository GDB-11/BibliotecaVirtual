package com.biblioteca.app.repository;

import com.biblioteca.app.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio JPA para la entidad Category.
 * Proporciona métodos CRUD y consultas personalizadas.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    
    /**
     * Busca una categoría por su nombre.
     * 
     * @param categoryName Nombre de la categoría
     * @return Optional con la categoría si existe
     */
    Optional<Category> findByCategoryName(String categoryName);
    
    /**
     * Verifica si existe una categoría con el nombre dado.
     * 
     * @param categoryName Nombre de la categoría
     * @return true si existe, false si no
     */
    boolean existsByCategoryName(String categoryName);
}