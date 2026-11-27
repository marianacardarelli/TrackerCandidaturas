package models;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

/**
 * Entrevista asociada a una oferta. Equivale a una fila de la tabla {@code entrevista}.
 * <p>
 * Los getters, los setters y los constructores (uno vacío, para rellenarla desde un formulario,
 * y otro con todos los campos, en el orden en que están declarados) los genera Lombok.
 * </p>
 */
@Data
public class Entrevista {

    /** Tipos válidos de entrevista. */
    public static final List<String> TIPOS = List.of("telefonica", "tecnica", "presencial", "online");

    /** Resultados válidos de una entrevista. */
    public static final List<String> RESULTADOS = List.of("pendiente", "positivo", "negativo");

    /** Identificador único (0 si todavía no está guardada). */
    private long id;

    /** Oferta a la que pertenece. */
    private Oferta oferta;

    /** Fecha de la entrevista. */
    private LocalDate fecha;

    /** Tipo de entrevista: uno de {@link #TIPOS}. */
    private String tipo;

    /** Notas sobre la entrevista (pueden estar vacías). */
    private String notas;

    /** Resultado de la entrevista: uno de {@link #RESULTADOS}. */
    private String resultado;

    /** @return texto con el formato "fecha | tipo | resultado", que es lo que muestran los desplegables */
    @Override
    public String toString() {
        String fechaTexto = fecha == null ? "" : fecha.format(Oferta.FORMATO_FECHA);
        return fechaTexto + " | " + tipo + " | " + resultado;
    }
}
