package data;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

/**
 * Crea las tablas y carga los datos iniciales a partir de los scripts
 * {@code schema.sql} y {@code data.sql} de la carpeta de recursos.
 */
public class DataBaseInitializer {

    private DataBaseInitializer() {
    }

    /**
     * Crea las tablas si no existen. Usa el script {@code schema.sql}.
     *
     * @param ds origen de datos de la base de datos
     */
    public static void initialize(DataSource ds) {
        executeScript(ds, "schema.sql");
    }

    /**
     * Carga los datos iniciales solo si la tabla Empresa está vacía.
     * Usa el script {@code data.sql}.
     *
     * @param ds origen de datos de la base de datos
     */
    public static void loadData(DataSource ds) {
        String countSql = "select count(*) from Empresa";

        try (Connection conn = ds.getConnection();
             PreparedStatement pst = conn.prepareStatement(countSql);
             ResultSet rs = pst.executeQuery()) {
            if (rs.next() && rs.getInt(1) == 0) {
                executeScript(ds, "data.sql");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private static void executeScript(DataSource ds, String resourceName) {
        try (InputStream is = DataBaseInitializer.class.getClassLoader().getResourceAsStream(resourceName)) {
            if (is == null) {
                throw new RuntimeException("No se encontró el recurso " + resourceName);
            }

            String sql;
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(is, StandardCharsets.UTF_8))) {
                sql = reader.lines().collect(Collectors.joining("\n"));
            }

            try (Connection conn = ds.getConnection();
                 Statement st = conn.createStatement()) {
                for (String sentencia : sql.split(";")) {
                    if (!sentencia.isBlank()) {
                        st.execute(sentencia);
                    }
                }
            }
        } catch (IOException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}