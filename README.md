# Track it - Tracker de Candidaturas Laborales (v2.0)

Aplicación para llevar el seguimiento de las candidaturas de trabajo: empresas,
ofertas y entrevistas. La v1.0 funcionaba por consola; en la v2.0 tiene interfaz
gráfica JavaFX y guarda los datos en una base de datos MySQL con JDBC.

Proyecto de 2º DAM (CESUR), de Maria Aguilar y Mariana Cardarelli.

## Tecnologías

- Java 25 y Maven
- JavaFX 21.0.6 (pantallas FXML hechas en Scene Builder)
- JDBC y MySQL (en Docker) con MySQL Connector/J 26.10.0
- Lombok 1.18.48

## Funcionalidades

- Candidaturas: listado con filtros combinados (empresa, estado, salario mínimo y
  fecha desde), alta, edición, borrado, cambio de estado desde el formulario,
  detalle con sus entrevistas e importación desde CSV con transacción.
- Empresas: listado con buscador por nombre, alta y edición con validación, y
  borrado (no se puede borrar una empresa que tenga ofertas).
- Entrevistas: listado con filtro por resultado, alta y edición, y borrado. Cada
  entrevista pertenece a una oferta (candidatura).
- Barra de navegación para moverse entre Candidaturas, Empresas y Entrevistas.

## Arquitectura

Controladores (JavaFX y FXML) → servicios → DAO (JDBC) → MySQL.

Paquetes: `controllers`, `service`, `data`, `models` y `utils`. Las clases de
arranque (`Main` y `AppTracker`) están en el paquete por defecto. Los servicios se
obtienen desde `Contexto` y la navegación entre pantallas se hace con `NavigateTo`.

Los FXML están en `src/main/resources/views/` (listado, formulario y detalle de
candidaturas; listado y formulario de empresas; listado y formulario de
entrevistas) y los estilos en `src/main/resources/css/style.css`, enlazado desde
todas las pantallas.

## Cómo ejecutarlo

1. Tener un contenedor de MySQL en Docker arrancado en el puerto, con la
   base de datos `ad`. El puerto y el nombre de la base de datos están en
   `DataSourceFactory`.
2. Definir las variables de entorno `USER` y `PASSWORD` con las credenciales de
   MySQL en la configuración de ejecución de `Main` (son obligatorias).
3. Ejecutar la clase `Main` con `mvn javafx:run` (o desde el IDE). Al arrancar se
   crean las tablas con `schema.sql` y se cargan los datos de ejemplo de `data.sql`
   solo si la tabla `Empresa` está vacía. La primera pantalla es el listado de
   candidaturas.

## Contribuciones

| Persona | Trabajo realizado |
|---|---|
| Maria | Empresas (servicio, listado y formulario), entrevistas (servicio, listado y formulario), estilos (`style.css`) y barra de navegación en sus pantallas |
| Mariana | Ofertas, filtros combinados, cambio de estado, importación de CSV con transacción, clase `Contexto`, navegación y detalle de candidatura |
| Ambas | README, `planning.md`, `tasks.md`, vídeo y tag `v2.0` |

## Versiones

- v1.0: versión de consola.
- v2.0: interfaz JavaFX, JDBC y MySQL (tag `v2.0`).
