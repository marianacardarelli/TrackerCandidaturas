package datos;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa de datos: lee y escribe las candidaturas en un archivo de texto
 * con los campos separados por punto y coma. Es la única clase que toca el archivo.
 */
public class OfertaRepositorio {

    private static final String SEPARADOR = ";";
    private static final String CABECERA = "id;titulo;empresa;estado;fechaPostulacion;enlace;notas";
    private static final int NUMERO_CAMPOS = 7;

    private final Path ruta;

    /**
     * Crea el repositorio sobre un archivo.
     *
     * @param ruta ruta del archivo de datos, por ejemplo "data/candidaturas.csv"
     */
    public OfertaRepositorio(String ruta) {
        this.ruta = Path.of(ruta);
    }

    /**
     * Lee todas las candidaturas del archivo de datos.
     * Si el archivo todavía no existe devuelve una lista vacía.
     *
     * @return lista de candidaturas
     * @throws IOException si no se puede leer el archivo
     */

}
