package presentacion;

import datos.Oferta;
import servicios.OfertaNegocio;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

/**
 * Capa de presentación: lee los argumentos de la línea de comandos,
 * llama a la capa de negocio e imprime los resultados y los errores.
 * Los resultados se imprimen por consola y los errores se registran con un logger.
 */
public class ComandosTerminal {

    private static final Logger LOGGER;

    static {
        System.setProperty("java.util.logging.SimpleFormatter.format", "%4$s: %5$s%n");
        LOGGER = Logger.getLogger(ComandosTerminal.class.getName());
    }

    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final OfertaNegocio negocio;

    /**
     * Crea la consola con la capa de negocio que va a utilizar.
     *
     * @param negocio capa de negocio
     */
    public ComandosTerminal(OfertaNegocio negocio) {
        this.negocio = negocio;
    }

    /**
     * Ejecuta la operación indicada en los argumentos y termina.
     * Captura los errores y los muestra con un mensaje claro.
     *
     * @param args argumentos recibidos en el main, por ejemplo {"-ver", "13"}
     */
    public void ejecutar(String[] args) {
        if (args.length == 0) {
            mostrarError("Falta la operación. Usa: -listar, -añadir, -ver, -borrar o -importar");
            return;
        }
        try {
            switch (args[0]) {
                case "-listar" -> listar(args);
                case "-añadir" -> añadir(args);
                case "-ver" -> ver(args);
                case "-borrar" -> borrar(args);
                case "-importar" -> importar(args);
                default -> mostrarError("Operación no válida: " + args[0]);
            }
        } catch (NumberFormatException e) {
            mostrarError("El ID debe ser un número. Ejemplo: -ver 13");
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
        }
    }

    private void listar(String[] args) {
        String estado = obtenerOpcion(args, "--estado");
        String empresa = obtenerOpcion(args, "--empresa");
        try {
            mostrarLista(negocio.listar(estado, empresa));
        } catch (IOException e) {
            mostrarError("No se puede leer el archivo de candidaturas: " + e.getMessage());
        }
    }

    private void añadir(String[] args) {
        String titulo = obtenerOpcion(args, "--titulo");
        String empresa = obtenerOpcion(args, "--empresa");
        String estado = obtenerOpcion(args, "--estado");
        String fecha = obtenerOpcion(args, "--fecha");
        if (titulo == null || empresa == null || estado == null || fecha == null) {
            mostrarError("Faltan argumentos. Ejemplo: -añadir --titulo \"Dev Java\" --empresa Indra "
                    + "--estado pendiente --fecha 15/09/2026");
            return;
        }
        try {
            negocio.añadir(titulo, empresa, estado, fecha);
            System.out.println("Candidatura añadida.");
        } catch (IOException e) {
            mostrarError("No se puede leer o escribir el archivo: " + e.getMessage());
        }
    }

    private void ver(String[] args) {
        if (args.length < 2) {
            mostrarError("Falta el ID. Ejemplo: -ver 13");
            return;
        }
        long id = Long.parseLong(args[1]);
        try {
            Oferta oferta = negocio.ver(id);
            if (oferta == null) {
                mostrarError("No existe ninguna candidatura con ID " + id);
                return;
            }
            mostrarDetalle(oferta);
        } catch (IOException e) {
            mostrarError("No se puede leer el archivo de candidaturas: " + e.getMessage());
        }
    }

    private void borrar(String[] args) {
        if (args.length < 2) {
            mostrarError("Falta el ID. Ejemplo: -borrar 13");
            return;
        }
        long id = Long.parseLong(args[1]);
        try {
            if (negocio.borrar(id)) {
                System.out.println("Candidatura " + id + " eliminada.");
            } else {
                mostrarError("No existe ninguna candidatura con ID " + id);
            }
        } catch (IOException e) {
            mostrarError("No se puede leer o escribir el archivo: " + e.getMessage());
        }
    }

    private void importar(String[] args) {
        if (args.length < 2) {
            mostrarError("Falta el archivo. Ejemplo: -importar candidaturas.csv");
            return;
        }
        try {
            negocio.importar(args[1]);
            System.out.println("Candidaturas importadas desde " + args[1]);
        } catch (IOException e) {
            mostrarError("No se encuentra o no se puede leer el archivo: " + e.getMessage());
        }
    }

    /**
     * Imprime una lista de candidaturas, una por línea.
     *
     * @param ofertas candidaturas a mostrar
     */
    public void mostrarLista(List<Oferta> ofertas) {
        if (ofertas.isEmpty()) {
            System.out.println("No hay candidaturas.");
            return;
        }
        for (Oferta oferta : ofertas) {
            System.out.println(oferta.getId() + " | " + oferta.getTitulo() + " | " + oferta.getEmpresa()
                    + " | " + oferta.getEstado() + " | " + oferta.getFechaPostulacion().format(FORMATO_FECHA));
        }
    }

    /**
     * Imprime todos los datos de una candidatura.
     *
     * @param oferta candidatura a mostrar
     */
    public void mostrarDetalle(Oferta oferta) {
        System.out.println("ID: " + oferta.getId());
        System.out.println("Título: " + oferta.getTitulo());
        System.out.println("Empresa: " + oferta.getEmpresa());
        System.out.println("Estado: " + oferta.getEstado());
        System.out.println("Fecha de postulación: " + oferta.getFechaPostulacion().format(FORMATO_FECHA));
        System.out.println("Enlace: " + oferta.getEnlace());
        System.out.println("Notas: " + oferta.getNotas());
    }

    /**
     * Registra un mensaje de error con el logger.
     *
     * @param mensaje texto del error
     */
    public void mostrarError(String mensaje) {
        LOGGER.severe(mensaje);
    }

    /**
     * Busca una opción en los argumentos y devuelve el valor que va después.
     *
     * @param args  argumentos recibidos
     * @param clave opción a buscar, por ejemplo "--estado"
     * @return el valor de la opción o null si no aparece
     */
    private String obtenerOpcion(String[] args, String clave) {
        for (int i = 1; i < args.length - 1; i++) {
            if (args[i].equals(clave)) {
                return args[i + 1];
            }
        }
        return null;
    }
}
