import datos.OfertaRepositorio;
import presentacion.ComandosTerminal;
import servicios.OfertaNegocio;

/**
 * Punto de entrada del programa. Crea las tres capas, las conecta
 * y ejecuta la operación recibida por línea de comandos.
 */
public class Main {

    private static final String RUTA_DATOS = "ofertas.csv";

    /**
     * Arranca la aplicación.
     *
     * @param args operación y parámetros, por ejemplo {"-listar"}
     */
    public static void main(String[] args) {
        OfertaRepositorio repositorio = new OfertaRepositorio(RUTA_DATOS);
        OfertaNegocio negocio = new OfertaNegocio(repositorio);
        ComandosTerminal comandos = new ComandosTerminal(negocio);
        comandos.ejecutar(args);
    }
}