package controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Empresa;
import models.Oferta;
import service.EmpresaService;
import service.OfertaService;
import utils.Contexto;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controlador del formulario de oferta: permite dar de alta una oferta nueva
 * o editar una existente, y valida los datos a través de {@link OfertaService}.
 */
public class OfertaFormController implements Initializable {
    @javafx.fxml.FXML
    private TextArea txtNotas;
    @javafx.fxml.FXML
    private TextField txtEnlace;
    @javafx.fxml.FXML
    private DatePicker dpFecha;
    @javafx.fxml.FXML
    private Label lblTitulo;
    @javafx.fxml.FXML
    private ComboBox<Empresa> cmbEmpresa;
    @javafx.fxml.FXML
    private TextField txtSalario;
    @javafx.fxml.FXML
    private ComboBox<String> cmbEstado;
    @javafx.fxml.FXML
    private TextField txtTitulo;

    private final EmpresaService empresaService = Contexto.getEmpresaService();
    private final OfertaService ofertaService = Contexto.getOfertaService();

    private Oferta ofertaEditada;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbEmpresa.setItems(FXCollections.observableArrayList(empresaService.findAll()));
        cmbEstado.setItems(FXCollections.observableArrayList(Oferta.ESTADOS));
        cmbEstado.setValue(Oferta.ESTADOS.getFirst());
        dpFecha.setValue(LocalDate.now());
    }

    @FXML
    private void cancelar() {
        cerrar();
    }

    @FXML
    private void guardarOferta() {
        Oferta oferta = ofertaEditada == null ? new Oferta() : ofertaEditada;
        oferta.setEmpresa(cmbEmpresa.getValue());
        oferta.setTitulo(txtTitulo.getText().trim());
        oferta.setEstado(cmbEstado.getValue());
        oferta.setFechaPostulacion(dpFecha.getValue());
        oferta.setEnlace(txtEnlace.getText().trim());
        oferta.setNotas(txtNotas.getText().trim());
        try {
            oferta.setSalario(leerSalario());
            if (ofertaEditada == null) {
                ofertaService.save(oferta);
            } else {
                ofertaService.update(oferta);
            }
        } catch (NumberFormatException e) {
            mostrarAviso("El salario debe ser un número.");
            return;
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
            return;
        }
        cerrar();
    }
    private Double leerSalario() {
        String texto = txtSalario.getText().trim();
        return texto.isEmpty() ? null : Double.valueOf(texto.replace(',', '.'));
    }

    private void cerrar() {
        ((Stage) txtTitulo.getScene().getWindow()).close();
    }

    private void mostrarAviso(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK).showAndWait();
    }
    /**
     * Prepara el formulario para editar una oferta existente.
     * @param oferta oferta cuyos datos se muestran
     */
    public void setOferta(Oferta oferta) {
        ofertaEditada = oferta;
        lblTitulo.setText("Editar oferta");
        cmbEmpresa.setValue(oferta.getEmpresa());
        txtTitulo.setText(oferta.getTitulo());
        cmbEstado.setValue(oferta.getEstado());
        dpFecha.setValue(oferta.getFechaPostulacion());
        txtSalario.setText(oferta.getSalario() == null ? "" : String.valueOf(oferta.getSalario()));
        txtEnlace.setText(oferta.getEnlace());
        txtNotas.setText(oferta.getNotas());
    }
}
