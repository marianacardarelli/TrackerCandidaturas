package data;

import com.mysql.cj.jdbc.MysqlDataSource;

import javax.sql.DataSource;

/**
 * Fábrica del {@link DataSource} de MySQL. Se crea una sola vez y se reutiliza.
 * Las credenciales se leen de las variables de entorno {@code USER} y
 * {@code PASSWORD}.
 */
public class DataSourceFactory {

    /** URL de conexión a la base de datos MySQL. */
    private static final String URL = "jdbc:mysql://localhost:3309/ad";

    /** Instancia única del {@link DataSource}. */
    private static DataSource dataSource = null;

    private DataSourceFactory() {
    }

    /**
     * Devuelve el {@link DataSource} compartido, creándolo si aún no existe.
     *
     * @return el {@link DataSource} configurado para MySQL
     * @throws RuntimeException si faltan las variables de entorno {@code USER} o {@code PASSWORD}
     */
    public static DataSource getDataSource() {
        if (dataSource == null) {
            String usuario = System.getenv("USER");
            String clave = System.getenv("PASSWORD");
            if (usuario == null || clave == null) {
                throw new RuntimeException("Las variables de entorno USER y PASSWORD son obligatorias");
            }
            MysqlDataSource ds = new MysqlDataSource();
            ds.setURL(URL);
            ds.setUser(usuario);
            ds.setPassword(clave);
            dataSource = ds;
        }
        return dataSource;
    }
}