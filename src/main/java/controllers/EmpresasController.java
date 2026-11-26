package controllers;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Empresa;
import service.EmpresaService;
import utils.Contexto;
import utils.NavigateTo;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


/**
 * Controlador del listado de empresas: muestra las empresas, filtra por nombre
 * y permite borrarlas. El alta y la edición se hacen desde el formulario de empresa.
 */
public class EmpresasController implements Initializable {

    private final EmpresaService empresaService = Contexto.getEmpresaService();

    private FilteredList<Empresa> empresasFiltradas;
    @FXML
    private Button btnNueva;
    @FXML
    private TableView<Empresa> tblEmpresas;
    @FXML
    private Button btnBorrar;
    @FXML
    private TableColumn<Empresa, String> colNombre;
    @FXML
    private TableColumn<Empresa, String> colWeb;
    @FXML
    private TextField txtBuscar;
    @FXML
    private TableColumn<Empresa, String> colSector;
    @FXML
    private TableColumn<Empresa, LocalDate> colRegistro;
    @FXML
    private Button btnEditar;
    @FXML
    private Button btnCandidaturas;
    @FXML
    private Button btnEntrevista;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSector.setCellValueFactory(new PropertyValueFactory<>("sector"));
        colWeb.setCellValueFactory(new PropertyValueFactory<>("web"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));

        txtBuscar.textProperty().addListener((obs, anterior, texto) -> filtrar(texto));
        recargar();
    }

    @FXML
    private void nuevaEmpresa() {
        abrirFormulario(null);
    }

    @FXML
    private void editarEmpresa() {
        Empresa seleccionada = tblEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una empresa de la lista.");
            return;
        }
        abrirFormulario(seleccionada);
    }

    @FXML
    private void borrarEmpresa() {
        Empresa seleccionada = tblEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una empresa de la lista.");
            return;
        }
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION,
                "¿Borrar la empresa " + seleccionada.getNombre() + "?", ButtonType.OK, ButtonType.CANCEL);
        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) != ButtonType.OK) {
            return;
        }
        try {
            empresaService.deleteById(seleccionada.getId());
            recargar();
        } catch (IllegalArgumentException e) {
            mostrarAviso(e.getMessage());
        }
    }

    private void abrirFormulario(Empresa empresa) {
        String titulo = empresa == null ? "Nueva empresa" : "Editar empresa";
        NavigateTo.abrirModal("/views/empresa-form-view.fxml", titulo,
                (EmpresaFormController formulario) -> formulario.setEmpresa(empresa));
        recargar();
    }

    /**
     * Vuelve a leer las empresas de la base de datos y refresca la tabla.
     */
    public void recargar() {
        empresasFiltradas = new FilteredList<>(FXCollections.observableArrayList(empresaService.findAll()));
        tblEmpresas.setItems(empresasFiltradas);
        filtrar(txtBuscar.getText());
    }

    private void filtrar(String texto) {
        String buscado = texto == null ? "" : texto.trim().toLowerCase();
        empresasFiltradas.setPredicate(e -> e.getNombre().toLowerCase().contains(buscado));
    }


    private void mostrarAviso(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK).showAndWait();
    }


    @FXML
    private void candidatura(ActionEvent evento) {
        NavigateTo.cambiarPantalla((Node) evento.getSource(), "/views/ofertas-view.fxml");
    }

    @FXML
    private void entrevista(ActionEvent evento) {
        NavigateTo.cambiarPantalla((Node) evento.getSource(), "/views/entrevistas-view.fxml");
    }
}