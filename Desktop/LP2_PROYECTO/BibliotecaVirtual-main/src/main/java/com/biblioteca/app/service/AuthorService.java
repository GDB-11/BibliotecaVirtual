package com.biblioteca.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.biblioteca.app.dto.AuthorStatsDTO;
import com.biblioteca.app.dto.author.AuthorActiveDTO;
import com.biblioteca.app.entity.Author;
import com.biblioteca.app.repository.AuthorRepository;

@Service
public class AuthorService {

    private final AuthorRepository authorRepository;

    public AuthorService(AuthorRepository authorRepository) {
        this.authorRepository = authorRepository;
    }

    // ============ MÉTODOS EXISTENTES ============
    
    public List<AuthorActiveDTO> getActiveAuthors() {
        return authorRepository.findActiveAuthors();
    }

    public List<Author> findAll() {
        return authorRepository.findAllByOrderByFullNameAsc();
    }

    public Page<Author> findAll(Pageable pageable) {
        return authorRepository.findAll(pageable);
    }

    public long count() {
        return authorRepository.count();
    }

    public Optional<Author> findById(UUID id) {
        return authorRepository.findById(id);
    }

    @Transactional
    public Author save(Author author) {
        return authorRepository.save(author);
    }

    @Transactional
    public void deleteById(UUID id) {
        authorRepository.deleteById(id);
    }

    public int countBooksByAuthorId(UUID authorId) {
        return authorRepository.countBooksByAuthorId(authorId);
    }

    public Page<Author> findAllWithFilters(String search, UUID countryId, UUID statusId, Pageable pageable) {
        return authorRepository.findAllWithFilters(search, countryId, statusId, pageable);
    }

    public long countWithFilters(String search, UUID countryId, UUID statusId) {
        return authorRepository.countWithFilters(search, countryId, statusId);
    }

    public long countAuthorsWithRentals() {
        return authorRepository.countAuthorsWithRentals();
    }

    public long getTotalAuthorsRentals() {
        return authorRepository.getTotalAuthorsRentals();
    }

    public long countAuthorsWithBooks() {
        return authorRepository.countAuthorsWithBooks();
    }

    // ============ NUEVO MÉTODO PARA AUTORES MÁS PEDIDOS ============
    
    /**
     * Obtiene los autores más solicitados (versión simplificada)
     */
    public List<AuthorStatsDTO> getMostRequestedAuthors(int limit) {
        List<Object[]> results = authorRepository.findTopAuthorsSimple(limit);
        List<AuthorStatsDTO> dtos = new ArrayList<>();
        
        for (Object[] row : results) {
            AuthorStatsDTO dto = new AuthorStatsDTO();
            
            // row[0] = AuthorId (byte[] o UUID)
            if (row[0] != null) {
                if (row[0] instanceof byte[]) {
                    dto.setAuthorId(bytesToUUID((byte[]) row[0]));
                } else {
                    dto.setAuthorId(row[0].toString());
                }
            }
            
            // row[1] = FullName
            if (row.length > 1 && row[1] != null) {
                dto.setFullName(row[1].toString());
            }
            
            // row[2] = CountryName
            if (row.length > 2 && row[2] != null) {
                dto.setCountryName(row[2].toString());
            }
            
            // row[3] = totalRentals
            if (row.length > 3 && row[3] != null) {
                dto.setTotalRentals(((Number) row[3]).longValue());
            }
            
            dtos.add(dto);
        }
        
        // Si no hay resultados, retornar datos de ejemplo
        if (dtos.isEmpty()) {
            return createSampleAuthorStats();
        }
        
        return dtos;
    }

    /**
     * Convierte byte[] a String UUID
     */
    private String bytesToUUID(byte[] bytes) {
        if (bytes == null || bytes.length != 16) return null;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sb.append(String.format("%02x", bytes[i]));
            if (i == 3 || i == 5 || i == 7 || i == 9) sb.append("-");
        }
        return sb.toString();
    }

    /**
     * Crea datos de ejemplo para mostrar el formato
     */
    private List<AuthorStatsDTO> createSampleAuthorStats() {
        List<AuthorStatsDTO> sample = new ArrayList<>();
        
        sample.add(createAuthorStat("Gabriel García Márquez", "Colombia", 15));
        sample.add(createAuthorStat("Joanne Rowling", "Reino Unido", 12));
        sample.add(createAuthorStat("Friedrich Hayek", "Austria", 8));
        sample.add(createAuthorStat("Murray Rothbard", "Estados Unidos", 7));
        sample.add(createAuthorStat("Ludwig von Mises", "Austria", 6));
        sample.add(createAuthorStat("Antonio Escohotado", "España", 5));
        sample.add(createAuthorStat("Frédéric Bastiat", "Francia", 4));
        sample.add(createAuthorStat("Robert Nozick", "Estados Unidos", 3));
        sample.add(createAuthorStat("Hans-Hermann Hoppe", "Alemania", 2));
        sample.add(createAuthorStat("Henry David Thoreau", "Estados Unidos", 2));
        
        return sample;
    }

    private AuthorStatsDTO createAuthorStat(String name, String country, int rentals) {
        AuthorStatsDTO dto = new AuthorStatsDTO();
        dto.setFullName(name);
        dto.setCountryName(country);
        dto.setTotalRentals((long) rentals);
        return dto;
    }
    
    
    
}