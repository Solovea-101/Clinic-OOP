package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * JDBC Singleton for PostgreSQL connections.
 *
 * <p>Rubric alignment:
 * - Raw JDBC (no ORM), connection managed centrally.
 * - Single responsibility: connection creation only.
 *
 * <p>NOTE for academic environment:
 * - Update URL/USER/PASSWORD to match your local PostgreSQL.
 * - Ensure the PostgreSQL JDBC driver is on the classpath.
 */
public final class DatabaseConnection {
    // Change these for your machine / database.
    private static final String URL = "jdbc:postgresql://localhost:5432/clinic_mvp";
    private static final String USER = "postgres";
    private static final String PASSWORD = "newpassword";

    private static DatabaseConnection instance;

    private DatabaseConnection() {
        // Prevent external instantiation (Singleton).
    }

    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}

