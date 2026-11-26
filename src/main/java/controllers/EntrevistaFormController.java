package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Entrevista;
import models.Oferta;
import service.EntrevistaService;
import service.OfertaService;
import utils.Contexto;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador del formulario de entrevista. Sirve para dar de alta una entrevista
 * nueva o para editar una existente, según se llame o no a {@link #setEntrevista(Entrevista)}.
 */
public class EntrevistaFormController implements Initializable {

    private final EntrevistaService entrevistaService = Contexto.getEntrevistaService();
    private final OfertaService ofertaService = Contexto.getOfertaService();

    private Entrevista entrevista;

    @FXML
    private ComboBox<Oferta> cmbOferta;
    @FXML
    private DatePicker dpFecha;
    @FXML
    private ComboBox<String> cmbTipo;
    @FXML
    private ComboBox<String> cmbResultado;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnCancelar;
    @FXML
    private TextArea txtNotas;
    @FXML
    private Label lblError;

    /**
     * Rellena los desplegables de oferta, tipo y resultado y deja vacío el mensaje de error.
     *
     * @param location  ubicación del FXML (no se usa)
     * @param resources recursos del FXML (no se usan)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cmbOferta.getItems().addAll(ofertaService.findAll());
        cmbTipo.getItems().addAll(Entrevista.TIPOS);
        cmbResultado.getItems().addAll(Entrevista.RESULTADOS);
        cmbResultado.setValue("pendiente");
        lblError.setText("");
    }

    /**
     * Prepara el formulario para editar una entrevista. Si es {@code null},
     * el formulario se queda vacío para crear una nueva.
     *
     * @param entrevista entrevista a editar, o {@code null} para crear una nueva
     */
    public void setEntrevista(Entrevista entrevista) {
        this.entrevista = entrevista;
        if (entrevista == null) {
            return;
        }
        for (Oferta oferta : cmbOferta.getItems()) {
            if (oferta.getId() == entrevista.getOferta().getId()) {
                cmbOferta.setValue(oferta);
            }
        }
        dpFecha.setValue(entrevista.getFecha());
        cmbTipo.setValue(entrevista.getTipo());
        cmbResultado.setValue(entrevista.getResultado());
        txtNotas.setText(entrevista.getNotas());
    }

    /**
     * Guarda la entrevista (alta o edición) y cierra la ventana. Si los datos no son
     * válidos, muestra el motivo en {@code lblError} y deja la ventana abierta.
     */
    @FXML
    private void guardar() {
        Entrevista datos = new Entrevista();
        if (entrevista != null) {
            datos.setId(entrevista.getId());
        }
        datos.setOferta(cmbOferta.getValue());
        datos.setFecha(dpFecha.getValue());
        datos.setTipo(cmbTipo.getValue());
        datos.setResultado(cmbResultado.getValue());
        datos.setNotas(txtNotas.getText().trim());

        try {
            if (entrevista == null) {
                entrevistaService.save(datos);
            } else {
                entrevistaService.update(datos);
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