package controllers;

import data.DataSourceFactory;
import data.OfertaJdbcDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
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

        cmbEstado.getItems().add(TODOS);
        cmbEstado.getItems().addAll(Oferta.ESTADOS);
        cmbEstado.setValue(TODOS);
        txtEmpresa.textProperty().addListener((obs, anterior, texto) -> aplicarFiltros());
        cmbEstado.valueProperty().addListener((obs, anterior, estado) -> aplicarFiltros());
        //Cargar la tabla por primera vez
        aplicarFiltros();
    }

    private void aplicarFiltros() {
        String estado = TODOS.equals(cmbEstado.getValue()) ? null : cmbEstado.getValue();
        tblOfertas.setItems(FXCollections.observableArrayList(
                servicio.filtrar(txtEmpresa.getText(), estado, null)));
    }

    @javafx.fxml.FXML
    public void verOferta(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void nuevaOferta(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void borrarOferta(ActionEvent actionEvent) {
    }

    @javafx.fxml.FXML
    public void editarOferta(ActionEvent actionEvent) {
    }
}
