package data;

import models.Oferta;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * Importa ofertas en la base de datos dentro de una única transacción:
 * o se guardan todas o no se guarda ninguna.
 */
public class ImportacionJdbcDAO {

    /** Sector que se asigna a las empresas creadas durante la importación. */
    private static final String SECTOR_POR_DEFECTO = "Sin especificar";

    /** Origen de datos del que se obtienen las conexiones. */
    private final DataSource ds;

    /**
     * Crea el DAO.
     *
     * @param ds origen de datos de la base de datos
     */
    public ImportacionJdbcDAO(DataSource ds) {
        this.ds = ds;
    }

    /**
     * Guarda las ofertas en una transacción. Las empresas que no existen se crean,
     * y las ofertas que ya existen (mismo título y misma empresa) se omiten.
     * Si ocurre cualquier error se deshace todo.
     *
     * @param ofertas ofertas a importar, con la empresa informada solo por su nombre
     * @return número de ofertas nuevas guardadas
     */
    public int importar(List<Oferta> ofertas) {
        try (Connection conn = ds.getConnection()) {
            conn.setAutoCommit(false);
            try {
                int importadas = 0;
                for (Oferta oferta : ofertas) {
                    long idEmpresa = buscarOCrearEmpresa(conn, oferta.getEmpresa().getNombre());
                    if (!existeOferta(conn, oferta.getTitulo(), idEmpresa)) {
                        insertarOferta(conn, oferta, idEmpresa);
                        importadas++;
                    }
                }
                conn.commit();
                return importadas;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Devuelve el id de la empresa con ese nombre y, si no existe, la crea
     * con el sector {@link #SECTOR_POR_DEFECTO}.
     *
     * @param conn   conexión de la transacción en curso
     * @param nombre nombre de la empresa
     * @return id de la empresa existente o recién creada
     * @throws SQLException si falla alguna consulta
     */
    private long buscarOCrearEmpresa(Connection conn, String nombre) throws SQLException {
        String consulta = "select id from Empresa where nombre = ?";
        try (PreparedStatement pst = conn.prepareStatement(consulta)) {
            pst.setString(1, nombre);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("id");
                }
            }
        }
        String insercion = "insert into Empresa (nombre, sector) values (?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(insercion, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, nombre);
            pst.setString(2, SECTOR_POR_DEFECTO);
            pst.executeUpdate();
            try (ResultSet rs = pst.getGeneratedKeys()) {
                rs.next();
                return rs.getLong(1);
            }
        }
    }

    /**
     * Comprueba si ya hay una oferta con ese título en esa empresa.
     *
     * @param conn      conexión de la transacción en curso
     * @param titulo    título de la oferta
     * @param idEmpresa id de la empresa
     * @return {@code true} si la oferta ya existe
     * @throws SQLException si falla la consulta
     */
    private boolean existeOferta(Connection conn, String titulo, long idEmpresa) throws SQLException {
        String consulta = "select count(*) from Oferta where titulo = ? and id_empresa = ?";
        try (PreparedStatement pst = conn.prepareStatement(consulta)) {
            pst.setString(1, titulo);
            pst.setLong(2, idEmpresa);
            try (ResultSet rs = pst.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    /**
     * Inserta una oferta asociada a la empresa indicada.
     *
     * @param conn      conexión de la transacción en curso
     * @param oferta    oferta a insertar
     * @param idEmpresa id de la empresa de la oferta
     * @throws SQLException si falla la inserción
     */
    private void insertarOferta(Connection conn, Oferta oferta, long idEmpresa) throws SQLException {
        String insercion = "insert into Oferta (id_empresa, titulo, estado, fecha_postulacion, enlace, notas) "
                + "values (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pst = conn.prepareStatement(insercion)) {
            pst.setLong(1, idEmpresa);
            pst.setString(2, oferta.getTitulo());
            pst.setString(3, oferta.getEstado());
            pst.setDate(4, Date.valueOf(oferta.getFechaPostulacion()));
            pst.setString(5, oferta.getEnlace());
            pst.setString(6, oferta.getNotas());
            pst.executeUpdate();
        }
    }
}