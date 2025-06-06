package com.bookstore.controllers;

import com.bookstore.database.BookDAO;
import com.bookstore.models.Book;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonDeserializationContext;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static spark.Spark.*;

/**
 * Simple controller to handle book HTTP requests
 */
public class BookController {
    private final BookDAO bookDAO;
    private final Gson gson;

    public BookController() {
        this.bookDAO = new BookDAO();

        // Create Gson with LocalDateTime and BigDecimal adapters
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(java.math.BigDecimal.class, new BigDecimalAdapter())
                .create();
    }

    /**
     * Custom adapter for LocalDateTime serialization/deserialization
     */
    private static class LocalDateTimeAdapter implements JsonSerializer<LocalDateTime>, JsonDeserializer<LocalDateTime> {
        private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

        @Override
        public JsonElement serialize(LocalDateTime localDateTime, Type srcType, JsonSerializationContext context) {
            return localDateTime != null ? new JsonPrimitive(formatter.format(localDateTime)) : null;
        }

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return json != null && !json.isJsonNull() ? LocalDateTime.parse(json.getAsString(), formatter) : null;
        }
    }

    /**
     * Custom adapter for BigDecimal serialization/deserialization
     */
    private static class BigDecimalAdapter implements JsonSerializer<BigDecimal>, JsonDeserializer<BigDecimal> {
        @Override
        public JsonElement serialize(BigDecimal bigDecimal, Type srcType, JsonSerializationContext context) {
            return bigDecimal != null ? new JsonPrimitive(bigDecimal) : null;
        }

        @Override
        public BigDecimal deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) {
            return json != null && !json.isJsonNull() ? json.getAsBigDecimal() : null;
        }
    }

    /**
     * Initialize basic routes
     */
    public void initRoutes() {
        // Set port
        port(8080);

        // Serve static files from resources/public
        staticFiles.location("/public");
        staticFiles.expireTime(600); // 10 minutes cache

        // Enable CORS for frontend
        before((request, response) -> {
            response.header("Access-Control-Allow-Origin", "*");
            response.header("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
            response.header("Access-Control-Allow-Headers", "Content-Type");
        });

        // Set JSON content type only for API routes
        before("/api/*", (request, response) -> {
            response.type("application/json");
        });

        // Basic routes
        get("/", (req, res) -> "📚 BookStore API is running!");

        // Get all books
        get("/api/books", (req, res) -> {
            try {
                List<Book> books = bookDAO.getAllBooks();
                return gson.toJson(books);
            } catch (Exception e) {
                res.status(500);
                return "{\"error\": \"Failed to get books: " + e.getMessage() + "\"}";
            }
        });

        // Get book by ID
        get("/api/books/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                Book book = bookDAO.getBookById(id);

                if (book != null) {
                    return gson.toJson(book);
                } else {
                    res.status(404);
                    return "{\"error\": \"Book not found\"}";
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid book ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"error\": \"Database error: " + e.getMessage() + "\"}";
            }
        });

        // Add new book
        post("/api/books", (req, res) -> {
            try {
                Book book = gson.fromJson(req.body(), Book.class);
                Book savedBook = bookDAO.addBook(book);  // Returns Book, not boolean

                if (savedBook != null && savedBook.getId() > 0) {
                    res.status(201);
                    return gson.toJson(savedBook);  // Return the saved book with ID
                } else {
                    res.status(500);
                    return "{\"error\": \"Failed to add book\"}";
                }
            } catch (Exception e) {
                res.status(500);
                return "{\"error\": \"Failed to add book: " + e.getMessage() + "\"}";
            }
        });

        // Update book
        put("/api/books/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                Book book = gson.fromJson(req.body(), Book.class);
                book.setId(id);

                boolean success = bookDAO.updateBook(book);

                if (success) {
                    return "{\"message\": \"Book updated successfully\"}";
                } else {
                    res.status(404);
                    return "{\"error\": \"Book not found\"}";
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid book ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"error\": \"Failed to update book: " + e.getMessage() + "\"}";
            }
        });

        // Delete book
        delete("/api/books/:id", (req, res) -> {
            try {
                int id = Integer.parseInt(req.params(":id"));
                boolean success = bookDAO.deleteBook(id);

                if (success) {
                    return "{\"message\": \"Book deleted successfully\"}";
                } else {
                    res.status(404);
                    return "{\"error\": \"Book not found\"}";
                }
            } catch (NumberFormatException e) {
                res.status(400);
                return "{\"error\": \"Invalid book ID\"}";
            } catch (Exception e) {
                res.status(500);
                return "{\"error\": \"Failed to delete book: " + e.getMessage() + "\"}";
            }
        });

        System.out.println("🚀 BookStore API started at http://localhost:8080");
        System.out.println("📖 Try: http://localhost:8080/api/books");
    }
}