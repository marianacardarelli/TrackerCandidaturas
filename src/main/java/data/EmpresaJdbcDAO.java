package data;
import models.Empresa;
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
 * Implementación de {@link DAO} para {@link Empresa} basada en JDBC.
 * Accede a la tabla {@code Empresa} a través del {@link DataSource} recibido.
 */
public class EmpresaJdbcDAO implements DAO<Empresa> {
    /** Origen de datos del que se obtienen las conexiones. */
    private final DataSource ds;
    /**
     * Crea el DAO.
     * @param ds origen de datos de la base de datos
     */
    public EmpresaJdbcDAO(DataSource ds) {this.ds = ds;}

    @Override
    public Optional<Empresa> findById(Long id) {
        String sql = "select * from Empresa where id=?";
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
    public List<Empresa> findAll() {
        String sql = "select * from Empresa order by nombre";
        List<Empresa> empresas = new ArrayList<>();

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                empresas.add(mapper(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return empresas;
    }

    @Override
    public Empresa save(Empresa obj) {
        String sql = "insert into Empresa (nombre, sector, web) values (?,?,?)";

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setString(1, obj.getNombre());
            pst.setString(2, obj.getSector());
            pst.setString(3, obj.getWeb());
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
    public void update(Empresa entity) {
        String sql = "update Empresa set nombre=?, sector=?, web=? where id=?";
        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setString(1, entity.getNombre());
            pst.setString(2, entity.getSector());
            pst.setString(3, entity.getWeb());
            pst.setLong(4, entity.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "delete from Empresa where id=?";
        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {
            pst.setLong(1, id);
            pst.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Convierte la fila actual de un {@link ResultSet} en una {@link Empresa}.
     * @param rs resultado posicionado en la fila a convertir
     * @return la empresa construida a partir de la fila
     * @throws SQLException si falla la lectura de alguna columna
     */
    public static Empresa mapper(ResultSet rs) throws SQLException {
        Empresa empresa = new Empresa();
        empresa.setId(rs.getLong("id"));
        empresa.setNombre(rs.getString("nombre"));
        empresa.setSector(rs.getString("sector"));
        empresa.setWeb(rs.getString("web"));
        Date fecha = rs.getDate("fecha_registro");
        empresa.setFechaRegistro(fecha == null ? null : fecha.toLocalDate());
        return empresa;
    }
}