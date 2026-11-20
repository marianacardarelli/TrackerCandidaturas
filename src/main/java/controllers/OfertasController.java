package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import models.Oferta;

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

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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
