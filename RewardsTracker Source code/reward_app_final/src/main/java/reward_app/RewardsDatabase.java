/**
 * RewardsDatabase - Data access layer for the Rewards Tracker system.
 * Manages SQLite database connection and schema creation.
 * Provides connection instance for database operations.
 */
package reward_app;

import java.sql.*;

public class RewardsDatabase {
    private Connection connection;
    private static final String DB_NAME = "rewards.db";

    /**
     * Initializes database connection and ensures schema exists.
     * Creates new database file if it doesn't exist.
     */
    public RewardsDatabase() {
        createDatabase();
    }

    /**
     * Establishes connection to SQLite database.
     * Creates new database file if it doesn't exist.
     * Loads SQLite JDBC driver and initializes connection.
     */
    private void createDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + DB_NAME);
            createTable();
        } catch (Exception e) {
            System.out.println("Error connecting to database: " + e.getMessage());
        }
    }

    /**
     * Creates rewards table if it doesn't exist.
     * Table schema:
     * - phone: TEXT PRIMARY KEY (customer's phone number)
     * - points: INTEGER (customer's current point balance)
     */
    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS rewards "
                  + "(phone TEXT PRIMARY KEY, "
                  + "points INTEGER)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            System.out.println("Error creating table: " + e.getMessage());
        }
    }

    /**
     * Provides access to database connection.
     * @return Active database connection instance
     */
    public Connection getConnection() {
        return connection;
    }
}