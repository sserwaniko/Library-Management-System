// Project: Library Management System
// Group name: Group 15
// Group Leader: SSerwaniko Jovan Mubuuke

// Members: (Name, Registration number, Student Number)
// KISEMBO CHRISTIANA MICHELLE,  26/U/03536/PS,  2600703536
// OJEDE CLAIRE PRECIOUS,  26/U/03627/PS,  2600703627
// SSERWANIKO JOVAN MUBUUKE,  26/U/03650/PSA,  2600703650
// AKANDONDA PRAISE,  26/U/06318/PS,  2600706318
// SSEKITO ABDUL,  26/U/31398/PS,  2600731398


import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * Swing GUI for the Library Management System.
 * Uses book1 (books, file persistence) and reader1 (readers).
 * Run: javac *.java  then  java LibraryGUI
 */
public class LibraryGUI extends JFrame {
    private static final String BOOK_FILE = "books.txt";

    private List<book1> books = new ArrayList<>();
    private final List<reader1> readers = new ArrayList<>();

    private final DefaultTableModel bookModel = new DefaultTableModel(
            new String[]{"Book ID", "Title", "Author", "Status"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final DefaultTableModel readerModel = new DefaultTableModel(
            new String[]{"Reader ID", "Name", "Contact"}, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };

    private final JTable bookTable = new JTable(bookModel);
    private final JTable readerTable = new JTable(readerModel);
    private final JTextField searchField = new JTextField(20);
    private final JLabel statusBar = new JLabel(" Ready");

    public LibraryGUI() {
        super("Library Management System");
        loadData();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(820, 520);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Books", buildBooksPanel());
        tabs.addTab("Readers", buildReadersPanel());

        add(tabs, BorderLayout.CENTER);
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(statusBar, BorderLayout.SOUTH);

        refreshBooks(null);
        refreshReaders();
    }

    // ---------- Data ----------

    private void loadData() {
        books = book1.readFromFile(BOOK_FILE);
        if (books.isEmpty()) {
            books.add(new book1("B001", "Java Programming", "Ssekitto Abdul", true));
            books.add(new book1("B004", "Easing Programming", "O. Claire", true));
        }
        readers.add(new reader1(1, "Claire", "123-456-7890"));
    }

    private void saveBooks() {
        book1.saveAllToFile(BOOK_FILE, books);
    }

    private book1 findBook(String id) {
        for (book1 b : books) {
            if (b.getBookId().equalsIgnoreCase(id)) return b;
        }
        return null;
    }

    private reader1 findReader(int id) {
        for (reader1 r : readers) {
            if (r.getReaderId() == id) return r;
        }
        return null;
    }

    // ---------- Books tab ----------

    private JPanel buildBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Search bar
        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        top.add(new JLabel("Search (title, author or ID):"));
        top.add(searchField);
        JButton searchBtn = new JButton("Search");
        JButton showAllBtn = new JButton("Show All");
        top.add(searchBtn);
        top.add(showAllBtn);
        panel.add(top, BorderLayout.NORTH);

        bookTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookTable.setRowHeight(24);
        panel.add(new JScrollPane(bookTable), BorderLayout.CENTER);

        // Action buttons
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        JButton addBtn = new JButton("Add Book");
        JButton borrowBtn = new JButton("Borrow Selected");
        JButton returnBtn = new JButton("Return Selected");
        bottom.add(addBtn);
        bottom.add(borrowBtn);
        bottom.add(returnBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        searchBtn.addActionListener(e -> refreshBooks(searchField.getText()));
        searchField.addActionListener(e -> refreshBooks(searchField.getText()));
        showAllBtn.addActionListener(e -> {
            searchField.setText("");
            refreshBooks(null);
        });
        addBtn.addActionListener(e -> addBook());
        borrowBtn.addActionListener(e -> borrowSelected());
        returnBtn.addActionListener(e -> returnSelected());

        return panel;
    }

    private void refreshBooks(String keyword) {
        bookModel.setRowCount(0);
        String k = keyword == null ? "" : keyword.trim().toLowerCase();
        int shown = 0;
        for (book1 b : books) {
            if (k.isEmpty()
                    || b.getBookId().toLowerCase().contains(k)
                    || b.getTitle().toLowerCase().contains(k)
                    || b.getAuthor().toLowerCase().contains(k)) {
                String status = b.isAvailable() ? "Available" : "Borrowed by " + b.getBorrowedBy();
                bookModel.addRow(new Object[]{b.getBookId(), b.getTitle(), b.getAuthor(), status});
                shown++;
            }
        }
        statusBar.setText(" Showing " + shown + " of " + books.size() + " book(s)");
        if (shown == 0 && !k.isEmpty()) {
            statusBar.setText(" No matching books found.");
        }
    }

    private String selectedBookId() {
        int row = bookTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a book in the table first.",
                    "No selection", JOptionPane.INFORMATION_MESSAGE);
            return null;
        }
        return (String) bookModel.getValueAt(row, 0);
    }

    private void addBook() {
        JTextField idField = new JTextField();
        JTextField titleField = new JTextField();
        JTextField authorField = new JTextField();
        Object[] form = {"Book ID:", idField, "Title:", titleField, "Author:", authorField};

        int result = JOptionPane.showConfirmDialog(this, form, "Add Book",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            if (findBook(idField.getText().trim()) != null) {
                throw new IllegalArgumentException("A book with that ID already exists.");
            }
            book1 b = new book1(idField.getText(), titleField.getText(), authorField.getText(), true);
            books.add(b);
            saveBooks();
            refreshBooks(searchField.getText());
            statusBar.setText(" Added book " + b.getBookId());
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot add book",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrowSelected() {
        String id = selectedBookId();
        if (id == null) return;
        book1 b = findBook(id);
        if (b == null) return;

        if (!b.isAvailable()) {
            JOptionPane.showMessageDialog(this,
                    "This book is already borrowed by Reader ID: " + b.getBorrowedBy(),
                    "Unavailable", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String input = JOptionPane.showInputDialog(this, "Enter Reader ID borrowing this book:",
                "Borrow Book", JOptionPane.QUESTION_MESSAGE);
        if (input == null) return;

        try {
            int readerId = Integer.parseInt(input.trim());
            if (findReader(readerId) == null) {
                throw new IllegalArgumentException(
                        "Reader " + readerId + " is not registered. Register them in the Readers tab first.");
            }
            b.markBorrowed(String.valueOf(readerId));
            saveBooks();
            refreshBooks(searchField.getText());
            statusBar.setText(" " + b.getTitle() + " borrowed by Reader " + readerId);
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Reader ID must be a number.",
                    "Invalid input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot borrow",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void returnSelected() {
        String id = selectedBookId();
        if (id == null) return;
        book1 b = findBook(id);
        if (b == null) return;

        if (b.isAvailable()) {
            JOptionPane.showMessageDialog(this, "This book was not marked as borrowed.",
                    "Cannot return", JOptionPane.WARNING_MESSAGE);
            return;
        }
        try {
            b.markReturned();
            saveBooks();
            refreshBooks(searchField.getText());
            statusBar.setText(" " + b.getTitle() + " returned.");
        } catch (IllegalStateException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Cannot return",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Readers tab ----------

    private JPanel buildReadersPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        readerTable.setRowHeight(24);
        panel.add(new JScrollPane(readerTable), BorderLayout.CENTER);

        JButton registerBtn = new JButton("Register New Reader");
        registerBtn.addActionListener(e -> registerReader());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottom.add(registerBtn);
        panel.add(bottom, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshReaders() {
        readerModel.setRowCount(0);
        for (reader1 r : readers) {
            readerModel.addRow(new Object[]{r.getReaderId(), r.getName(), r.getContactDetails()});
        }
    }

    private void registerReader() {
        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField contactField = new JTextField();
        Object[] form = {"Reader ID (positive integer):", idField,
                "Name:", nameField, "Contact details:", contactField};

        int result = JOptionPane.showConfirmDialog(this, form, "Register Reader",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            int id = Integer.parseInt(idField.getText().trim());
            if (findReader(id) != null) {
                throw new IllegalArgumentException("A reader with that ID already exists.");
            }
            reader1 r = new reader1(id, nameField.getText(), contactField.getText());
            readers.add(r);
            refreshReaders();
            statusBar.setText(" Registered reader " + r.getName());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid ID format. Please enter a valid number.",
                    "Invalid input", JOptionPane.ERROR_MESSAGE);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    // ---------- Entry point ----------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LibraryGUI().setVisible(true));
    }
}
