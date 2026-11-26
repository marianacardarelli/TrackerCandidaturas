# planning.md - Track it v2.0

## Contexto
Aplicación "Tracker de Candidaturas Laborales" de 2º DAM (Acceso a Datos + Desarrollo de
Interfaces). La v1.0 era de consola; la v2.0 añade interfaz gráfica JavaFX, persistencia
con JDBC y base de datos MySQL. Proyecto en pareja (Maria y Mariana). Nivel básico: el
código debe ser sencillo y fácil de explicar.

## Tecnologías
- Java con Maven. JavaFX 21.0.6 (FXML). Lombok. MySQL en Docker.
- La conexión usa las variables de entorno `USER` y `PASSWORD`.

## Arquitectura
`controllers` (+ FXML) → `service` → `data` (DAO con JDBC) → MySQL.
- Paquetes: `controllers`, `service`, `data`, `models`, `utils`.
- `DAO<T>`: `findById(Long)`, `findAll()`, `save`, `update`, `deleteById(Long)`.
- Los servicios reciben un `DAO<T>`, validan los datos y lanzan `IllegalArgumentException`
  con mensajes claros (también al traducir errores de la base de datos).
- Los controladores obtienen los servicios desde `Contexto`.
- La navegación entre pantallas se hace con `NavigateTo`.

## Reglas de interfaz
- Las pantallas se diseñan en Scene Builder (FXML en `src/main/resources/views/`).
  NO se construye la interfaz por código.
- Un único `src/main/resources/css/style.css` enlazado desde todos los FXML.
- Estilo claymorfismo lila del Figma (paleta #6856D1 / #7564D7).
- Textos de ayuda en Prompt Text; `fx:id` con prefijos `txt`, `cmb`, `tbl`, `col`, `btn`, `lbl`.
- Formularios en ventana modal; los errores se muestran en `lblError`.

## Reglas de código
- Javadoc en la clase y en los métodos (no en los atributos).
- Sin imports sin usar ni código de prueba (`printStackTrace`, etc.).
- Fechas con el formato `dd/MM/uuuu`.
- Una empresa con ofertas no se puede borrar; cada entrevista pertenece a una oferta.

## Plan de trabajo
1. Capa de datos: tablas y DAO JDBC (empresa, oferta, entrevista).
2. Servicios con validaciones.
3. Pantallas: listado y formulario de empresas, entrevistas y ofertas.
4. Ofertas: filtros combinados, cambio de estado, importar CSV con transacción.
5. Navegación entre pantallas y detalle de candidatura.
6. Estilos (`style.css`).
7. Documentación: README, tasks.md y vídeo. Tag `v2.0`.

## Reparto
- Maria: empresas, entrevistas, estilos.
- Mariana: ofertas, filtros, importar CSV, contexto, navegación y detalle.

## Flujo de Git
Ramas `main`, `MariaAguilar` y `mariana`. Se integra en `main` mediante pull request.
Mínimo 5 commits sustanciales por persona.