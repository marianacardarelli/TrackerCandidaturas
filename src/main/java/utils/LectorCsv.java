package utils;

import models.Empresa;
import models.Oferta;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee un archivo CSV de candidaturas y lo convierte en objetos {@link Oferta}.
 */
public final class LectorCsv {

    /** Separador de columnas del CSV. */
    private static final String SEPARADOR = ";";
    /** Número de columnas que debe tener cada línea. */
    private static final int COLUMNAS = 7;

    /** No se instancia: solo tiene métodos estáticos. */
    private LectorCsv() {
    }

    /**
     * Lee las candidaturas de un CSV que está en resources.
     * Formato de cada línea: id;titulo;empresa;estado;fechaPostulacion;enlace;notas
     *
     * @param ruta ruta del archivo dentro de resources, por ejemplo "/candidaturas.csv"
     * @return ofertas leídas, con la empresa solo con su nombre
     * @throws IllegalArgumentException si no existe el archivo o alguna línea tiene un formato incorrecto
     * @throws UncheckedIOException si no se puede leer el archivo
     */
    public static List<Oferta> leerOfertas(String ruta) {
        InputStream entrada = LectorCsv.class.getResourceAsStream(ruta);
        if (entrada == null) {
            throw new IllegalArgumentException("No se encuentra el archivo " + ruta);
        }
        List<Oferta> ofertas = new ArrayList<>();
        try (BufferedReader lector = new BufferedReader(new InputStreamReader(entrada, StandardCharsets.UTF_8))) {
            String linea;
            int numeroLinea = 0;
            while ((linea = lector.readLine()) != null) {
                numeroLinea++;
                if (linea.isBlank() || linea.startsWith("id" + SEPARADOR)) {
                    continue;
                }
                ofertas.add(convertir(linea, numeroLinea));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return ofertas;
    }

    /**
     * Convierte una línea del CSV en una {@link Oferta}. El id de la línea se ignora.
     *
     * @param linea       línea del archivo
     * @param numeroLinea número de la línea, para indicarlo en los errores
     * @return la oferta leída, con la empresa solo con su nombre
     * @throws IllegalArgumentException si no tiene {@link #COLUMNAS} columnas o la fecha no es válida
     */
    private static Oferta convertir(String linea, int numeroLinea) {
        String[] columnas = linea.split(SEPARADOR, -1);
        if (columnas.length != COLUMNAS) {
            throw new IllegalArgumentException("Línea " + numeroLinea + ": se esperaban "
                    + COLUMNAS + " columnas y hay " + columnas.length + ".");
        }
        Empresa empresa = new Empresa();
        empresa.setNombre(columnas[2].trim());

        Oferta oferta = new Oferta();
        oferta.setTitulo(columnas[1].trim());
        oferta.setEmpresa(empresa);
        oferta.setEstado(columnas[3].trim());
        try {
            oferta.setFechaPostulacion(LocalDate.parse(columnas[4].trim(), Oferta.FORMATO_FECHA));
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Línea " + numeroLinea + ": la fecha '"
                    + columnas[4] + "' no es válida (dd/MM/yyyy).");
        }
        oferta.setEnlace(columnas[5].trim());
        oferta.setNotas(columnas[6].trim());
        return oferta;
    }
}
