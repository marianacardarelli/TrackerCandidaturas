package controllers;

import data.DataSourceFactory;
import data.OfertaJdbcDAO;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
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
    private ComboBox cmbEstado;
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        colEmpresa.setCellValueFactory(new PropertyValueFactory<>("nombreEmpresa"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaPostulacion"));
        tblOfertas.setItems(FXCollections.observableArrayList(servicio.findAll()));
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
