/** Represents a registered reader in the Library Management System. */
public class reader1 {
    private int readerId;
    private String name;
    private String contactDetails;

    public reader1(int readerId, String name, String contactDetails) {
        setReaderId(readerId);
        setName(name);
        setContactDetails(contactDetails);
    }

    public int getReaderId() {
        return readerId;
    }

    public void setReaderId(int readerId) {
        if (readerId <= 0) {
            throw new IllegalArgumentException("Reader ID must be positive.");
        }
        this.readerId = readerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Reader name cannot be empty.");
        }
        this.name = name.trim();
    }

    public String getContactDetails() {
        return contactDetails;
    }

    public void setContactDetails(String contactDetails) {
        if (contactDetails == null || contactDetails.trim().isEmpty()) {
            throw new IllegalArgumentException("Contact details cannot be empty.");
        }
        this.contactDetails = contactDetails.trim();
    }

    @Override
    public String toString() {
        return "Reader ID: " + readerId + ", Name: " + name
                + ", Contact: " + contactDetails;
    }
}
