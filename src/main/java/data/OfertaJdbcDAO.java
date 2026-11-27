package data;
import models.Empresa;
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
 * Implementación de {@link DAO} para {@link Oferta} basada en JDBC.
 * Las consultas de lectura hacen un JOIN con la tabla {@code Empresa} para
 * devolver cada oferta con su empresa ya cargada.
 */
public class OfertaJdbcDAO implements DAO<Oferta> {

    /** Consulta base: oferta con los datos de su empresa. */
    private static final String SELECT_CON_EMPRESA =
            "select Oferta.*, Empresa.nombre, Empresa.sector, Empresa.web, Empresa.fecha_registro "
                    + "from Oferta join Empresa on Oferta.id_empresa = Empresa.id ";

    /** Origen de datos del que se obtienen las conexiones. */
    private final DataSource ds;

    /**
     * Crea el DAO.
     * @param ds origen de datos de la base de datos
     */
    public OfertaJdbcDAO(DataSource ds) {
        this.ds = ds;
    }

    @Override
    public Optional<Oferta> findById(Long id) {
        String sql = SELECT_CON_EMPRESA + "where Oferta.id=?";

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
    public List<Oferta> findAll() {
        String sql = SELECT_CON_EMPRESA + "order by Oferta.fecha_postulacion desc";
        List<Oferta> ofertas = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ofertas.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ofertas;
    }

    @Override
    public Oferta save(Oferta obj) {
        String sql = "insert into Oferta (id_empresa, titulo, descripcion, estado, "
                + "fecha_postulacion, enlace, salario, notas) values (?,?,?,?,?,?,?,?)";

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
    public void update(Oferta entity) {
        String sql = "update Oferta set id_empresa=?, titulo=?, descripcion=?, estado=?, "
                + "fecha_postulacion=?, enlace=?, salario=?, notas=? where id=?";

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            rellenar(pst, entity);
            pst.setLong(9, entity.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from Oferta where id=?";

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Rellena los ocho parámetros comunes del insert y del update.
     *
     * @param pst    sentencia a rellenar
     * @param oferta oferta de la que se toman los datos
     * @throws SQLException si falla la asignación de algún parámetro
     */
    private static void rellenar(PreparedStatement pst, Oferta oferta) throws SQLException {
        pst.setLong(1, oferta.getEmpresa().getId());
        pst.setString(2, oferta.getTitulo());
        pst.setString(3, oferta.getDescripcion());
        pst.setString(4, oferta.getEstado());
        pst.setObject(5, oferta.getFechaPostulacion());
        pst.setString(6, oferta.getEnlace());
        pst.setObject(7, oferta.getSalario());
        pst.setString(8, oferta.getNotas());
    }

    /**
     * Convierte la fila actual del JOIN en una {@link Oferta} con su {@link Empresa}.
     *
     * @param rs resultado posicionado en la fila a convertir
     * @return la oferta construida, con su empresa
     * @throws SQLException si falla la lectura de alguna columna
     */
    public static Oferta mapper(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setId(rs.getLong("id_empresa"));
        empresa.setNombre(rs.getString("nombre"));
        empresa.setSector(rs.getString("sector"));
        empresa.setWeb(rs.getString("web"));
        Date fechaRegistro = rs.getDate("fecha_registro");
        empresa.setFechaRegistro(fechaRegistro == null ? null : fechaRegistro.toLocalDate());

        Oferta oferta = new Oferta();
        oferta.setId(rs.getLong("id"));
        oferta.setEmpresa(empresa);
        oferta.setTitulo(rs.getString("titulo"));
        oferta.setDescripcion(rs.getString("descripcion"));
        oferta.setEstado(rs.getString("estado"));
        Date fecha = rs.getDate("fecha_postulacion");
        oferta.setFechaPostulacion(fecha == null ? null : fecha.toLocalDate());
        oferta.setEnlace(rs.getString("enlace"));
        double salario = rs.getDouble("salario");
        oferta.setSalario(rs.wasNull() ? null : salario);
        oferta.setNotas(rs.getString("notas"));
        return oferta;
    }
}