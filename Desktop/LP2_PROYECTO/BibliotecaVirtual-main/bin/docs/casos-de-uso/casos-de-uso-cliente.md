Casos De Uso – Biblioteca Virtual (parte Del Cliente Y Transaccional)
Sistema: Biblioteca Virtual
Autor: Isac Fuentes Calixto
Rol en el proyecto: Consultas (Cliente) y Proceso Transaccional
Repositorio: BibliotecaVirtual
________________________________________
Caso de Uso 1: Consulta de Libros
Identificador
CU-01
Actor Principal
Cliente
Descripción
Permite al cliente consultar el catálogo de libros disponibles en la biblioteca, visualizando información general como título, autor, categoría, año de publicación y disponibilidad.
Precondiciones
•	El cliente ha iniciado sesión correctamente.
•	Existen libros registrados en el sistema.
Postcondiciones
•	El sistema muestra la lista de libros según el criterio de búsqueda.
Flujo Principal
1.	El cliente ingresa al módulo de consulta de libros.
2.	El sistema muestra el listado general de libros disponibles.
3.	El cliente puede buscar libros por título, autor o categoría.
4.	El sistema filtra y muestra los resultados.
5.	El cliente selecciona un libro para ver su información detallada.
Flujos Alternativos
4a. Si no existen libros que coincidan con el criterio de búsqueda, el sistema muestra un mensaje indicando que no se encontraron resultados.
Reglas de Negocio
•	Solo se muestran libros con estado activo.
•	La consulta no permite modificar información.
Tablas Relacionadas
•	Book
•	Author
•	Category
•	BookStatus
________________________________________
Caso de Uso 2: Consulta de Autores
Identificador
CU-02
Actor Principal
Cliente
Descripción
Permite al cliente consultar información de los autores registrados en el sistema.
Precondiciones
•	El cliente ha iniciado sesión correctamente.
•	Existen autores registrados.
Postcondiciones
•	El sistema muestra la información del autor seleccionado.
Flujo Principal
1.	El cliente accede al módulo de consulta de autores.
2.	El sistema muestra la lista de autores.
3.	El cliente realiza una búsqueda por nombre.
4.	El sistema muestra los autores que coinciden con la búsqueda.
5.	El cliente selecciona un autor para ver su información.
Flujos Alternativos
4a. Si no existen autores que coincidan, el sistema muestra un mensaje informativo.
Reglas de Negocio
•	Solo se muestran autores activos.
Tablas Relacionadas
•	Author
•	Country
•	Status
________________________________________
Caso de Uso 3: Proceso de Alquiler de Libro
Identificador
CU-03
Actor Principal
Cliente
Actores Secundarios
Sistema
Descripción
Permite al cliente realizar el alquiler de un libro disponible, registrando el préstamo y actualizando el estado del ejemplar.
Precondiciones
•	El cliente ha iniciado sesión.
•	El cliente se encuentra activo.
•	Existen ejemplares disponibles del libro seleccionado.
Postcondiciones
•	El alquiler queda registrado en el sistema.
•	El estado del ejemplar cambia a "Prestado".
Flujo Principal
1.	El cliente consulta el catálogo de libros.
2.	El cliente selecciona un libro.
3.	El sistema muestra los ejemplares disponibles.
4.	El cliente selecciona un ejemplar.
5.	El cliente confirma el alquiler.
6.	El sistema registra el alquiler.
7.	El sistema actualiza el estado del ejemplar.
8.	El sistema muestra un mensaje de confirmación.
Flujos Alternativos
3a. Si no existen ejemplares disponibles, el sistema muestra un mensaje indicando que el libro no está disponible.
6a. Si ocurre un error durante el registro, el sistema cancela la operación y no realiza cambios.
Reglas de Negocio
•	Un ejemplar solo puede estar en un alquiler activo.
•	El alquiler se realiza mediante una transacción.
•	Solo usuarios con rol Cliente pueden realizar alquileres.
Tablas Relacionadas
•	Rental
•	RentalStatus
•	BookCopy
•	BookCopyStatus
•	User
________________________________________

Diagramas UML – Casos de Uso (Responsabilidad del Cliente)

A continuación se describen los diagramas UML de Casos de Uso que corresponden a las funcionalidades desarrolladas por el Cliente y al proceso transaccional.

Diagrama de Casos de Uso – Cliente

Actores:

Cliente

Casos de Uso:

Consulta de Libros

Consulta de Autores

Proceso de Alquiler

Relaciones:

El actor Cliente interactúa directamente con los tres casos de uso.

El caso de uso Proceso de Alquiler incluye implícitamente la consulta de libros, ya que el cliente debe seleccionar un libro antes de alquilarlo.

Descripción UML (texto guía):

Cliente → Consulta de Libros

Cliente → Consulta de Autores

Cliente → Proceso de Alquiler

Proceso de Alquiler <> Consulta de Libros

Notas para el Diagrama UML

El diagrama debe mostrar un único actor (Cliente).

Los casos de uso deben estar dentro del límite del sistema "Biblioteca Virtual".

No se incluyen casos administrativos, ya que corresponden a otro integrante del equipo.

Observación Final

Estos diagramas UML representan de forma clara las interacciones del Cliente con el sistema y se encuentran alineados con los casos de uso redactados y con el modelo de base de datos del proyecto Biblioteca Virtual.


Observación Final
Estos casos de uso corresponden a la funcionalidad del Cliente y al proceso transaccional del sistema, y se encuentran alineados con el modelo de base de datos implementado en el proyecto Biblioteca Virtual.

	
