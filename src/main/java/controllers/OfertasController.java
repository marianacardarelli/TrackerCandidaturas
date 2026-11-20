package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Empresa;
import models.Oferta;

import java.time.LocalDate;

/**
 * Controlador de la pantalla de listado de ofertas ({@code ofertas-view.fxml}).
 * <p>
 * De momento trabaja con datos de ejemplo escritos en el código. Cuando exista la capa
 * de servicio, la lista se rellenará con las ofertas de la base de datos.
 * </p>
 */
public class OfertasController {

    private static final String TODOS = "Todos";

    @FXML
    private TextField txtBuscar;
    @FXML
    private ComboBox<String> cmbEstado;
    @FXML
    private TableView<Oferta> tablaOfertas;
    @FXML
    private TableColumn<Oferta, String> colTitulo;
    @FXML
    private TableColumn<Oferta, String> colEmpresa;
    @FXML
    private TableColumn<Oferta, String> colEstado;
    @FXML
    private TableColumn<Oferta, LocalDate> colFecha;
    @FXML
    private Label lblMensaje;

    private final ObservableList<Oferta> ofertas = FXCollections.observableArrayList();
    private FilteredList<Oferta> ofertasFiltradas;

    /** Se ejecuta automáticamente al cargar el .fxml: prepara la tabla y los filtros. */
    @FXML
    private void initialize() {
        cargarDatosDeEjemplo();
        configurarColumnas();
        configurarFiltros();
    }

    @FXML
    private void limpiarFiltros() {
        txtBuscar.clear();
        cmbEstado.setValue(TODOS);
    }

    @FXML
    private void nuevaOferta() {
        lblMensaje.setText("Pendiente: abrir el formulario de nueva candidatura");
    }

    @FXML
    private void verOferta() {
        if (hayOfertaSeleccionada()) {
            lblMensaje.setText("Pendiente: abrir el detalle de la candidatura");
        }
    }

    @FXML
    private void editarOferta() {
        if (hayOfertaSeleccionada()) {
            lblMensaje.setText("Pendiente: abrir el formulario de edición");
        }
    }

    @FXML
    private void borrarOferta() {
        if (hayOfertaSeleccionada()) {
            ofertas.remove(tablaOfertas.getSelectionModel().getSelectedItem());
            lblMensaje.setText("Candidatura eliminada");
        }
    }

    private boolean hayOfertaSeleccionada() {
        boolean seleccionada = tablaOfertas.getSelectionModel().getSelectedItem() != null;
        if (!seleccionada) {
            lblMensaje.setText("Selecciona una candidatura de la tabla");
        }
        return seleccionada;
    }

    private void configurarColumnas() {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("nombreEmpresa"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPostulacion"));
        // La columna guarda la fecha real (para ordenar bien) y la muestra con formato dd/MM/yyyy
        colFecha.setCellFactory(columna -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate fecha, boolean vacia) {
                super.updateItem(fecha, vacia);
                setText(vacia || fecha == null ? null : fecha.format(Oferta.FORMATO_FECHA));
            }
        });
    }

    private void configurarFiltros() {
        cmbEstado.getItems().add(TODOS);
        cmbEstado.getItems().addAll(Oferta.ESTADOS);
        cmbEstado.setValue(TODOS);

        ofertasFiltradas = new FilteredList<>(ofertas, oferta -> true);
        txtBuscar.textProperty().addListener((observable, anterior, nuevo) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((observable, anterior, nuevo) -> aplicarFiltros());

        // La lista ordenada permite ordenar la tabla pulsando en las cabeceras de las columnas
        SortedList<Oferta> ofertasOrdenadas = new SortedList<>(ofertasFiltradas);
        ofertasOrdenadas.comparatorProperty().bind(tablaOfertas.comparatorProperty());
        tablaOfertas.setItems(ofertasOrdenadas);
    }

    private void aplicarFiltros() {
        String texto = txtBuscar.getText() == null ? "" : txtBuscar.getText().trim().toLowerCase();
        String estado = cmbEstado.getValue();
        boolean cualquierEstado = estado == null || TODOS.equals(estado);
        ofertasFiltradas.setPredicate(oferta ->
                (texto.isEmpty()
                        || oferta.getTitulo().toLowerCase().contains(texto)
                        || oferta.getNombreEmpresa().toLowerCase().contains(texto))
                        && (cualquierEstado || oferta.getEstado().equals(estado)));
    }

    private void cargarDatosDeEjemplo() {
        Empresa indra = new Empresa(1, "Indra", "Tecnología", "", LocalDate.of(2026, 9, 1));
        Empresa everis = new Empresa(2, "Everis", "Consultoría", "", LocalDate.of(2026, 9, 2));
        Empresa telefonica = new Empresa(3, "Telefónica", "Telecomunicaciones", "", LocalDate.of(2026, 9, 3));
        Empresa bbva = new Empresa(4, "BBVA", "Banca", "", LocalDate.of(2026, 9, 4));

        ofertas.addAll(
                ejemplo(1, indra, "Desarrollador Java", "pendiente", LocalDate.of(2026, 9, 15), 30000.0),
                ejemplo(2, everis, "Backend Spring", "entrevista", LocalDate.of(2026, 9, 12), 32000.0),
                ejemplo(3, telefonica, "Analista de datos", "aceptado", LocalDate.of(2026, 9, 3), 28000.0),
                ejemplo(4, bbva, "Frontend Angular", "rechazado", LocalDate.of(2026, 8, 28), null),
                ejemplo(5, indra, "Tester QA", "pendiente", LocalDate.of(2026, 10, 2), 24000.0),
                ejemplo(6, everis, "DevOps Junior", "entrevista", LocalDate.of(2026, 10, 9), 27000.0));
    }

    private Oferta ejemplo(long id, Empresa empresa, String titulo, String estado, LocalDate fecha, Double salario) {
        return new Oferta(id, empresa, titulo, "", estado, fecha, "", salario, "");
    }
}