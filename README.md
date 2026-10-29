# Tracker de Candidaturas Laborales

Aplicación Java para gestionar candidaturas laborales desde la línea de comandos. Proyecto de Maria y Mariana.

**Versión actual: v1.0** (archivos de texto + CLI por argumentos + diseño en Figma).

## Tecnologías utilizadas

| Tecnología                  | Uso                                      |
|-----------------------------|------------------------------------------|
| Java 25                     | Lenguaje                                 |
| Maven                       | Gestión del proyecto y compilación       |
| Archivo CSV (separador `;`) | Persistencia en v1.0, sin base de datos  |
| Figma                       | Prototipo interactivo y Manual de estilo |

## Arquitectura

Tres capas. Cada capa solo llama a la de debajo.

| Capa         | Paquete                    | Clases                        |
|--------------|----------------------------|-------------------------------|
| Presentación | `com.tracker.presentacion` | `ComandosTerminal`            |
| Negocio      | `com.tracker.negocio`      | `OfertaNegocio`               |
| Datos        | `com.tracker.datos`        | `Oferta`, `OfertaRepositorio` |

`Main` solo arranca el programa.

## Compilar y ejecutar (v1.0)

Desde la carpeta del proyecto:

```bash
mvn clean package
java -cp target/classes com.tracker.Main -listar
```

Los datos se guardan en `ofertas.csv`. Cada ejecución hace una operación y termina (no hay menú interactivo).

## Comandos

| Comando                                                                             | Qué hace                                         |
|-------------------------------------------------------------------------------------|--------------------------------------------------|
| `-listar`                                                                           | Lista todas las candidaturas                     |
| `-listar --estado pendiente --empresa Indra`                                        | Filtra por estado y/o empresa                    |
| `-añadir --titulo "Dev Java" --empresa Indra --estado pendiente --fecha 15/09/2026` | Añade una candidatura                            |
| `-ver 13`                                                                           | Muestra los detalles de la candidatura con ID 13 |
| `-borrar 13`                                                                        | Elimina la candidatura con ID 13                 |
| `-importar candidaturas.csv`                                                        | Importa candidaturas desde otro CSV              |

Estados posibles: `pendiente`, `entrevista`, `rechazado`, `aceptado`. Fechas en formato `dd/MM/yyyy`.

Si falta un argumento, el archivo no existe o el ID no existe, el programa muestra un mensaje de error claro.

## Formato del archivo

Una candidatura por línea, con los campos separados por `;`:

```
id;titulo;empresa;estado;fechaPostulacion;enlace;notas
1;Desarrollador Java;Indra;pendiente;15/09/2026;https://...;Contacto por LinkedIn
```

## Datos de prueba

CSV de 15 candidaturas generado con IA a partir del prompt

## Diseño

- Prototipo de Figma (solo lectura): https://www.figma.com/design/o6q6eRuSD1RB9p3rkg479i/Sin-t%C3%ADtulo?node-id=0-1&t=B3MGt85trtYnlHAV-1
- Manual de estilo y assets de diseño
