package controllers;

import data.DataSourceFactory;
import data.EmpresaJdbcDAO;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Empresa;
import service.EmpresaService;
import utils.Contexto;
import models.Oferta;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador del formulario de empresa. Sirve para dar de alta una empresa nueva
 * o para editar una existente, según se llame o no a {@link #setEmpresa(Empresa)}.
 */
public class EmpresaFormController implements Initializable {

    private final EmpresaService empresaService = Contexto.getEmpresaService();

    private Empresa empresa;

    @FXML
    private TextField txtNombre;
    @FXML
    private ComboBox<String> cmbSector;
    @FXML
    private TextField txtWeb;
    @FXML
    private Button btnGuardar;
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblNombre;
    @FXML
    private Label titulo;
    @FXML
    private Button btnCancelar;
    @FXML
    private Label lblweb;
    @FXML
    private TextField txtFecha;
    @FXML
    private Label lblSector;
    @FXML
    private Label lblError;

    /**
     * Rellena el desplegable de sectores y deja vacío el mensaje de error.
     *
     * @param location  ubicación del FXML (no se usa)
     * @param resources recursos del FXML (no se usan)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbSector.getItems().addAll("Consultoría tecnológica", "Tecnología", "Telecomunicaciones", "Banca", "Otros");
        lblError.setText("");
        txtFecha.setEditable(false);
    }

    /**
     * Prepara el formulario para editar una empresa. Si es {@code null},
     * el formulario se queda vacío para crear una nueva.
     *
     * @param empresa empresa a editar, o {@code null} para crear una nueva
     */
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
        if (empresa != null) {
            txtNombre.setText(empresa.getNombre());
            cmbSector.setValue(empresa.getSector());
            txtWeb.setText(empresa.getWeb());
            if (empresa.getFechaRegistro() != null) {
                txtFecha.setText(empresa.getFechaRegistro().format(Oferta.FORMATO_FECHA));
            }
        }
    }

    /**
     * Guarda la empresa (alta o edición) y cierra la ventana. Si los datos no son
     * válidos, muestra el motivo en {@code lblError} y deja la ventana abierta.
     */
    @FXML
    private void guardar() {
        Empresa datos = new Empresa();
        if (empresa != null) {
            datos.setId(empresa.getId());
            datos.setFechaRegistro(empresa.getFechaRegistro());
        }
        datos.setNombre(txtNombre.getText().trim());
        datos.setSector(cmbSector.getValue());
        datos.setWeb(txtWeb.getText().trim());

        try {
            if (empresa == null) {
                empresaService.save(datos);
            } else {
                empresaService.update(datos);
            }
            cerrar();
        } catch (IllegalArgumentException e) {
            lblError.setText(e.getMessage());
        }
    }

    /** Cierra la ventana sin guardar nada. */
    @FXML
    private void cancelar() {
        cerrar();
    }

    /** Cierra la ventana en la que está el formulario. */
    private void cerrar() {
        btnGuardar.getScene().getWindow().hide();
    }
}