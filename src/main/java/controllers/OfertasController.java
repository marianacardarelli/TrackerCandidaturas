package controllers;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Oferta;
import service.ImportacionService;
import service.OfertaService;
import utils.Contexto;
import utils.NavigateTo;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controlador de la pantalla principal: muestra el listado de ofertas, las filtra
 * por empresa, estado, salario mínimo y fecha, y permite crearlas, editarlas,
 * borrarlas, ver su detalle e importarlas desde un CSV.
 */
public class OfertasController implements Initializable {
    @javafx.fxml.FXML
    private TextField txtEmpresa;

    @javafx.fxml.FXML
    private ComboBox<String> cmbEstado;
    //Estado para el combo Box
    private static final String TODOS = "Todos";

    @javafx.fxml.FXML
    private TableView<Oferta> tblOfertas;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colTitulo;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colEmpresa;
    @javafx.fxml.FXML
    private TableColumn<Oferta, String> colEstado;
    @javafx.fxml.FXML
    private TableColumn<Oferta, LocalDate> colFecha;


    private final OfertaService servicio = Contexto.getOfertaService();
    @javafx.fxml.FXML
    private TextField txtSalarioMinimo;
    @javafx.fxml.FXML
    private DatePicker dpDesde;

    private static final String RUTA_FORMULARIO = "/views/oferta-form-view.fxml";
    private static final String RUTA_DETALLE = "/views/oferta-detalle-view.fxml";
    private static final String RUTA_EMPRESAS = "/views/empresas-view.fxml";
    private static final String RUTA_ENTREVISTAS = "/views/entrevistas-view.fxml";
    private static final String RUTA_CSV = "/candidaturas.csv";
    private final ImportacionService importacionService = Contexto.getImportacionService();

    /**
     * Enlaza las columnas con los datos de {@link Oferta}, rellena el filtro por estado,
     * hace que los filtros se apliquen al cambiar y carga las ofertas.
     *
     * @param location  ubicación del FXML (no se usa)
     * @param resources recursos del FXML (no se usan)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("nombreEmpresa"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPostulacion"));
        colFecha.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setText(vacia || fecha == null ? null : fecha.format(Oferta.FORMATO_FECHA));
            }
        });

        cmbEstado.getItems().add(TODOS);
        cmbEstado.getItems().addAll(Oferta.ESTADOS);
        cmbEstado.setValue(TODOS);

        txtEmpresa.textProperty().addListener((obs, anterior, texto) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((obs, anterior, estado) -> aplicarFiltros());
        dpDesde.valueProperty().addListener((obs, anterior, fecha) -> aplicarFiltros());
        //Cargar la tabla por primera vez
        aplicarFiltros();

    }

    /**
     * Vuelve a cargar la tabla con las ofertas que cumplen los filtros indicados.
     * Si el salario mínimo no es un número, muestra un aviso y no cambia la tabla.
     */
    @FXML
    private void aplicarFiltros() {
        String estado = TODOS.equals(cmbEstado.getValue()) ? null : cmbEstado.getValue();
        Double salarioMinimo;
        try {
            salarioMinimo = leerSalarioMinimo();
        } catch (NumberFormatException e) {
            mostrarAviso("El salario mínimo debe ser un número.");
            return;
        }
        tblOfertas.setItems(FXCollections.observableArrayList(
                servicio.filtrar(txtEmpresa.getText(), estado, salarioMinimo, dpDesde.getValue())));
    }

    /**
     * Abre en una ventana el detalle de la oferta seleccionada.
     *
     * @param actionEvent evento del botón (no se usa)
     */
    @javafx.fxml.FXML
    public void verOferta(ActionEvent actionEvent) {
        Oferta seleccionada = tblOfertas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una oferta de la lista.");
            return;
        }
        NavigateTo.abrirModal(RUTA_DETALLE, "Detalle de la oferta",
                (OfertaDetalleController controlador) -> controlador.setOferta(seleccionada));
    }

    /**
     * Abre el formulario vacío para crear una oferta nueva y, al cerrarlo, refresca la tabla.
     *
     * @param actionEvent evento del botón (no se usa)
     */
    @javafx.fxml.FXML
    public void nuevaOferta(ActionEvent actionEvent) {
        NavigateTo.abrirModal(RUTA_FORMULARIO, "Nueva oferta", null);
        aplicarFiltros();
    }

    /** Borra la oferta seleccionada tras pedir confirmación. */
    @javafx.fxml.FXML
    private void borrarOferta() {
        Oferta seleccionada = tblOfertas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una oferta de la lista.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Borrar la oferta " + seleccionada.getTitulo() + "?", ButtonType.OK, ButtonType.CANCEL);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        servicio.deleteById(seleccionada.getId());
        aplicarFiltros();
    }

    /**
     * Abre el formulario con los datos de la oferta seleccionada y, al cerrarlo,
     * refresca la tabla.
     *
     * @param actionEvent evento del botón (no se usa)
     */
    @javafx.fxml.FXML
    public void editarOferta(ActionEvent actionEvent) {
        Oferta seleccionada = tblOfertas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una oferta de la lista.");
            return;
        }
        NavigateTo.abrirModal(RUTA_FORMULARIO, "Editar oferta",
                (OfertaFormController controlador) -> controlador.setOferta(seleccionada));
        aplicarFiltros();
    }
    /**
     * Importa las candidaturas del CSV de resources, informa de cuántas se han
     * guardado y refresca la tabla. Si algo falla, no se guarda ninguna.
     */
    @FXML
    private void importarCsv() {
        try {
            int importadas = importacionService.importarCsv(RUTA_CSV);
            new Alert(Alert.AlertType.INFORMATION,
                    "Candidaturas importadas: " + importadas, ButtonType.OK).showAndWait();
            aplicarFiltros();
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        } catch (RuntimeException e) {
            mostrarAviso("No se pudo importar. No se ha guardado ninguna candidatura.");
        }
    }

    /** Cambia a la pantalla del listado de empresas. */
    @FXML
    private void empresa() {
        NavigateTo.cambiarPantalla(tblOfertas, RUTA_EMPRESAS);
    }

    /** Cambia a la pantalla del listado de entrevistas. */
    @FXML
    private void entrevista() {
        NavigateTo.cambiarPantalla(tblOfertas, RUTA_ENTREVISTAS);
    }


    /**
     * Lee el salario mínimo escrito en el filtro. Acepta coma o punto decimal.
     *
     * @return el salario mínimo, o {@code null} si el campo está vacío
     * @throws NumberFormatException si el texto no es un número
     */
    private Double leerSalarioMinimo() {
        String texto = txtSalarioMinimo.getText().trim();
        return texto.isEmpty() ? null : Double.valueOf(texto.replace(',', '.'));
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
