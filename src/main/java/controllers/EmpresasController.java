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

    /**
     * Enlaza las columnas con los datos de {@link Empresa}, activa el filtro por nombre
     * y carga las empresas desde la base de datos.
     *
     * @param location  ubicación del FXML (no se usa)
     * @param resources recursos del FXML (no se usan)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colSector.setCellValueFactory(new PropertyValueFactory<>("sector"));
        colWeb.setCellValueFactory(new PropertyValueFactory<>("web"));
        colRegistro.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));

        txtBuscar.textProperty().addListener((obs, anterior, texto) -> filtrar(texto));
        recargar();
    }

    /** Abre el formulario vacío para crear una empresa nueva. */
    @FXML
    private void nuevaEmpresa() {
        abrirFormulario(null);
    }

    /** Abre el formulario con los datos de la empresa seleccionada. */
    @FXML
    private void editarEmpresa() {
        Empresa seleccionada = tblEmpresas.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarAviso("Selecciona una empresa de la lista.");
            return;
        }
        abrirFormulario(seleccionada);
    }

    /**
     * Borra la empresa seleccionada tras pedir confirmación. Si tiene ofertas
     * asociadas, muestra un aviso y no la borra.
     */
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

    /**
     * Abre el formulario de empresa en una ventana nueva y, al cerrarla,
     * refresca la tabla.
     *
     * @param empresa empresa a editar, o {@code null} para crear una nueva
     */
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

    /**
     * Deja en la tabla solo las empresas cuyo nombre contiene el texto indicado,
     * sin distinguir mayúsculas de minúsculas.
     *
     * @param texto texto a buscar; vacío o {@code null} muestra todas
     */
    private void filtrar(String texto) {
        String buscado = texto == null ? "" : texto.trim().toLowerCase();
        empresasFiltradas.setPredicate(e -> e.getNombre().toLowerCase().contains(buscado));
    }

    /**
     * Muestra un aviso con el mensaje indicado.
     *
     * @param mensaje texto del aviso
     */
    private void mostrarAviso(String mensaje) {
        new Alert(Alert.AlertType.WARNING, mensaje, ButtonType.OK).showAndWait();
    }

    /**
     * Cambia a la pantalla del listado de ofertas.
     *
     * @param evento evento del botón, para localizar la ventana actual
     */
    @FXML
    private void candidatura(ActionEvent evento) {
        NavigateTo.cambiarPantalla((Node) evento.getSource(), "/views/ofertas-view.fxml");
    }

    /**
     * Cambia a la pantalla del listado de entrevistas.
     *
     * @param evento evento del botón, para localizar la ventana actual
     */
    @FXML
    private void entrevista(ActionEvent evento) {
        NavigateTo.cambiarPantalla((Node) evento.getSource(), "/views/entrevistas-view.fxml");
    }
}