package com.bookstore.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Simple test class to verify database connection
 */
public class DatabaseTest {

    public static void main(String[] args) {
        System.out.println("Testing database connection...");

        try {
            // Test connection
            Connection conn = DatabaseConnection.getConnection();
            System.out.println("✅ Database connection successful!");

            // Test query - count books in database
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as book_count FROM books");

            if (rs.next()) {
                int bookCount = rs.getInt("book_count");
                System.out.println("📚 Found " + bookCount + " books in database");
            }

            // Test sample data
            rs = stmt.executeQuery("SELECT title, author FROM books LIMIT 3");
            System.out.println("\n📖 Sample books:");
            while (rs.next()) {
                System.out.println("- " + rs.getString("title") + " by " + rs.getString("author"));
            }

            rs.close();
            stmt.close();

        } catch (Exception e) {
            System.err.println("❌ Database connection failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}