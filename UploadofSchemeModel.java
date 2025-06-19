import java.io.File;
import java.io.FileInputStream; // For reading file bytes to upload to DB
import java.io.IOException;     // For file I/O errors
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

// Assuming you have a DatabaseConnection utility class for database connectivity.
// For example:
/*
public class DatabaseConnection {
    private static final String DB_URL = "jdbc:sqlite:schemes.db"; // Example for SQLite
    // Or for MySQL: "jdbc:mysql://localhost:3306/your_database"
    private static final String USER = "your_username"; // For MySQL/PostgreSQL
    private static final String PASSWORD = "your_password"; // For MySQL/PostgreSQL

    public static Connection getConnection() throws SQLException {
        // Load the JDBC driver (only needed for older JDBC versions or specific drivers)
        // try {
        //     Class.forName("org.sqlite.JDBC"); // For SQLite
        //     // Class.forName("com.mysql.cj.jdbc.Driver"); // For MySQL
        // } catch (ClassNotFoundException e) {
        //     e.printStackTrace();
        // }
        return DriverManager.getConnection(DB_URL, USER, PASSWORD); // Adjust for SQLite if no user/pass needed
    }
}
*/

/**
 * The Model in the MVC pattern for the file upload feature.
 * It manages the state (selected semester, selected file) and
 * contains the business logic for validating file extensions and
 * persisting the file data to a database.
 * It has no knowledge of the UI.
 */
public class UploadofSchemeModel {

    private String selectedSemester; // Data: The semester chosen by the user
    private File selectedFile;       // Data: The file chosen by the user

    /**
     * No-argument constructor. The model's state is set by controller methods.
     */
    public UploadofSchemeModel() {
        // Model initialization logic if any (e.g., loading initial data)
    }

    // --- Getter and Setter methods for the Model's data ---

    public String getSelectedSemester() {
        return this.selectedSemester;
    }

    /**
     * Sets the semester chosen by the user. This data is updated by the Controller.
     * @param selectedSemester The chosen semester string.
     */
    public void setSelectedSemester(String selectedSemester) {
        this.selectedSemester = selectedSemester;
    }

    public File getSelectedFile() {
        return this.selectedFile;
    }

    /**
     * Sets the file chosen by the user. This data is updated by the Controller.
     * @param selectedFile The chosen File object.
     */
    public void setSelectedFile(File selectedFile) {
        this.selectedFile = selectedFile;
    }

    /**
     * Business Logic: Validates if the selected file has an allowed extension.
     * The Controller calls this method to determine if the upload can proceed.
     * @return true if the file extension is .txt or .dox, false otherwise or if no file is selected.
     */
    public boolean isValidFileExtension() {
        if (this.selectedFile == null) {
            return false; // No file selected, so it's not valid
        }
        String fileName = this.selectedFile.getName();
        // Check if filename ends with .txt or .dox (case-insensitive)
        return fileName.toLowerCase().endsWith(".txt") || fileName.toLowerCase().endsWith(".dox");
    }

    /**
     * Business Logic: Performs the core operation of uploading the scheme.
     * This involves connecting to a database and inserting the file's binary content.
     * The Controller calls this method to trigger the upload process.
     * @return true if the upload to the database is successful, false otherwise.
     */
    public boolean uploadScheme() {
        // Model performs its own internal validation before proceeding with heavy operations
        if (this.selectedFile == null) {
            System.err.println("Model Error: No file selected for upload.");
            return false;
        }
        if (this.selectedSemester == null || "--Select--".equals(this.selectedSemester)) {
            System.err.println("Model Error: No semester selected for upload.");
            return false;
        }
        if (!this.isValidFileExtension()) {
            System.err.println("Model Error: Invalid file type for upload.");
            return false;
        }

        Connection conn = null;
        PreparedStatement pstmt = null;
        FileInputStream fis = null; // Used to read the file's binary content

        try {
            // 1. Establish database connection (delegated to DatabaseConnection utility)
            conn = DatabaseConnection.getConnection();

            if (conn == null) {
                System.err.println("Model Error: Database connection could not be established.");
                return false;
            }

            // 2. Prepare to read the file's content as a binary stream
            fis = new FileInputStream(this.selectedFile);

            // 3. Define the SQL INSERT statement.
            //    Assumes a table 'schemes' with columns:
            //    - semester (VARCHAR/TEXT)
            //    - file_name (VARCHAR/TEXT)
            //    - upload_date (TIMESTAMP)
            //    - file_content (BLOB/BYTEA)
            String sql = "INSERT INTO schemes (semester, file_name, upload_date, file_content) VALUES (?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);

            // 4. Set parameters for the PreparedStatement
            pstmt.setString(1, this.selectedSemester);
            pstmt.setString(2, this.selectedFile.getName());
            pstmt.setTimestamp(3, new Timestamp(System.currentTimeMillis())); // Current upload time
            // Set the file's content as a binary stream (BLOB)
            pstmt.setBinaryStream(4, fis, (int) this.selectedFile.length());

            // 5. Execute the SQL update
            int rowsAffected = pstmt.executeUpdate();

            // 6. Check if the insertion was successful
            if (rowsAffected > 0) {
                System.out.println("Model Success: Scheme details and file content inserted into database successfully.");
                return true; // Report success to the Controller
            } else {
                System.err.println("Model Failed: Database insertion failed - No rows affected.");
                return false; // Report failure to the Controller
            }

        } catch (SQLException e) {
            System.err.println("Model SQL Error during scheme upload: " + e.getMessage());
            e.printStackTrace();
            return false; // Report failure due to SQL error
        } catch (IOException e) {
            System.err.println("Model File I/O Error during scheme upload: " + e.getMessage());
            e.printStackTrace();
            return false; // Report failure due to file reading error
        } finally {
            // 7. Ensure resources are closed in a finally block to prevent leaks
            try {
                if (fis != null) fis.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Model Error closing database resources: " + e.getMessage());
                e.printStackTrace();
            } catch (IOException e) {
                System.err.println("Model Error closing file input stream: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}