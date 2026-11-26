package service;

import data.DAO;
import models.Entrevista;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Reglas de negocio de las entrevistas: valida los datos antes de guardarlos
 * y traduce los errores de la base de datos a mensajes comprensibles.
 */
public class EntrevistaService {

    private final DAO<Entrevista> dao;

    /**
     * Crea el servicio con el DAO que va a utilizar.
     *
     * @param dao acceso a los datos de las entrevistas
     */
    public EntrevistaService(DAO<Entrevista> dao) {
        this.dao = dao;
    }

    /**
     * Devuelve todas las entrevistas.
     *
     * @return lista con todas las entrevistas
     */
    public List<Entrevista> findAll() {
        return dao.findAll();
    }

    /**
     * Devuelve las entrevistas de una oferta.
     *
     * @param idOferta identificador de la oferta
     * @return entrevistas de esa oferta
     */
    public List<Entrevista> findByOferta(Long idOferta) {
        return dao.findAll().stream()
                .filter(e -> e.getOferta() != null && Objects.equals(e.getOferta().getId(), idOferta))
                .toList();
    }

    /**
     * Busca una entrevista por su id.
     *
     * @param id identificador de la entrevista
     * @return la entrevista, si existe
     */
    public Optional<Entrevista> findById(Long id) {
        return dao.findById(id);
    }

    /**
     * Guarda una entrevista nueva.
     *
     * @param entrevista entrevista a guardar
     * @return la entrevista guardada, con su id
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public Entrevista save(Entrevista entrevista) {
        validar(entrevista);
        return dao.save(entrevista);
    }

    /**
     * Actualiza una entrevista existente. El id no se modifica.
     *
     * @param entrevista entrevista con los datos nuevos
     * @throws IllegalArgumentException si algún dato no es válido
     */
    public void update(Entrevista entrevista) {
        validar(entrevista);
        dao.update(entrevista);
    }

    /**
     * Borra una entrevista.
     *
     * @param id identificador de la entrevista
     */
    public void deleteById(Long id) {
        dao.deleteById(id);
    }

    private void validar(Entrevista entrevista) {
        if (entrevista.getOferta() == null) {
            throw new IllegalArgumentException("Debes elegir una oferta.");
        }
        if (entrevista.getFecha() == null) {
            throw new IllegalArgumentException("La fecha de la entrevista es obligatoria.");
        }
        if (entrevista.getTipo() == null || !Entrevista.TIPOS.contains(entrevista.getTipo())) {
            throw new IllegalArgumentException("El tipo debe ser uno de: " + Entrevista.TIPOS + ".");
        }
        if (entrevista.getResultado() == null || !Entrevista.RESULTADOS.contains(entrevista.getResultado())) {
            throw new IllegalArgumentException("El resultado debe ser uno de: " + Entrevista.RESULTADOS + ".");
        }
    }
}
