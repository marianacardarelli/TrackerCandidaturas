package data;

import java.util.List;
import java.util.Optional;

/**
 * Operaciones básicas de acceso a datos (CRUD) para una entidad.
 *
 * @param <T> tipo de la entidad que se guarda
 */
public interface DAO<T> {

    /**
     * Busca una entidad por su id.
     *
     * @param id identificador de la entidad
     * @return la entidad, o vacío si no existe
     */
    Optional<T> findById(Long id);

    /**
     * Devuelve todas las entidades.
     *
     * @return lista con todas las entidades
     */
    List<T> findAll();

    /**
     * Guarda una entidad nueva y le asigna el id generado.
     *
     * @param obj entidad a guardar
     * @return la entidad guardada, con su id
     */
    T save(T obj);

    /**
     * Actualiza una entidad existente, identificada por su id.
     *
     * @param entity entidad con los datos nuevos
     */
    void update(T entity);

    /**
     * Borra una entidad.
     *
     * @param id identificador de la entidad
     */
    void deleteById(Long id);
}
