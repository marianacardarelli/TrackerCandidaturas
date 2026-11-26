package utils;

import data.DataSourceFactory;
import data.EmpresaJdbcDAO;
import data.EntrevistaJdbcDAO;
import data.OfertaJdbcDAO;
import service.EmpresaService;
import service.EntrevistaService;
import service.OfertaService;

import javax.sql.DataSource;

/**
 * Crea una sola vez la conexión, los DAO y los servicios de la aplicación
 * y los ofrece a los controladores.
 */
public final class Contexto {

    private static final DataSource DATA_SOURCE = DataSourceFactory.getDataSource();

    private static final EmpresaService EMPRESA_SERVICE =
            new EmpresaService(new EmpresaJdbcDAO(DATA_SOURCE));

    private static final OfertaService OFERTA_SERVICE =
            new OfertaService(new OfertaJdbcDAO(DATA_SOURCE));

    private static final EntrevistaService ENTREVISTA_SERVICE =
            new EntrevistaService(new EntrevistaJdbcDAO(DATA_SOURCE));

    private Contexto() {
    }

    /**
     * @return servicio de empresas compartido por toda la aplicación
     */
    public static EmpresaService getEmpresaService() {
        return EMPRESA_SERVICE;
    }

    /**
     * @return servicio de ofertas compartido por toda la aplicación
     */
    public static OfertaService getOfertaService() {
        return OFERTA_SERVICE;
    }

    /**
     * @return servicio de entrevistas compartido por toda la aplicación
     */
    public static EntrevistaService getEntrevistaService() {
        return ENTREVISTA_SERVICE;
    }
}
