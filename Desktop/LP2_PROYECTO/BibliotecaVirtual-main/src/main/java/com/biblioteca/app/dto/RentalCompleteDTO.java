package com.biblioteca.app.dto;

import java.time.LocalDateTime;

public interface RentalCompleteDTO {
    Long getRentalId();
    String getUsuario();
    String getLibro();
    LocalDateTime getFechaAlquiler();
    LocalDateTime getFechaVencimiento();
    LocalDateTime getFechaDevolucion();
    Double getCostoTotal();
    String getEstadoAlquiler();
}