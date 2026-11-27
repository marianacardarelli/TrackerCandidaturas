package service;

import data.DAO;
import models.Empresa;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.Optional;

/**
 * Reglas de negocio de las empresas: valida los datos antes de guardarlos
 * y traduce los errores de la base de datos a mensajes comprensibles.
 */
public class EmpresaService {

    /**
     * Longitud máxima del nombre de una empresa, igual que en la tabla.
     */
    private static final int LONGITUD_MAXIMA_NOMBRE = 100;

    /** DAO con el que se leen y guardan las empresas. */
    private final DAO<Empresa> dao;

    /**
     * Crea el servicio con el DAO que va a utilizar.
     *
     * @param dao acceso a los datos de las empresas
     */
    public EmpresaService(DAO<Empresa> dao) {
        this.dao = dao;
    }

    /**
     * Devuelve todas las empresas.
     *
     * @return lista con todas las empresas
     */
    public List<Empresa> findAll() {
        return dao.findAll();
    }

    /**
     * Busca una empresa por su id.
     *
     * @param id identificador de la empresa
     * @return la empresa, si existe
     */
    public Optional<Empresa> findById(Long id) {
        return dao.findById(id);
    }

    /**
     * Guarda una empresa nueva. La fecha de registro la asigna la base de datos.
     *
     * @param empresa empresa a guardar
     * @return la empresa guardada, con su id
     * @throws IllegalArgumentException si el nombre no es válido o ya existe
     */
    public Empresa save(Empresa empresa) {
        validar(empresa);
        try {
            return dao.save(empresa);
        } catch (RuntimeException e) {
            throw traducirNombreDuplicado(e);
        }
    }

    /**
     * Actualiza una empresa existente. El id no se modifica.
     *
     * @param empresa empresa con los datos nuevos
     * @throws IllegalArgumentException si el nombre no es válido o ya existe
     */
    public void update(Empresa empresa) {
        validar(empresa);
        try {
            dao.update(empresa);
        } catch (RuntimeException e) {
            throw traducirNombreDuplicado(e);
        }
    }

    /**
     * Borra una empresa.
     *
     * @param id identificador de la empresa
     * @throws IllegalArgumentException si la empresa tiene ofertas asociadas
     */
    public void deleteById(Long id) {
        try {
            dao.deleteById(id);
        } catch (RuntimeException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                throw new IllegalArgumentException(
                        "No se puede borrar la empresa porque tiene ofertas asociadas.");
            }
            throw e;
        }
    }

    /**
     * Comprueba que la empresa tiene nombre y sector, y que el nombre no es demasiado largo.
     *
     * @param empresa empresa a validar
     * @throws IllegalArgumentException si algún dato falta o no es válido
     */
    private void validar(Empresa empresa) {
        if (empresa.getNombre() == null || empresa.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre de la empresa es obligatorio.");
        }
        if (empresa.getSector() == null || empresa.getSector().isBlank()) {
            throw new IllegalArgumentException("El sector es obligatorio.");
        }
        if (empresa.getNombre().length() > LONGITUD_MAXIMA_NOMBRE) {
            throw new IllegalArgumentException(
                    "El nombre no puede superar los " + LONGITUD_MAXIMA_NOMBRE + " caracteres.");
        }
    }

    /**
     * Convierte el error de nombre repetido de la base de datos en un mensaje comprensible.
     *
     * @param e excepción lanzada por el DAO
     * @return una {@link IllegalArgumentException} si el nombre ya existe, o la misma excepción si no
     */
    private RuntimeException traducirNombreDuplicado(RuntimeException e) {
        if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
            return new IllegalArgumentException("Ya existe una empresa con ese nombre.");
        }
        return e;
    }
}