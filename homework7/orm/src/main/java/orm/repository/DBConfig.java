package orm.repository;

import orm.core.OrmException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DBConfig {

    public static final String DEFAULT_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1";
    public static final String DEFAULT_USER = "sa";
    public static final String DEFAULT_PASSWORD = "";

    private DBConfig() {
    }

    public static Connection getConnection() {
        return getConnection(DEFAULT_URL, DEFAULT_USER, DEFAULT_PASSWORD);
    }

    public static Connection getConnection(String url, String user, String password) {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            throw new OrmException("Failed to open JDBC connection to " + url, e);
        }
    }
}
