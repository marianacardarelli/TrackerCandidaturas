# tasks.md - Track it v2.0

## 1. Base de datos y capa de datos
- Tablas empresa, oferta y entrevista en MySQL (Docker) (Mariana)
- Interfaz `DAO<T>` y DAO JDBC de empresa, oferta y entrevista (Mariana)
- Clase `Contexto` con los servicios (Mariana)

## 2. Servicios y validaciones
- `EmpresaService`: nombre y sector obligatorios, nombre duplicado y empresa con ofertas no borrable (Maria)
- `EntrevistaService`: oferta, fecha, tipo y resultado válidos (Maria)
- `OfertaService` (Mariana)

## 3. Pantallas de empresas (Maria)
- Listado con buscador por nombre
- Formulario de alta y edición con validación
- Borrado con confirmación y aviso si la empresa tiene ofertas

## 4. Pantallas de entrevistas (Maria)
- Listado con filtro por resultado
- Formulario de alta y edición con desplegables
- Borrado con confirmación

## 5. Pantallas de candidaturas (Mariana)
- Listado de candidaturas
- Formulario de alta y edición
- Detalle de candidatura con tabla de entrevistas
- Filtros combinados
- Cambio de estado
- Importar CSV con transacción

## 6. Navegación y estilos
- Barra de navegación entre Candidaturas, Empresas y Entrevistas (Mariana y Maria)
- `style.css` compartido por todas las pantallas (Maria)

## 7. Documentación y entrega
- planning.md
- tasks.md
- README v2.0
- Vídeo
- Tag v2.0