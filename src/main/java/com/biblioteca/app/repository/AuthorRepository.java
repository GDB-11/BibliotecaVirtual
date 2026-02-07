package com.biblioteca.app.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.biblioteca.app.dto.AuthorActiveDTO;
import com.biblioteca.app.entity.Author;

public interface AuthorRepository extends JpaRepository<Author, Long> {

	@Query(value = """
		    SELECT 
		        a.FullName AS autor,
		        c.CountryName AS pais,
		        a.BirthYear AS anioNacimiento,
		        a.DeathYear AS anioFallecimiento
		    FROM Author a
		    JOIN Country c ON a.CountryId = c.CountryId
		    JOIN Status s ON a.StatusId = s.StatusId
		    WHERE s.StatusName = 'Active'
		    ORDER BY a.FullName
		""", nativeQuery = true)
		List<AuthorActiveDTO> findActiveAuthors();
}
