import javafx.application.Application;

/**
 * Lanzador de la aplicación. Se ejecuta esta clase, no {@link AppTracker}.
 */
public class Main {

    /**
     * Arranca la aplicación JavaFX.
     *
     * @param args argumentos de la línea de comandos (no se usan)
     */
    public static void main(String[] args) {
        Application.launch(AppTracker.class, args);
    }
}