# 📚 Biblioteca Virtual - Sistema de Gestión

Sistema backend para gestión de biblioteca desarrollado en Spring Boot con MySQL.

##  Características

- ✅ Gestión de autores, libros y categorías
- ✅ Sistema de alquileres con control de fechas
- ✅ Reportes completos de actividad
- ✅ API REST documentada

##  Endpoints Disponibles

### Autores
- `GET /api/authors/active` - Lista autores activos con información de país

### Libros
- `GET /api/books/active` - Lista libros activos con autor y categoría

### Alquileres
- `GET /api/rentals/active` - Alquileres activos y por vencer
- `GET /api/rentals/all` - Historial completo de alquileres

##  Tecnologías

- **Java 17+**
- **Spring Boot 4.0.2**
- **Spring Data JPA**
- **MySQL 8.0**
- **Maven**

##  Configuración

1. Clonar el repositorio
2. Configurar base de datos en `application.properties`
3. Ejecutar: `mvn spring-boot:run`
4. Acceder a: `http://localhost:8080`

##  Estructura del Proyecto

#  Base de Datos

El proyecto utiliza las siguientes tablas principales:
- `Author`, `Book`, `BookCopy`, `Category`
- `Rental`, `RentalStatus`, `User`
- `Country`, `Status`, `BookStatus`

##  Autor

src/main/java/com/biblioteca/app/
├── controller/ # Endpoints REST
├── dto/ # Objetos de transferencia
├── entity/ # Entidades JPA
├── repository/ # Acceso a datos
└── service/ # Lógica de negocio

Desarrollado como proyecto académico para LP2.
