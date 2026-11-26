package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Entrevista;
import models.Oferta;
import service.EntrevistaService;
import utils.Contexto;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controlador de la pantalla de detalle de una oferta: muestra sus datos,
 * la empresa asociada y la lista de sus entrevistas.
 */
public class OfertaDetalleController implements Initializable {

    @FXML
    private Label lblEstado;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblNotas;
    @FXML
    private Label lblEnlace;
    @FXML
    private Label lblTitulo;
    @FXML
    private Label lblSalario;
    @FXML
    private Label lblEmpresa;
    @FXML
    private TableColumn<Entrevista, String> colTipoEntrevista;
    @FXML
    private TableColumn<Entrevista, String> colResultadoEntrevista;
    @FXML
    private TableView<Entrevista> tblEntrevistas;
    @FXML
    private TableColumn<Entrevista, LocalDate> colFechaEntrevista;

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
