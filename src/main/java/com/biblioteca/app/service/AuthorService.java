package com.biblioteca.app.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.dto.author.AuthorActiveDTO;
import com.biblioteca.app.dto.author.AuthorDTO;
import com.biblioteca.app.dto.author.AuthorStatsDTO;
import com.biblioteca.app.dto.shared.PagedResult;
import com.biblioteca.app.entity.Author;
import com.biblioteca.app.helper.PageMapper;
import com.biblioteca.app.repository.AuthorRepository;
import com.biblioteca.app.repository.projection.AuthorStatsProjection;

/**
 * Servicio para la gestión de autores.
 * Todos los métodos de listado retornan PagedResult<DTO> para optimizar transferencia de datos.
 */
@Service
@Transactional(readOnly = true)
public class AuthorService {

    @Autowired
    private AuthorRepository authorRepository;

    /**
     * Búsqueda paginada de autores con filtros.
     * Retorna PagedResult con DTOs para optimizar transferencia.
     * 
     * @param page Número de página (base 1, primera página = 1)
     * @param size Tamaño de página
     * @param search Texto de búsqueda (nombre o pseudónimo)
     * @param countryId Filtro por país (opcional)
     * @param statusId Filtro por estado (opcional)
     * @return PagedResult con AuthorDTOs
     */
    public PagedResult<AuthorDTO> findAllPaginated(int page, int size, String search, 
                                                   UUID countryId, UUID statusId) {
        int springPage = PageMapper.toSpringPageNumber(page);
        
        Pageable pageable = PageRequest.of(springPage, size, Sort.by("fullName").ascending());
        
        Page<Author> springPageResult = authorRepository.findAllWithFilters(
            search, countryId, statusId, pageable
        );
        
        return PageMapper.toPagedResult(springPageResult, page, this::toDTO);
    }

    /**
     * Obtiene un autor por ID.
     * 
     * @param authorId UUID del autor
     * @return Optional con el autor si existe
     */
    public Optional<Author> findById(UUID authorId) {
        return authorRepository.findById(authorId);
    }

    /**
     * Obtiene un autor DTO por ID.
     * 
     * @param authorId UUID del autor
     * @return Optional con AuthorDTO si existe
     */
    public Optional<AuthorDTO> findDTOById(UUID authorId) {
        return authorRepository.findById(authorId).map(this::toDTO);
    }

    /**
     * Busca un autor por nombre completo.
     * 
     * @param fullName Nombre completo del autor
     * @return Optional con el autor si existe
     */
    public Optional<Author> findByName(String fullName) {
        return authorRepository.findByFullName(fullName);
    }

    /**
     * Obtiene todos los autores como DTOs para selectores/dropdowns.
     * Sin paginación - usar solo para listas cortas.
     * 
     * @return Lista de AuthorDTOs ordenados por nombre
     */
    public List<AuthorDTO> findAllForSelector() {
        return authorRepository.findAllByOrderByFullNameAsc()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene autores por país como DTOs.
     * 
     * @param countryId UUID del país
     * @return Lista de AuthorDTOs
     */
    public List<AuthorDTO> findByCountry(UUID countryId) {
        return authorRepository.findByCountry_CountryIdOrderByFullNameAsc(countryId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Obtiene autores por estado como DTOs.
     * 
     * @param statusId UUID del estado
     * @return Lista de AuthorDTOs
     */
    public List<AuthorDTO> findByStatus(UUID statusId) {
        return authorRepository.findByStatus_StatusIdOrderByFullNameAsc(statusId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    /**
     * Guarda o actualiza un autor.
     * 
     * @param author Entidad autor a guardar
     * @return Autor guardado
     */
    @Transactional
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    /**
     * Elimina un autor por ID.
     * 
     * @param authorId UUID del autor a eliminar
     */
    @Transactional
    public void delete(UUID authorId) {
        authorRepository.deleteById(authorId);
    }

    /**
     * Verifica si existe un autor con el nombre dado.
     * 
     * @param fullName Nombre completo
     * @return true si existe, false si no
     */
    public boolean existsByName(String fullName) {
        return authorRepository.existsByFullName(fullName);
    }

    /**
     * Cuenta el número de libros de un autor.
     * 
     * @param authorId UUID del autor
     * @return Cantidad de libros
     */
    public int countAuthorBooks(UUID authorId) {
        return authorRepository.countBooksByAuthorId(authorId);
    }

    /**
     * Cuenta autores con filtros.
     * 
     * @param search Texto de búsqueda
     * @param countryId Filtro por país
     * @param statusId Filtro por estado
     * @return Total de autores que coinciden con los filtros
     */
    public long countWithFilters(String search, UUID countryId, UUID statusId) {
        return authorRepository.countWithFilters(search, countryId, statusId);
    }

    /**
     * Cuenta total de autores.
     * 
     * @return Total de autores
     */
    public long count() {
        return authorRepository.count();
    }

    /**
     * Obtiene los autores más solicitados con estadísticas.
     * 
     * @param countryId Filtro por país (opcional)
     * @param statusId Filtro por estado (opcional)
     * @param limit Cantidad máxima de resultados
     * @return Lista de AuthorStatsDTOs
     */
    public List<AuthorStatsDTO> getMostRequestedAuthors(UUID countryId, UUID statusId, int limit) {
        String countryIdStr = countryId != null ? countryId.toString() : null;
        String statusIdStr = statusId != null ? statusId.toString() : null;
        
        List<AuthorStatsProjection> projections = authorRepository.getMostRequestedAuthors(
            countryIdStr, statusIdStr, limit
        );
        
        return projections.stream()
            .map(this::toStatsDTO)
            .collect(Collectors.toList());
    }

    /**
     * Cuenta autores que tienen al menos un alquiler.
     * 
     * @return Cantidad de autores con alquileres
     */
    public long countAuthorsWithRentals() {
        return authorRepository.countAuthorsWithRentals();
    }

    /**
     * Obtiene el total de alquileres de todos los autores.
     * 
     * @return Total de alquileres
     */
    public long getTotalAuthorsRentals() {
        return authorRepository.getTotalAuthorsRentals();
    }

    /**
     * Cuenta autores que tienen al menos un libro.
     * 
     * @return Cantidad de autores con libros
     */
    public long countAuthorsWithBooks() {
        return authorRepository.countAuthorsWithBooks();
    }

    public List<AuthorActiveDTO> getActiveAuthors() {
        return authorRepository.findActiveAuthors();
    }

    // ========== MÉTODOS PRIVADOS DE CONVERSIÓN ==========

    /**
     * Convierte una entidad Author a AuthorDTO.
     * 
     * @param author Entidad autor
     * @return AuthorDTO
     */
    private AuthorDTO toDTO(Author author) {
        AuthorDTO dto = new AuthorDTO();
        dto.setAuthorId(author.getAuthorId().toString());
        dto.setFullName(author.getFullName());
        dto.setPseudonym(author.getPseudonym());
        dto.setCountryName(author.getCountry().getCountryName());
        dto.setCountryCode(author.getCountry().getCountryCode());
        dto.setStatusName(author.getStatus().getStatusName());
        dto.setBirthYear(author.getBirthYear());
        dto.setDeathYear(author.getDeathYear());
        dto.setPhotoUrl(author.getPhotoUrl());
        return dto;
    }

    /**
     * Convierte una proyección de estadísticas a AuthorStatsDTO.
     * 
     * @param projection Proyección de estadísticas
     * @return AuthorStatsDTO
     */
    private AuthorStatsDTO toStatsDTO(AuthorStatsProjection projection) {
        return new AuthorStatsDTO(
            projection.getAuthorId(),
            projection.getFullName(),
            projection.getPseudonym(),
            projection.getPhotoUrl(),
            projection.getCountryName(),
            projection.getTotalBooks(),
            projection.getTotalCopies(),
            projection.getAvailableCopies(),
            projection.getTotalRentals()
        );
    }
}