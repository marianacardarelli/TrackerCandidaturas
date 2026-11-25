package controllers;

import data.DataSourceFactory;
import data.EmpresaJdbcDAO;
import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import models.Empresa;
import service.EmpresaService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

import java.io.IOException;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;


/**
 * Controlador del listado de empresas: muestra las empresas, filtra por nombre
 * y permite borrarlas. El alta y la edición se hacen desde el formulario de empresa.
 */
public class EmpresasController implements Initializable {

    private final EmpresaService empresaService = new EmpresaService(new EmpresaJdbcDAO(DataSourceFactory.getDataSource()));

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/empresa-form-view.fxml"));
            Parent raiz = loader.load();
            EmpresaFormController formulario = loader.getController();
            formulario.setEmpresa(empresa);

            Stage ventana = new Stage();
            ventana.setScene(new Scene(raiz));
            ventana.showAndWait();
            recargar();
        } catch (IOException e) {
            mostrarAviso("No se pudo abrir el formulario.");
        }
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
    public void candidatura(ActionEvent actionEvent) {
    }

    @FXML
    public void entrevista(ActionEvent actionEvent) {
    }
}