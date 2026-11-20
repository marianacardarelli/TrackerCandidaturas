import data.DataBaseInitializer;
import data.DataSourceFactory;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Aplicación JavaFX del Tracker de Candidaturas. Abre la ventana con el listado de ofertas.
 */
public class AppTracker extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        DataBaseInitializer.initialize(DataSourceFactory.getDataSource());
        DataBaseInitializer.loadData(DataSourceFactory.getDataSource());
    }
}