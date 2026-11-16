package models;
import lombok.Data;
import java.time.LocalDate;

/**
 * Empresa a la que se envían candidaturas. Equivale a una fila de la tabla {@code empresa}.
 * <p>
 * Los getters, los setters y los constructores (uno vacío, para rellenarla desde un formulario,
 * y otro con todos los campos) los genera Lombok.
 * </p>
 */
@Data
public class Empresa {

    private long id;
    private String nombre;
    private String sector;
    /** URL del sitio web (puede estar vacía). */
    private String web;
    /** Fecha en que se registró la empresa. */
    private LocalDate fechaRegistro;

    /**
     * Dos empresas guardadas son la misma si tienen el mismo ID. Así un {@code ComboBox}
     * puede marcar como seleccionada la empresa de una oferta al editarla.
     */
    @Override
    public boolean equals(Object otro) {
        if (this == otro) {
            return true;
        }
        return otro instanceof Empresa empresa && id != 0 && id == empresa.id;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(id);
    }

    /** @return el nombre, que es lo que muestran los desplegables */
    @Override
    public String toString() {
        return nombre;
    }
}
