package com.bookstore.database;

import com.bookstore.models.Book;
import java.math.BigDecimal;
import java.util.List;

/**
 * Test class for BookDAO operations
 */
public class BookDAOTest {

    public static void main(String[] args) {
        BookDAO bookDAO = new BookDAO();

        try {
            System.out.println("Testing BookDAO operations...\n");

            // Test 1: Get all books
            System.out.println("📚 Test 1: Getting all books");
            List<Book> books = bookDAO.getAllBooks();
            System.out.println("Found " + books.size() + " books");
            for (Book book : books) {
                System.out.println("- " + book.getTitle() + " by " + book.getAuthor() + " ($" + book.getPrice() + ")");
            }

            // Test 2: Get book by ID
            System.out.println("\n📖 Test 2: Getting book with ID 1");
            Book book1 = bookDAO.getBookById(1);
            if (book1 != null) {
                System.out.println("Found: " + book1.getTitle() + " by " + book1.getAuthor());
            } else {
                System.out.println("Book not found");
            }

            // Test 3: Add new book
            System.out.println("\n➕ Test 3: Adding new book");
            Book newBook = new Book(
                    "Java Programming",
                    "John Doe",
                    "978-1234567890",
                    new BigDecimal("29.99"),
                    15,
                    "Learn Java programming",
                    "Programming"
            );

            Book savedBook = bookDAO.addBook(newBook);
            System.out.println("Added book with ID: " + savedBook.getId());

            // Test 4: Update book
            System.out.println("\n✏️ Test 4: Updating book");
            savedBook.setPrice(new BigDecimal("24.99"));
            savedBook.setQuantity(20);
            boolean updated = bookDAO.updateBook(savedBook);
            System.out.println("Update successful: " + updated);

            // Test 5: Delete book
            System.out.println("\n🗑️ Test 5: Deleting the added book");
            boolean deleted = bookDAO.deleteBook(savedBook.getId());
            System.out.println("Delete successful: " + deleted);

            System.out.println("\n✅ All BookDAO tests completed!");

        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
}