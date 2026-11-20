package data;
import models.Empresa;
import models.Entrevista;
import models.Oferta;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de {@link DAO} para {@link Entrevista} basada en JDBC.
 * Las consultas de lectura hacen JOIN con {@code Oferta} y {@code Empresa}
 * para que cada entrevista lleve el título de su oferta y el nombre de su empresa.
 */
public class EntrevistaJdbcDAO implements DAO<Entrevista> {

    /** Consulta base: entrevista con el título de su oferta y el nombre de su empresa. */
    private static final String SELECT_CON_OFERTA =
            "select Entrevista.*, Oferta.titulo, Empresa.nombre "
                    + "from Entrevista "
                    + "join Oferta on Entrevista.id_oferta = Oferta.id "
                    + "join Empresa on Oferta.id_empresa = Empresa.id ";

    /** Origen de datos del que se obtienen las conexiones. */
    private final DataSource ds;

    /**
     * Crea el DAO.
     *
     * @param ds origen de datos de la base de datos
     */
    public EntrevistaJdbcDAO(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Optional<Entrevista> findById(Long id) {
        String sql = SELECT_CON_OFERTA + "where Entrevista.id=?";

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return Optional.empty();
    }

    @Override
    public List<Entrevista> findAll() {
        String sql = SELECT_CON_OFERTA + "order by Entrevista.fecha desc";
        List<Entrevista> entrevistas = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                entrevistas.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entrevistas;
    }

    /**
     * Devuelve las entrevistas de una oferta, de la más reciente a la más antigua.
     *
     * @param idOferta identificador de la oferta
     * @return lista de entrevistas de esa oferta
     */
    public List<Entrevista> findByOfertaId(Long idOferta) {
        String sql = SELECT_CON_OFERTA + "where Entrevista.id_oferta=? order by Entrevista.fecha desc";
        List<Entrevista> entrevistas = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, idOferta);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    entrevistas.add(mapper(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return entrevistas;
    }

    @Override
    public Entrevista save(Entrevista obj) {
        String sql = "insert into Entrevista (id_oferta, fecha, tipo, notas, resultado) "
                + "values (?,?,?,?,?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            rellenar(pst, obj);
            pst.executeUpdate();

            try (ResultSet rs = pst.getGeneratedKeys()) {
                if (rs.next()) {
                    obj.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return obj;
    }

    @Override
    public void update(Entrevista entity) {
        String sql = "update Entrevista set id_oferta=?, fecha=?, tipo=?, notas=?, resultado=? "
                + "where id=?";

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            rellenar(pst, entity);
            pst.setLong(6, entity.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from Entrevista where id=?";

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Rellena los cinco parámetros comunes del insert y del update.
     */
    private static void rellenar(PreparedStatement pst, Entrevista entrevista) throws SQLException {
        pst.setLong(1, entrevista.getOferta().getId());
        pst.setObject(2, entrevista.getFecha());
        pst.setString(3, entrevista.getTipo());
        pst.setString(4, entrevista.getNotas());
        pst.setString(5, entrevista.getResultado());
    }

    /**
     * Convierte la fila actual del JOIN en una {@link Entrevista}. Su oferta
     * solo trae el id, el título y la empresa (con su nombre).
     *
     * @param rs resultado posicionado en la fila a convertir
     * @return la entrevista construida
     * @throws SQLException si falla la lectura de alguna columna
     */
    public static Entrevista mapper(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setNombre(rs.getString("nombre"));

        Oferta oferta = new Oferta();
        oferta.setId(rs.getLong("id_oferta"));
        oferta.setTitulo(rs.getString("titulo"));
        oferta.setEmpresa(empresa);

        Entrevista entrevista = new Entrevista();
        entrevista.setId(rs.getLong("id"));
        entrevista.setOferta(oferta);
        Date fecha = rs.getDate("fecha");
        entrevista.setFecha(fecha == null ? null : fecha.toLocalDate());
        entrevista.setTipo(rs.getString("tipo"));
        entrevista.setNotas(rs.getString("notas"));
        entrevista.setResultado(rs.getString("resultado"));
        return entrevista;
    }
}