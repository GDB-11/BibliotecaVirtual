package com.biblioteca.app.dto;

import java.time.LocalDateTime;

public interface RentalActiveDTO {
    Long getRentalId();
    String getUsuario();
    String getLibro();
    LocalDateTime getFechaAlquiler();
    LocalDateTime getFechaVencimiento();
    Integer getDiasParaVencer();
    String getEstadoAlquiler();
}