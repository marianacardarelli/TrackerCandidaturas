package controllers;

import data.DataSourceFactory;
import data.EntrevistaJdbcDAO;
import javafx.collections.FXCollections;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Oferta;
import service.EntrevistaService;
import utils.Contexto;

import java.net.URL;
import java.util.ResourceBundle;

public class OfertaDetalleController implements Initializable {

    @javafx.fxml.FXML
    private Label lblEstado;
    @javafx.fxml.FXML
    private Label lblFecha;
    @javafx.fxml.FXML
    private Label lblNotas;
    @javafx.fxml.FXML
    private Label lblEnlace;
    @javafx.fxml.FXML
    private Label lblTitulo;
    @javafx.fxml.FXML
    private Label lblSalario;
    @javafx.fxml.FXML
    private Label lblEmpresa;
    @javafx.fxml.FXML
    private TableColumn colTipoEntrevista;
    @javafx.fxml.FXML
    private TableColumn colResultadoEntrevista;
    @javafx.fxml.FXML
    private TableView tblEntrevistas;
    @javafx.fxml.FXML
    private TableColumn colFechaEntrevista;

    private final EntrevistaService entrevistaService = Contexto.getEntrevistaService();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colFechaEntrevista.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipoEntrevista.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colResultadoEntrevista.setCellValueFactory(new PropertyValueFactory<>("resultado"));

    }

    /**
     * Muestra en pantalla los datos de la oferta recibida.
     *
     * @param oferta oferta a mostrar
     */
    public void setOferta(Oferta oferta) {
        lblTitulo.setText(oferta.getTitulo());
        lblEmpresa.setText("Empresa: " + oferta.getEmpresa().getNombre());
        lblEstado.setText("Estado: " + oferta.getEstado());
        lblFecha.setText("Fecha: " + oferta.getFechaPostulacion());
        lblSalario.setText("Salario: " + oferta.getSalario());
        lblEnlace.setText("Enlace: " + oferta.getEnlace());
        lblNotas.setText("Notas: " + oferta.getNotas());
        tblEntrevistas.setItems(FXCollections.observableArrayList(
                entrevistaService.findByOferta(oferta.getId())));
    }
}
