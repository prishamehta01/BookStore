package com.bookstore;

import com.bookstore.controllers.BookController;
import com.bookstore.database.DatabaseConnection;

/**
 * Main application class for BookStore
 * This class initializes the database and starts the web server
 */
public class BookStoreApp {

    public static void main(String[] args) {
        System.out.println("🚀 Starting BookStore application...");

        try {
            // Initialize database connection and create tables if needed
            System.out.println("📡 Connecting to database...");
            DatabaseConnection.initializeDatabase();
            System.out.println("✅ Database initialized successfully!");

            // Initialize and start the web server with routes
            System.out.println("🌐 Starting web server...");
            BookController bookController = new BookController();
            bookController.initRoutes();

            System.out.println("✅ BookStore application is ready!");
            System.out.println("🌐 Frontend available at: http://localhost:8080");
            System.out.println("🔗 API available at: http://localhost:8080/api/books");
            System.out.println("📚 Open your browser and start managing books!");

            // Add shutdown hook to properly close database connection
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                System.out.println("\n🛑 Shutting down BookStore application...");
                DatabaseConnection.closeConnection();
                System.out.println("👋 BookStore application stopped.");
            }));

        } catch (Exception e) {
            System.err.println("❌ Failed to start BookStore application: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}