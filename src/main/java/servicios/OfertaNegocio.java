package servicios;
import datos.Oferta;
import datos.OfertaRepositorio;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

    /**
     * Capa de negocio: aplica las reglas de la aplicación (filtrar, validar,
     * calcular el ID nuevo y buscar) usando el repositorio para leer y guardar.
     */
    public class OfertaNegocio {

        private static final List<String> ESTADOS = List.of("pendiente", "entrevista", "rechazado", "aceptado");

        private final OfertaRepositorio repositorio;

        /**
         * Crea la capa de negocio con el repositorio que va a utilizar.
         *
         * @param repositorio capa de datos
         */
        public OfertaNegocio(OfertaRepositorio repositorio) {
            this.repositorio = repositorio;
        }

        /**
         * Devuelve las candidaturas, filtradas por estado y/o empresa si se indican.
         *
         * @param estado  estado a buscar o null para no filtrar
         * @param empresa empresa a buscar o null para no filtrar
         * @return candidaturas que cumplen los filtros
         * @throws IOException              si no se puede leer el archivo
         * @throws IllegalArgumentException si el estado no es válido
         */
        public List<Oferta> listar(String estado, String empresa) throws IOException {
            if (estado != null) {
                validarEstado(estado);
            }
            List<Oferta> resultado = new ArrayList<>();
            for (Oferta oferta : repositorio.leerTodas()) {
                boolean coincideEstado = estado == null || oferta.getEstado().equalsIgnoreCase(estado);
                boolean coincideEmpresa = empresa == null || oferta.getEmpresa().equalsIgnoreCase(empresa);
                if (coincideEstado && coincideEmpresa) {
                    resultado.add(oferta);
                }
            }
            return resultado;
        }

        /**
         * Añade una candidatura nueva con el siguiente ID libre.
         *
         * @param titulo  puesto al que se postula
         * @param empresa nombre de la empresa
         * @param estado  pendiente, entrevista, rechazado o aceptado
         * @param fecha   fecha de postulación en formato dd/MM/yyyy
         * @throws IOException              si no se puede leer o escribir el archivo
         * @throws IllegalArgumentException si el estado o la fecha no son válidos
         */
        public void añadir(String titulo, String empresa, String estado, String fecha) throws IOException {
            validarEstado(estado);
            LocalDate fechaPostulacion = convertirFecha(fecha);
            List<Oferta> ofertas = repositorio.leerTodas();
            ofertas.add(new Oferta(siguienteId(ofertas), titulo, empresa, estado.toLowerCase(),
                    fechaPostulacion, "", ""));
            repositorio.escribirTodas(ofertas);
        }

        /**
         * Busca una candidatura por su ID.
         *
         * @param id identificador de la candidatura
         * @return la candidatura o null si no existe
         * @throws IOException si no se puede leer el archivo
         */
        public Oferta ver(long id) throws IOException {
            for (Oferta oferta : repositorio.leerTodas()) {
                if (oferta.getId() == id) {
                    return oferta;
                }
            }
            return null;
        }

        /**
         * Elimina una candidatura por su ID.
         *
         * @param id identificador de la candidatura
         * @return true si se eliminó, false si no existe
         * @throws IOException si no se puede leer o escribir el archivo
         */
        public boolean borrar(long id) throws IOException {
            List<Oferta> ofertas = repositorio.leerTodas();
            boolean borrada = ofertas.removeIf(oferta -> oferta.getId() == id);
            if (borrada) {
                repositorio.escribirTodas(ofertas);
            }
            return borrada;
        }

        /**
         * Importa las candidaturas de otro CSV y las suma a las existentes.
         * Las importadas reciben IDs nuevos para no repetir los que ya hay.
         *
         * @param ruta ruta del archivo CSV a importar
         * @throws IOException              si el archivo no existe o no se puede leer o escribir
         * @throws IllegalArgumentException si alguna línea del CSV no es válida
         */
        public void importar(String ruta) throws IOException {
            List<Oferta> importadas = repositorio.leerDesde(ruta);
            List<Oferta> ofertas = repositorio.leerTodas();
            long siguiente = siguienteId(ofertas);
            for (Oferta importada : importadas) {
                ofertas.add(new Oferta(siguiente++, importada.getTitulo(), importada.getEmpresa(),
                        importada.getEstado(), importada.getFechaPostulacion(),
                        importada.getEnlace(), importada.getNotas()));
            }
            repositorio.escribirTodas(ofertas);
        }

        private long siguienteId(List<Oferta> ofertas) {
            long maximo = 0;
            for (Oferta oferta : ofertas) {
                maximo = Math.max(maximo, oferta.getId());
            }
            return maximo + 1;
        }

        private void validarEstado(String estado) {
            if (!ESTADOS.contains(estado.toLowerCase())) {
                throw new IllegalArgumentException("Estado no válido: " + estado
                        + ". Usa: " + String.join(", ", ESTADOS));
            }
        }

        private LocalDate convertirFecha(String fecha) {
            try {
                return LocalDate.parse(fecha, Oferta.FORMATO_FECHA);
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Fecha no válida: " + fecha + ". Usa el formato dd/MM/yyyy");
            }
        }
    }
