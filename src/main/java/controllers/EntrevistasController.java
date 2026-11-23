package controllers;

import data.DataSourceFactory;
import data.EntrevistaJdbcDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Entrevista;
import models.Oferta;
import service.EntrevistaService;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador del listado de entrevistas: muestra las entrevistas, las filtra
 * por resultado y permite borrarlas. El alta y la edición se hacen desde
 * el formulario de entrevista.
 */
public class EntrevistasController implements Initializable {

    private static final String TODOS = "Todos";

    private final EntrevistaService entrevistaService =
            new EntrevistaService(new EntrevistaJdbcDAO(DataSourceFactory.getDataSource()));

    private FilteredList<Entrevista> entrevistasFiltradas;

    @FXML
    private TableView<Entrevista> tblEntrevistas;
    @FXML
    private TableColumn<Entrevista, String> colFecha;
    @FXML
    private TableColumn<Entrevista, String> colOferta;
    @FXML
    private TableColumn<Entrevista, String> colTipo;
    @FXML
    private TableColumn<Entrevista, String> colResultado;
    @FXML
    private ComboBox<String> cmbResultado;
    @FXML
    private Button btnNueva;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnBorrar;

    /**
     * Enlaza las columnas con los datos de {@link Entrevista}, rellena el filtro
     * por resultado y carga las entrevistas desde la base de datos.
     *
     * @param location  ubicación del FXML (no se usa)
     * @param resources recursos del FXML (no se usan)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colFecha.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getFecha() == null ? "" : Oferta.FORMATO_FECHA.format(c.getValue().getFecha())));
        colOferta.setCellValueFactory(c -> new SimpleStringProperty(
                c.getValue().getOferta() == null ? "" : c.getValue().getOferta().getTitulo()));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colResultado.setCellValueFactory(new PropertyValueFactory<>("resultado"));

        cmbResultado.getItems().add(TODOS);
        cmbResultado.getItems().addAll(Entrevista.RESULTADOS);
        cmbResultado.setValue(TODOS);

        recargar();
        cmbResultado.valueProperty().addListener((obs, anterior, nuevo) -> filtrar());
    }

    /** Abrirá el formulario vacío para crear una entrevista nueva. */
    @FXML
    private void nuevaEntrevista() {
        // Pendiente: abrir el formulario de entrevista vacío.
    }

    /** Abrirá el formulario con los datos de la entrevista seleccionada. */
    @FXML
    private void editarEntrevista() {
        Entrevista seleccionada = tblEntrevistas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una entrevista de la lista.");
            return;
        }
        // Pendiente: abrir el formulario con los datos de "seleccionada".
    }

    /** Borra la entrevista seleccionada tras pedir confirmación. */
    @FXML
    private void borrarEntrevista() {
        Entrevista seleccionada = tblEntrevistas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una entrevista de la lista.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Borrar esta entrevista?", ButtonType.OK, ButtonType.CANCEL);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        entrevistaService.deleteById(seleccionada.getId());
        recargar();
    }

    /** Vuelve a leer las entrevistas de la base de datos y refresca la tabla. */
    public void recargar() {
        entrevistasFiltradas = new FilteredList<>(FXCollections.observableArrayList(entrevistaService.findAll()));
        tblEntrevistas.setItems(entrevistasFiltradas);
        filtrar();
    }

    /** Deja en la tabla solo las entrevistas con el resultado elegido en el desplegable. */
    private void filtrar() {
        String resultado = cmbResultado.getValue();
        entrevistasFiltradas.setPredicate(e ->
                resultado == null || resultado.equals(TODOS) || resultado.equals(e.getResultado()));
    }

    /**
     * Muestra un aviso con el mensaje indicado.
     *
     * @param mensaje texto del aviso
     */
    private void mostrarAviso(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK).showAndWait();
    }
}