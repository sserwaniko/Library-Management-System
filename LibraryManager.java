import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * LibraryManager for Claire (Library Operations)
 * Handles book and reader arrays/lists, searching, borrowing, and returning.
 */
public class LibraryManager {
    private static List<book1> books = new ArrayList<>();
    private static List<reader1> readers = new ArrayList<>();
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        // 1. Load initial books from file (or fallback if file isn't found)
        books = book1.readFromFile("books.txt");
        if (books.isEmpty()) {
            System.out.println("No books loaded from books.txt. Adding default sample books.");
            books.add(new book1("B001", "Java Programming", "Ssekitto Abdul", true));
            books.add(new book1("B004", "Easing Programming", "O. Claire", true));
        }

        // Register a sample reader for testing operations
        try {
            readers.add(new reader1(1, "Claire", "123-456-7890"));
        } catch (IllegalArgumentException e) {
            System.out.println("Error initializing reader: " + e.getMessage());
        }

        boolean running = true;
        while (running) {
            System.out.println("\n===== LIBRARY OPERATIONS =====");
            System.out.println("1. View All Books");
            System.out.println("2. Register New Reader");
            System.out.println("3. Search for a Book");
            System.out.println("4. Borrow a Book");
            System.out.println("5. Return a Book");
            System.out.println("6. Exit");
            System.out.print("Choose an option (1-6): ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("1")) {
                viewAllBooks();
            } else if (choice.equals("2")) {
                registerReader();
            } else if (choice.equals("3")) {
                searchBook();
            } else if (choice.equals("4")) {
                borrowBook();
            } else if (choice.equals("5")) {
                returnBook();
            } else if (choice.equals("6")) {
                running = false;
                System.out.println("Exiting Library Manager. Goodbye!");
            } else {
                System.out.println("Invalid option. Please enter a number between 1 and 6.");
            }
        }
        scanner.close();
    }

    private static void viewAllBooks() {
        System.out.println("\n--- Library Books ---");
        if (books.isEmpty()) {
            System.out.println("No books available.");
        } else {
            for (book1 b : books) {
                System.out.println(b);
            }
        }
    }

    private static void registerReader() {
        System.out.println("\n--- Register Reader ---");
        try {
            System.out.print("Enter Reader ID (positive integer): ");
            int id = Integer.parseInt(scanner.nextLine().trim());

            System.out.print("Enter Reader Name: ");
            String name = scanner.nextLine();

            System.out.print("Enter Contact Details: ");
            String contact = scanner.nextLine();

            reader1 newReader = new reader1(id, name, contact);
            readers.add(newReader);
            System.out.println("Successfully registered: " + newReader);
        } catch (NumberFormatException e) {
            System.out.println("Invalid ID format. Please enter a valid number.");
        } catch (IllegalArgumentException e) {
            System.out.println("Registration failed: " + e.getMessage());
        }
    }

    private static void searchBook() {
        System.out.println("\n--- Search For a Book ---");
        System.out.print("Enter keyword (Title, Author, or Book ID): ");
        String keyword = scanner.nextLine().toLowerCase().trim();

        boolean found = false;
        for (book1 b : books) {
            if (b.getBookId().toLowerCase().contains(keyword) ||
                b.getTitle().toLowerCase().contains(keyword) ||
                b.getAuthor().toLowerCase().contains(keyword)) {
                System.out.println(b);
                found = true;
            }
        }
        if (!found) {
            System.out.println("No matching books found.");
        }
    }

    private static void borrowBook() {
        System.out.println("\n--- Borrow a Book ---");
        System.out.print("Enter Book ID to borrow: ");
        String bookId = scanner.nextLine().trim();

        book1 targetBook = null;
        for (book1 b : books) {
            if (b.getBookId().equalsIgnoreCase(bookId)) {
                targetBook = b;
                break;
            }
        }

        if (targetBook == null) {
            System.out.println("Book ID not found.");
            return;
        }

        if (!targetBook.isAvailable()) {
            System.out.println("Sorry, this book is already borrowed by Reader ID: " + targetBook.getBorrowedBy());
            return;
        }

        System.out.print("Enter Reader ID borrowing this book: ");
        String readerId = scanner.nextLine().trim();

        try {
            targetBook.markBorrowed(readerId);
            book1.saveAllToFile("books.txt", books); // Persist update
            System.out.println("Book successfully borrowed!");
        } catch (IllegalStateException | IllegalArgumentException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    private static void returnBook() {
        System.out.println("\n--- Return a Book ---");
        System.out.print("Enter Book ID to return: ");
        String bookId = scanner.nextLine().trim();

        book1 targetBook = null;
        for (book1 b : books) {
            if (b.getBookId().equalsIgnoreCase(bookId)) {
                targetBook = b;
                break;
            }
        }

        if (targetBook == null) {
            System.out.println("Book ID not found.");
            return;
        }

        if (targetBook.isAvailable()) {
            System.out.println("This book was not marked as borrowed.");
            return;
        }

        try {
            targetBook.markReturned();
            book1.saveAllToFile("books.txt", books); // Persist update
            System.out.println("Book successfully returned!");
        } catch (IllegalStateException e) {
            System.out.println("Error returning book: " + e.getMessage());
        }
    }
}