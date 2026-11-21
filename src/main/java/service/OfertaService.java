package service;
import data.DAO;
import models.Oferta;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Lógica de negocio de las ofertas. Trabaja contra cualquier {@link DAO},
 * ya sea en memoria o JDBC.
 */
public class OfertaService {
    /** DAO con el que se leen y guardan las ofertas. */
    private final DAO<Oferta> dao;
    /**
     * Crea el servicio.
     * @param dao acceso a datos de ofertas
     */
    public OfertaService(DAO<Oferta> dao) {
        this.dao = dao;
    }

    /**
     * Devuelve todas las ofertas.
     * @return lista de ofertas
     */
    public List<Oferta> findAll() {
        return dao.findAll();
    }
    /**
     * Busca una oferta por id.
     * @param id identificador de la oferta
     * @return la oferta, o vacío si no existe
     */
    public Optional<Oferta> findById(long id) {
        return dao.findById(id);
    }

    /**
     * Valida y guarda una oferta nueva.
     * @param oferta oferta a guardar
     * @return la oferta guardada, con su id
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public Oferta save(Oferta oferta) {
        validar(oferta);
        return dao.save(oferta);
    }

    /**
     * Valida y actualiza una oferta existente.
     * @param oferta oferta con los datos nuevos
     * @throws IllegalArgumentException si los datos no son válidos
     */
    public void update(Oferta oferta) {
        validar(oferta);
        dao.update(oferta);
    }

    /**
     * Elimina una oferta.
     * @param id identificador de la oferta
     */
    public void deleteById(long id) {
        dao.deleteById(id);
    }

    /**
     * Cambia el estado de una oferta.
     * @param oferta oferta a modificar
     * @param estado nuevo estado, uno de {@link Oferta#ESTADOS}
     * @throws IllegalArgumentException si el estado no es válido
     */
    public void cambiarEstado(Oferta oferta, String estado) {
        if (!Oferta.ESTADOS.contains(estado)) {
            throw new IllegalArgumentException("Estado no válido: " + estado);
        }
        oferta.setEstado(estado);
        dao.update(oferta);
    }

    /**
     * Filtra las ofertas combinando empresa, estado y salario mínimo.
     * Los filtros nulos o vacíos se ignoran.
     * @param empresa texto contenido en el nombre de la empresa
     * @param estado estado exacto de la oferta
     * @param salarioMinimo salario mínimo
     * @return ofertas que cumplen todos los filtros indicados
     */
    public List<Oferta> filtrar(String empresa, String estado, Double salarioMinimo, LocalDate desde) {
        return dao.findAll().stream()
                .filter(o -> empresa == null || empresa.isBlank()
                        || o.getNombreEmpresa().toLowerCase().contains(empresa.toLowerCase()))
                .filter(o -> estado == null || estado.isBlank() || o.getEstado().equals(estado))
                .filter(o -> salarioMinimo == null
                        || (o.getSalario() != null && o.getSalario() >= salarioMinimo))
                .filter(o -> desde == null
                        || (o.getFechaPostulacion() != null && !o.getFechaPostulacion().isBefore(desde)))
                .toList();
    }

    private void validar(Oferta oferta) {
        if (oferta.getTitulo() == null || oferta.getTitulo().isBlank()) {
            throw new IllegalArgumentException("El título es obligatorio");
        }
        if (oferta.getEmpresa() == null) {
            throw new IllegalArgumentException("La empresa es obligatoria");
        }
        if (!Oferta.ESTADOS.contains(oferta.getEstado())) {
            throw new IllegalArgumentException("Estado no válido: " + oferta.getEstado());
        }
        if (oferta.getSalario() != null && oferta.getSalario() < 0) {
            throw new IllegalArgumentException("El salario no puede ser negativo");
        }
    }
}
