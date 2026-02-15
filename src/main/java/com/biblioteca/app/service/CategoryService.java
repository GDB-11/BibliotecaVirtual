package com.biblioteca.app.service;

import com.biblioteca.app.entity.Category;
import com.biblioteca.app.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Servicio para la gestión de categorías.
 * Proporciona operaciones CRUD y consultas relacionadas con categorías.
 */
@Service
@Transactional(readOnly = true)
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    /**
     * Obtiene todas las categorías.
     * 
     * @return Lista de todas las categorías
     */
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
    
    /**
     * Obtiene una categoría por su ID.
     * 
     * @param categoryId UUID de la categoría
     * @return Optional con la categoría si existe
     */
    public Optional<Category> findById(UUID categoryId) {
        return categoryRepository.findById(categoryId);
    }
    
    /**
     * Obtiene una categoría por su nombre.
     * 
     * @param categoryName Nombre de la categoría
     * @return Optional con la categoría si existe
     */
    public Optional<Category> findByName(String categoryName) {
        return categoryRepository.findByCategoryName(categoryName);
    }
    
    /**
     * Verifica si existe una categoría con el nombre dado.
     * 
     * @param categoryName Nombre de la categoría
     * @return true si existe, false si no
     */
    public boolean existsByName(String categoryName) {
        return categoryRepository.existsByCategoryName(categoryName);
    }
    
    /**
     * Guarda o actualiza una categoría.
     * 
     * @param category Categoría a guardar
     * @return Categoría guardada
     */
    @Transactional
    public Category save(Category category) {
        return categoryRepository.save(category);
    }
    
    /**
     * Elimina una categoría por su ID.
     * 
     * @param categoryId UUID de la categoría a eliminar
     */
    @Transactional
    public void delete(UUID categoryId) {
        categoryRepository.deleteById(categoryId);
    }
    
    /**
     * Cuenta el total de categorías.
     * 
     * @return Total de categorías
     */
    public long count() {
        return categoryRepository.count();
    }
}