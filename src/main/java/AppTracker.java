import data.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import service.OfertaService;

import java.io.IOException;
/**
 * Aplicación JavaFX del Tracker de Candidaturas. Abre la ventana con el listado de ofertas.
 */
public class AppTracker extends Application {

    /**
     * Crea las tablas y los datos iniciales si hace falta y muestra la pantalla de ofertas.
     *
     * @param stage ventana principal que proporciona JavaFX
     * @throws IOException si no se puede cargar el .fxml de la pantalla de ofertas
     */
    @Override
    public void start(Stage stage) throws IOException {
        DataBaseInitializer.initialize(DataSourceFactory.getDataSource());
        DataBaseInitializer.loadData(DataSourceFactory.getDataSource());
        FXMLLoader cargador = new FXMLLoader(AppTracker.class.getResource("/views/ofertas-view.fxml"));
        stage.setTitle("Tracker de Candidaturas");
        stage.setScene(new Scene(cargador.load()));
        stage.show();
    }
}