package com.routezen.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Database Connection Utility for Route Zen
 * Manages MySQL database connectivity using JDBC
 * 
 * @author Route Zen Team
 * @version 1.0
 */
public class DatabaseConnection {

    // Database Configuration
    private static final String DB_URL = "jdbc:mysql://localhost:3306/routezen_db";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "your_password";
    private static final String DB_DRIVER = "com.mysql.cj.jdbc.Driver";

    // Static block to load MySQL Driver
    static {
        try {
            Class.forName(DB_DRIVER);
            System.out.println("✓ MySQL JDBC Driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("✗ MySQL JDBC Driver not found!");
            e.printStackTrace();
            throw new RuntimeException("Failed to load MySQL driver", e);
        }
    }

    /**
     * Establishes and returns a database connection
     * 
     * @return Connection object to the database
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            System.out.println("✓ Database connection established");
            return conn;
        } catch (SQLException e) {
            System.err.println("✗ Failed to connect to database!");
            throw e;
        }
    }

    /**
     * Safely closes the database connection
     * 
     * @param conn Connection object to close
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("✓ Database connection closed");
            } catch (SQLException e) {
                System.err.println("✗ Error closing connection");
                e.printStackTrace();
            }
        }
    }

    /**
     * Tests the database connection
     * 
     * @return true if connection is successful, false otherwise
     */
    public static boolean testConnection() {
        try (Connection conn = getConnection()) {
            return conn != null && !conn.isClosed();
        } catch (SQLException e) {
            return false;
        }
    }
}
