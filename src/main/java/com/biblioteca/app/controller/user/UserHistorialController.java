package com.biblioteca.app.controller.user;

import java.security.Principal;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.biblioteca.app.entity.Rental;
import com.biblioteca.app.entity.User;
import com.biblioteca.app.service.RentalService;
import com.biblioteca.app.service.UserService;

@Controller
@RequestMapping("/user/historial")
public class UserHistorialController {

    private final RentalService rentalService;
    private final UserService userService;

    public UserHistorialController(RentalService rentalService, UserService userService) {
        this.rentalService = rentalService;
        this.userService = userService;
    }

    @GetMapping
    public String listarHistorial(
            Principal principal,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        User user = userService.getUserByEmail(principal.getName()).orElseThrow();
        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Rental> historial = rentalService.findByUser(user.getUserId(), pageable);

        long totalItems   = historial.getTotalElements();
        long activosCount = rentalService.countActiveByUser(user.getUserId());
        long finalizadosCount = totalItems - activosCount;

        model.addAttribute("alquileres",      historial.getContent());
        model.addAttribute("currentPage",     page);
        model.addAttribute("totalPages",      historial.getTotalPages());
        model.addAttribute("totalItems",      totalItems);
        model.addAttribute("activosCount",    activosCount);
        model.addAttribute("finalizadosCount", finalizadosCount);

        return "user/historial/lista";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable UUID id, Principal principal, Model model) {
        User user = userService.getUserByEmail(principal.getName()).orElseThrow();
        Rental rental = rentalService.findById(id).orElseThrow();

        // Verificar que el alquiler pertenezca al usuario
        if (!rental.getUser().getUserId().equals(user.getUserId())) {
            throw new IllegalArgumentException("No tienes permiso para ver este alquiler");
        }

        model.addAttribute("alquiler", rental);
        return "user/historial/detalle";
    }
}