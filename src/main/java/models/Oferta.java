package models;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.List;

/**
 * Candidatura a una oferta de empleo. Equivale a una fila de la tabla {@code oferta}.
 * <p>
 * Los getters, los setters y los constructores (uno vacío, para rellenarla desde un formulario,
 * y otro con todos los campos, en el orden en que están declarados) los genera Lombok.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Oferta {

    /** Formato de fecha mostrado y aceptado en la aplicación: dd/MM/yyyy. */
    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** Estados válidos de una candidatura. */
    public static final List<String> ESTADOS = List.of("pendiente", "entrevista", "rechazado", "aceptado");

    /** Identificador único (0 si todavía no está guardada). */
    private long id;

    /** Empresa a la que se postula. */
    private Empresa empresa;

    /** Puesto al que se postula. */
    private String titulo;

    /** Descripción de la oferta (puede estar vacía). */
    private String descripcion;

    /** Estado de la candidatura: uno de {@link #ESTADOS}. */
    private String estado;

    /** Fecha en que se envió la candidatura. */
    private LocalDate fechaPostulacion;

    /** URL de la oferta (puede estar vacía). */
    private String enlace;

    /** Salario ofrecido, o {@code null} si no se conoce. */
    private Double salario;

    /** Notas personales (pueden estar vacías). */
    private String notas;

    /**
     * Atajo para las tablas, que no pueden leer {@code empresa.nombre} directamente.
     *
     * @return nombre de la empresa, o texto vacío si no tiene
     */
    public String getNombreEmpresa() {
        return empresa == null ? "" : empresa.getNombre();
    }

    @Override
    public String toString() {
        String fecha = fechaPostulacion == null ? "" : fechaPostulacion.format(FORMATO_FECHA);
        return id + " | " + titulo + " | " + getNombreEmpresa() + " | " + estado + " | " + fecha;
    }
}