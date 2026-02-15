package com.biblioteca.app.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.biblioteca.app.dto.RentalActiveDTO;
import com.biblioteca.app.dto.RentalCompleteDTO;
import com.biblioteca.app.service.RentalService;

@RestController
@RequestMapping("/api/rentals")
public class RentalController {

    private final RentalService rentalService;

    public RentalController(RentalService rentalService) {
        this.rentalService = rentalService;
    }

    @GetMapping("/active")
    public List<RentalActiveDTO> getActiveRentals() {
        return rentalService.getActiveRentals();
    }

    @GetMapping("/all")
    public List<RentalCompleteDTO> getAllRentals() {
        return rentalService.getAllRentals();
    }
}