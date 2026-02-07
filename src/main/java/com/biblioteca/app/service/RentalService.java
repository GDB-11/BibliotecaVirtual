package com.biblioteca.app.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.biblioteca.app.dto.RentalActiveDTO;
import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.repository.RentalRepository;

@Service
public class RentalService {

    private final RentalRepository rentalRepository;

    public RentalService(RentalRepository rentalRepository) {
        this.rentalRepository = rentalRepository;
    }

    public List<RentalActiveDTO> getActiveRentals() {
        return rentalRepository.findActiveRentals();
    }

    public List<RentalCompleteDTO> getAllRentals() {
        return rentalRepository.findAllRentals();
    }
}