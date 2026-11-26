package service;

import data.ImportacionJdbcDAO;
import models.Oferta;
import utils.LectorCsv;

import java.util.List;

/**
 * Reglas de negocio de la importación: lee el CSV, valida las candidaturas
 * y las guarda en la base de datos en una sola transacción.
 */
public class ImportacionService {

    private final ImportacionJdbcDAO dao;

    /**
     * Crea el servicio con el DAO que va a utilizar.
     *
     * @param dao acceso a los datos de la importación
     */
    public ImportacionService(ImportacionJdbcDAO dao) {
        this.dao = dao;
    }

    /**
     * Importa las candidaturas de un CSV que está en resources.
     *
     * @param ruta ruta del archivo dentro de resources, por ejemplo "/candidaturas.csv"
     * @return número de candidaturas nuevas guardadas
     * @throws IllegalArgumentException si el archivo no existe o alguna línea no es válida
     */
    public int importarCsv(String ruta) {
        List<Oferta> ofertas = LectorCsv.leerOfertas(ruta);
        for (int i = 0; i < ofertas.size(); i++) {
            validar(ofertas.get(i), i + 1);
        }
        return dao.importar(ofertas);
    }

    private void validar(Oferta oferta, int posicion) {
        if (oferta.getTitulo().isBlank()) {
            throw new IllegalArgumentException("Candidatura " + posicion + ": el título es obligatorio.");
        }
        if (oferta.getEmpresa().getNombre().isBlank()) {
            throw new IllegalArgumentException("Candidatura " + posicion + ": la empresa es obligatoria.");
        }
        if (!Oferta.ESTADOS.contains(oferta.getEstado())) {
            throw new IllegalArgumentException("Candidatura " + posicion + ": el estado debe ser uno de "
                    + Oferta.ESTADOS + ".");
        }
    }
}
