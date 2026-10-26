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

    public List<Oferta> leerDesde(String rutaArchivo) throws IOException {
        return leer(Path.of(rutaArchivo));
    }

    /**
     * Reescribe el archivo de datos con la lista indicada.
     *
     * @param ofertas candidaturas a guardar
     * @throws IOException si no se puede escribir el archivo
     */
    public void escribirTodas(List<Oferta> ofertas) throws IOException {
        List<String> lineas = new ArrayList<>();
        lineas.add(CABECERA);
        for (Oferta oferta : ofertas) {
            lineas.add(convertirALinea(oferta));
        }
        Path carpeta = ruta.getParent();
        if (carpeta != null) {
            Files.createDirectories(carpeta);
        }
        Files.write(ruta, lineas);
    }

    private List<Oferta> leer(Path archivo) throws IOException {
        List<String> lineas = Files.readAllLines(archivo);
        List<Oferta> ofertas = new ArrayList<>();
        for (int i = 1; i < lineas.size(); i++) {
            String linea = lineas.get(i);
            if (!linea.isBlank()) {
                ofertas.add(convertirAOferta(linea));
            }
        }
        return ofertas;
    }

    private String convertirALinea(Oferta oferta) {
        return String.join(SEPARADOR,
                String.valueOf(oferta.getId()),
                oferta.getTitulo(),
                oferta.getEmpresa(),
                oferta.getEstado(),
                oferta.getFechaPostulacion().format(Oferta.FORMATO_FECHA),
                oferta.getEnlace(),
                oferta.getNotas());
    }

    private Oferta convertirAOferta(String linea) {
        String[] campos = linea.split(SEPARADOR, NUMERO_CAMPOS);
        if (campos.length < NUMERO_CAMPOS) {
            throw new IllegalArgumentException("Línea no válida en el archivo: " + linea);
        }
        try {
            return new Oferta(Long.parseLong(campos[0].trim()), campos[1], campos[2], campos[3],
                    LocalDate.parse(campos[4].trim(), Oferta.FORMATO_FECHA), campos[5], campos[6]);
        } catch (NumberFormatException | DateTimeParseException e) {
            throw new IllegalArgumentException("Línea no válida en el archivo: " + linea);
        }
    }
}
