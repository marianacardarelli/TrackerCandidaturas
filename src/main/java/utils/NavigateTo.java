package utils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.function.Consumer;
/**
 * Abre pantallas de la aplicación en ventanas nuevas.
 */
public final class NavigateTo {

    private NavigateTo() {
    }

    /**
     * Abre una pantalla en una ventana modal y espera a que se cierre.
     *
     * @param rutaFxml ruta del .fxml dentro de resources, por ejemplo "/views/oferta-form-view.fxml"
     * @param titulo título de la ventana
     * @param configurar acción que recibe el controlador de la pantalla antes de mostrarla,
     *                   o {@code null} si no hace falta configurar nada
     * @param <T> tipo del controlador de esa pantalla
     * @throws UncheckedIOException si no se puede cargar el .fxml
     */
    public static <T> void abrirModal(String rutaFxml, String titulo, Consumer<T> configurar) {
        try {
            FXMLLoader cargador = new FXMLLoader(NavigateTo.class.getResource(rutaFxml));
            Parent raiz = cargador.load();
            T controlador = cargador.getController();
            if (configurar != null) {
                configurar.accept(controlador);
            }
            Stage ventana = new Stage();
            ventana.setTitle(titulo);
            ventana.initModality(Modality.APPLICATION_MODAL);
            ventana.setScene(new Scene(raiz));
            ventana.showAndWait();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Sustituye el contenido de la ventana actual por otra pantalla.
     *
     * @param origen cualquier elemento de la pantalla actual, para localizar su ventana
     * @param rutaFxml ruta del .fxml dentro de resources, por ejemplo "/views/empresas-view.fxml"
     * @throws UncheckedIOException si no se puede cargar el .fxml
     */
    public static void cambiarPantalla(Node origen, String rutaFxml) {
        try {
            Parent raiz = FXMLLoader.load(NavigateTo.class.getResource(rutaFxml));
            Stage ventana = (Stage) origen.getScene().getWindow();
            ventana.getScene().setRoot(raiz);
            ventana.sizeToScene();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
