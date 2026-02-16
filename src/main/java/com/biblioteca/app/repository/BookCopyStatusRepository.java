package com.biblioteca.app.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.biblioteca.app.entity.BookCopyStatus;

@Repository
public interface BookCopyStatusRepository extends JpaRepository<BookCopyStatus, UUID> {

    Optional<BookCopyStatus> findByBookCopyStatusName(String statusName);

}