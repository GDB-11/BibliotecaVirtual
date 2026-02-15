package com.biblioteca.app.dto.rental;

/**
 * DTO para el reporte de libros más pedidos con información de tendencias
 */
public class BookMostRequestedDTO {
    
    private String bookId;
    private String title;
    private String isbn;
    private String authorName;
    private String categoryName;
    private Integer totalRentals;
    private Integer yesterdayRentals;
    private Integer todayRentals;
    private Double trendPercentage;
    private String trendIcon;
    private String trendClass;
    private Double popularityPercentage;

    // Constructor vacío
    public BookMostRequestedDTO() {
    }

    // Constructor completo
    public BookMostRequestedDTO(String bookId, String title, String isbn, String authorName, 
                                String categoryName, Integer totalRentals, Integer yesterdayRentals, 
                                Integer todayRentals) {
        this.bookId = bookId;
        this.title = title;
        this.isbn = isbn;
        this.authorName = authorName;
        this.categoryName = categoryName;
        this.totalRentals = totalRentals != null ? totalRentals : 0;
        this.yesterdayRentals = yesterdayRentals != null ? yesterdayRentals : 0;
        this.todayRentals = todayRentals != null ? todayRentals : 0;
        
        // Calcular tendencia
        calculateTrend();
    }

    /**
     * Calcula el porcentaje de tendencia y asigna íconos/clases CSS
     */
    private void calculateTrend() {
        if (yesterdayRentals == 0 && todayRentals == 0) {
            this.trendPercentage = 0.0;
            this.trendIcon = "bi-dash-circle";
            this.trendClass = "text-muted";
        } else if (yesterdayRentals == 0 && todayRentals > 0) {
            this.trendPercentage = 100.0;
            this.trendIcon = "bi-arrow-up-circle-fill";
            this.trendClass = "text-success";
        } else {
            double change = ((double) (todayRentals - yesterdayRentals) / yesterdayRentals) * 100;
            this.trendPercentage = Math.round(change * 10.0) / 10.0;
            
            if (change > 0) {
                this.trendIcon = "bi-arrow-up-circle-fill";
                this.trendClass = "text-success";
            } else if (change < 0) {
                this.trendIcon = "bi-arrow-down-circle-fill";
                this.trendClass = "text-danger";
            } else {
                this.trendIcon = "bi-dash-circle";
                this.trendClass = "text-muted";
            }
        }
    }

    /**
     * Establece el porcentaje de popularidad basado en el total máximo
     */
    public void setPopularityPercentage(Integer maxRentals) {
        if (maxRentals == null || maxRentals == 0) {
            this.popularityPercentage = 0.0;
        } else {
            this.popularityPercentage = Math.round(((double) totalRentals / maxRentals) * 100.0 * 10.0) / 10.0;
        }
    }

    // Getters y Setters
    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Integer getTotalRentals() {
        return totalRentals;
    }

    public void setTotalRentals(Integer totalRentals) {
        this.totalRentals = totalRentals;
    }

    public Integer getYesterdayRentals() {
        return yesterdayRentals;
    }

    public void setYesterdayRentals(Integer yesterdayRentals) {
        this.yesterdayRentals = yesterdayRentals;
    }

    public Integer getTodayRentals() {
        return todayRentals;
    }

    public void setTodayRentals(Integer todayRentals) {
        this.todayRentals = todayRentals;
    }

    public Double getTrendPercentage() {
        return trendPercentage;
    }

    public void setTrendPercentage(Double trendPercentage) {
        this.trendPercentage = trendPercentage;
    }

    public String getTrendIcon() {
        return trendIcon;
    }

    public void setTrendIcon(String trendIcon) {
        this.trendIcon = trendIcon;
    }

    public String getTrendClass() {
        return trendClass;
    }

    public void setTrendClass(String trendClass) {
        this.trendClass = trendClass;
    }

    public Double getPopularityPercentage() {
        return popularityPercentage;
    }

    public void setPopularityPercentage(Double popularityPercentage) {
        this.popularityPercentage = popularityPercentage;
    }

    @Override
    public String toString() {
        return "BookMostRequestedDTO{" +
                "bookId='" + bookId + '\'' +
                ", title='" + title + '\'' +
                ", totalRentals=" + totalRentals +
                ", trendPercentage=" + trendPercentage +
                '}';
    }
}