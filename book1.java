import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** Represents one book in the Library Management System. */
public class book1 {
    private String bookId;
    private String title;
    private String author;
    private boolean available;
    private String borrowedBy; // Reader ID, or null if available

    public book1(String bookId, String title, String author, boolean available) {
        setBookId(bookId);
        setTitle(title);
        setAuthor(author);
        // A new book begins available; borrowing records the reader ID.
        this.available = true;
        this.borrowedBy = null;
        if (!available) {
            throw new IllegalArgumentException(
                    "Use markBorrowed(readerId) to record an unavailable book.");
        }
    }

    public String getBookId() { return bookId; }
    public void setBookId(String bookId) { this.bookId = requiredField(bookId, "Book ID"); }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = requiredField(title, "Title"); }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = requiredField(author, "Author"); }

    public boolean isAvailable() { return available; }
    public String getBorrowedBy() { return borrowedBy; }

    public void markBorrowed(String readerId) {
        if (!available) {
            throw new IllegalStateException("This book is already borrowed.");
        }
        this.borrowedBy = requiredField(readerId, "Reader ID");
        this.available = false;
    }

    public void markReturned() {
        if (available) {
            throw new IllegalStateException("This book is already available.");
        }
        this.available = true;
        this.borrowedBy = null;
    }

    // This simple text-file format deliberately disallows commas in fields.
    private static String requiredField(String value, String label) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(label + " cannot be empty.");
        }
        if (value.contains(",") || value.contains("\n") || value.contains("\r")) {
            throw new IllegalArgumentException(label + " cannot contain commas or line breaks.");
        }
        return value.trim();
    }

    // Save this book as one record; called after changes by the library controller.
    public void saveToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
            writer.write(bookId + "," + title + "," + author + "," + available
                    + "," + (borrowedBy == null ? "" : borrowedBy));
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving book: " + e.getMessage());
        }
    }

    // For a full library update, rewrite the complete list rather than appending.
    public static void saveAllToFile(String filename, List<book1> books) {
        if (books == null) {
            throw new IllegalArgumentException("Books list cannot be null.");
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (book1 book : books) {
                writer.write(book.bookId + "," + book.title + "," + book.author
                        + "," + book.available + ","
                        + (book.borrowedBy == null ? "" : book.borrowedBy));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving books: " + e.getMessage());
        }
    }

    public static List<book1> readFromFile(String filename) {
        List<book1> books = new ArrayList<>();
        Set<String> bookIds = new HashSet<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                // -1 preserves an empty last field for available books.
                String[] parts = line.split(",", -1);
                if (parts.length != 5 && parts.length != 4) {
                    System.out.println("Invalid record on line " + lineNumber);
                    continue;
                }

                String id = parts[0].trim();
                String title = parts[1].trim();
                String author = parts[2].trim();
                String availability = parts[3].trim();
                String borrower = parts.length == 5 ? parts[4].trim() : "";

                if (id.isEmpty() || title.isEmpty() || author.isEmpty()
                        || !(availability.equalsIgnoreCase("true")
                        || availability.equalsIgnoreCase("false"))) {
                    System.out.println("Invalid book data on line " + lineNumber);
                    continue;
                }

                boolean isAvailable = Boolean.parseBoolean(availability);
                if ((isAvailable && !borrower.isEmpty())
                        || (!isAvailable && borrower.isEmpty())) {
                    System.out.println("Inconsistent borrowing data on line " + lineNumber);
                    continue;
                }
                if (bookIds.contains(id)) {
                    System.out.println("Duplicate Book ID: " + id);
                    continue;
                }

                book1 book = new book1(id, title, author, true);
                if (!isAvailable) {
                    book.markBorrowed(borrower);
                }
                books.add(book);
                bookIds.add(id);
            }
        } catch (IOException e) {
            System.out.println("Error reading books: " + e.getMessage());
        }
        return books;
    }

    @Override
    public String toString() {
        return "Book ID: " + bookId + ", Title: " + title + ", Author: " + author
                + ", Status: " + (available ? "Available" : "Borrowed by " + borrowedBy);
    }
}
