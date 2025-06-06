package com.bookstore.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database connection utility class for MySQL
 */
public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/bookstore_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "prisha@2005";

    private static Connection connection = null;

    /**
     * Get database connection
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Database connected successfully!");
            } catch (ClassNotFoundException e) {
                throw new SQLException("MySQL JDBC Driver not found", e);
            }
        }
        return connection;
    }

    /**
     * Initialize database tables (if needed)
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Check if books table exists, create if not
            String checkTable = "SHOW TABLES LIKE 'books'";
            var rs = stmt.executeQuery(checkTable);

            if (!rs.next()) {
                String createBooksTable = """
                    CREATE TABLE books (
                        id INT PRIMARY KEY AUTO_INCREMENT,
                        title VARCHAR(255) NOT NULL,
                        author VARCHAR(255) NOT NULL,
                        isbn VARCHAR(20) UNIQUE,
                        price DECIMAL(10,2) NOT NULL,
                        quantity INT NOT NULL DEFAULT 0,
                        description TEXT,
                        category VARCHAR(100),
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                        updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                    )
                """;
                stmt.executeUpdate(createBooksTable);
                System.out.println("Books table created successfully!");
            }

        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    /**
     * Close database connection
     */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}