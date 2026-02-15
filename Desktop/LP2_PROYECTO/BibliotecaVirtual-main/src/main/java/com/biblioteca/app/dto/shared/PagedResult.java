package com.biblioteca.app.dto.shared;

import java.util.List;

/**
 * DTO genérico para resultados paginados.
 * @param <T> Tipo de elemento en la lista
 */
public class PagedResult<T> {
    private final List<T> items;
    private final int currentPage;
    private final int pageSize;
    private final int totalItems;
    private final int totalPages;

    public PagedResult(List<T> items, int currentPage, int pageSize, int totalItems) {
        this.items = items;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
    }

    public List<T> getItems() {
        return items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public boolean hasPreviousPage() {
        return currentPage > 1;
    }

    public boolean hasNextPage() {
        return currentPage < totalPages;
    }

    public int getPreviousPage() {
        return hasPreviousPage() ? currentPage - 1 : 1;
    }

    public int getNextPage() {
        return hasNextPage() ? currentPage + 1 : totalPages;
    }

    /**
     * Calcula el rango de páginas a mostrar en la paginación.
     * @param maxVisible Número máximo de páginas visibles
     * @return Array con [inicio, fin] del rango
     */
    public int[] getPageRange(int maxVisible) {
        int half = maxVisible / 2;
        int start = Math.max(1, currentPage - half);
        int end = Math.min(totalPages, start + maxVisible - 1);
        
        // Ajustar inicio si estamos cerca del final
        if (end - start + 1 < maxVisible) {
            start = Math.max(1, end - maxVisible + 1);
        }
        
        return new int[] { start, end };
    }

    /**
     * Obtiene el número del primer elemento en la página actual.
     */
    public int getStartItem() {
        return totalItems == 0 ? 0 : (currentPage - 1) * pageSize + 1;
    }

    /**
     * Obtiene el número del último elemento en la página actual.
     */
    public int getEndItem() {
        return Math.min(currentPage * pageSize, totalItems);
    }
}