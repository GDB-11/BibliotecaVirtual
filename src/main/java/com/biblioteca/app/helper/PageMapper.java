package com.biblioteca.app.helper;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.biblioteca.app.dto.shared.PagedResult;

/**
 * Mapper para convertir entre Page de Spring Data y PagedResult custom.
 * 
 * Este mapper permite usar Page internamente (integrado con Spring Data JPA)
 * mientras se expone PagedResult en la API pública (UI-friendly con base 1).
 */
@Component
public class PageMapper {

    /**
     * Convierte Page<T> de Spring a PagedResult<T> custom.
     * Mantiene los mismos elementos (sin conversión).
     * 
     * IMPORTANTE: Spring Page usa índice base 0, PagedResult usa base 1.
     * 
     * @param springPage Page de Spring Data (base 0)
     * @param requestedPage Número de página solicitado por el usuario (base 1)
     * @return PagedResult con índice base 1
     */
    public static <T> PagedResult<T> toPagedResult(Page<T> springPage, int requestedPage) {
        return new PagedResult<>(
            springPage.getContent(),
            requestedPage,                          // Página actual (base 1)
            springPage.getSize(),                   // Tamaño de página
            (int) springPage.getTotalElements()     // Total de elementos
        );
    }

    /**
     * Convierte Page<S> de Spring a PagedResult<T> custom aplicando una transformación.
     * Útil para convertir entidades a DTOs.
     * 
     * @param springPage Page de Spring Data (base 0)
     * @param requestedPage Número de página solicitado por el usuario (base 1)
     * @param converter Función para convertir cada elemento S a T
     * @return PagedResult con elementos convertidos
     */
    public static <S, T> PagedResult<T> toPagedResult(
            Page<S> springPage, 
            int requestedPage,
            Function<S, T> converter) {
        
        List<T> convertedItems = springPage.getContent()
            .stream()
            .map(converter)
            .collect(Collectors.toList());
        
        return new PagedResult<>(
            convertedItems,
            requestedPage,                          // Página actual (base 1)
            springPage.getSize(),                   // Tamaño de página
            (int) springPage.getTotalElements()     // Total de elementos
        );
    }

    /**
     * Convierte Page<T> a PagedResult<T> asumiendo que la página solicitada
     * es la página actual de Spring + 1 (conversión de base 0 a base 1).
     * 
     * @param springPage Page de Spring Data (base 0)
     * @return PagedResult con índice base 1
     */
    public static <T> PagedResult<T> toPagedResult(Page<T> springPage) {
        return toPagedResult(springPage, springPage.getNumber() + 1);
    }

    /**
     * Convierte Page<S> a PagedResult<T> aplicando transformación,
     * asumiendo que la página solicitada es la página actual de Spring + 1.
     * 
     * @param springPage Page de Spring Data (base 0)
     * @param converter Función para convertir cada elemento S a T
     * @return PagedResult con elementos convertidos
     */
    public static <S, T> PagedResult<T> toPagedResult(
            Page<S> springPage,
            Function<S, T> converter) {
        
        return toPagedResult(springPage, springPage.getNumber() + 1, converter);
    }

    /**
     * Método helper para convertir número de página de base 1 (UI) a base 0 (Spring).
     * 
     * @param pageBase1 Número de página base 1 (del usuario)
     * @return Número de página base 0 (para Spring)
     */
    public static int toSpringPageNumber(int pageBase1) {
        return Math.max(0, pageBase1 - 1);
    }

    /**
     * Método helper para convertir número de página de base 0 (Spring) a base 1 (UI).
     * 
     * @param pageBase0 Número de página base 0 (de Spring)
     * @return Número de página base 1 (para el usuario)
     */
    public static int toUserPageNumber(int pageBase0) {
        return pageBase0 + 1;
    }
}