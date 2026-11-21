package controllers;

import data.DataSourceFactory;
import data.OfertaJdbcDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Oferta;
import service.OfertaService;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

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


    private final OfertaService servicio =
            new OfertaService(new OfertaJdbcDAO(DataSourceFactory.getDataSource()));
    @javafx.fxml.FXML
    private TextField txtSalarioMinimo;
    @javafx.fxml.FXML
    private DatePicker dpDesde;

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

    private Double leerSalarioMinimo() {
        String texto = txtSalarioMinimo.getText().trim();
        return texto.isEmpty() ? null : Double.valueOf(texto.replace(',', '.'));
    }

    private void mostrarAviso(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK).showAndWait();
    }

    @javafx.fxml.FXML
    public void verOferta(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void nuevaOferta(ActionEvent actionEvent) {
    }

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

    @javafx.fxml.FXML
    public void editarOferta(ActionEvent actionEvent) {
    }
}
