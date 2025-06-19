import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.File; // Used for JFileChooser, but the View itself doesn't process the file content

/**
 * The View in the MVC pattern for the file upload feature.
 * It is solely responsible for rendering the UI and collecting user input.
 * It exposes methods for the Controller to interact with its components and
 * update the display. It has no application logic or direct data manipulation.
 */
public class UploadofSchemeView extends JFrame {

    private CardLayout cardLayout; // To switch between different panels
    private JPanel mainPanel;     // The main panel using CardLayout

    // --- Components for the Semester Selection Panel ---
    private JComboBox<String> semesterComboBox;
    private JButton nextButton;

    // --- Components for the Upload Panel ---
    private JLabel fileLabel;      // Displays the name of the selected file
    private JButton browseButton;
    private JButton uploadButton;
    private JButton backButton;

    /**
     * Constructor for the View.
     * Initializes the UI components and sets up the layout.
     * The Controller will attach listeners to these components.
     */
    public UploadofSchemeView() {
        setTitle("Upload Scheme of Study");
        setSize(450, 350); // Increased height slightly
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add the different UI panels (views) to the CardLayout
        mainPanel.add(createSemesterSelectionPanel(), "SemesterSelection");
        mainPanel.add(createUploadPanel(), "UploadPanel");

        add(mainPanel); // Add the main panel to the JFrame

        // Start by showing the semester selection panel
        showSemesterSelection();
    }

    /**
     * Creates and returns the JPanel for semester selection.
     * This is a UI construction method.
     */
    private JPanel createSemesterSelectionPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Padding
        gbc.fill = GridBagConstraints.HORIZONTAL; // Components fill their display area

        JLabel titleLabel = new JLabel("Select Semester for Upload", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2; // Span two columns
        panel.add(titleLabel, gbc);

        JLabel promptLabel = new JLabel("Semester Type:", SwingConstants.RIGHT);
        promptLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1; // Back to one column
        panel.add(promptLabel, gbc);

        semesterComboBox = new JComboBox<>(new String[]{"--Select--", "Spring", "Fall"});
        semesterComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        gbc.gridx = 1;
        gbc.gridy = 1;
        panel.add(semesterComboBox, gbc);

        nextButton = createStyledButton("Next");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.ipadx = 50; // Make button wider
        gbc.ipady = 10; // Make button taller
        panel.add(nextButton, gbc);

        return panel;
    }

    /**
     * Creates and returns the JPanel for file upload.
     * This is a UI construction method.
     */
    private JPanel createUploadPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255)); // Alice Blue
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Upload Scheme File", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(titleLabel, gbc);

        fileLabel = new JLabel("No file selected", SwingConstants.CENTER);
        fileLabel.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        fileLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        fileLabel.setPreferredSize(new Dimension(300, 30)); // Make the label wider
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        panel.add(fileLabel, gbc);

        browseButton = createStyledButton("Browse File");
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.ipadx = 50;
        gbc.ipady = 10;
        panel.add(browseButton, gbc);

        uploadButton = createStyledButton("Upload Scheme");
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.ipadx = 50;
        gbc.ipady = 10;
        panel.add(uploadButton, gbc);

        backButton = createStyledButton("Back");
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.ipadx = 50;
        gbc.ipady = 10;
        panel.add(backButton, gbc);

        return panel;
    }

    // --- Public methods for the Controller to interact with the View ---

    /**
     * Returns a reference to the semester selection combo box.
     * The Controller uses this to get the user's selected semester.
     */
    public JComboBox<String> getSemesterComboBox() {
        return semesterComboBox;
    }

    /**
     * Returns a reference to the "Next" button.
     * The Controller attaches an ActionListener to this button.
     */
    public JButton getNextButton() {
        return nextButton;
    }

    /**
     * Returns a reference to the "Browse File" button.
     * The Controller attaches an ActionListener to this button.
     */
    public JButton getBrowseButton() {
        return browseButton;
    }

    /**
     * Returns a reference to the "Upload Scheme" button.
     * The Controller attaches an ActionListener to this button.
     */
    public JButton getUploadButton() {
        return uploadButton;
    }

    /**
     * Returns a reference to the "Back" button on the upload panel.
     * The Controller attaches an ActionListener to this button.
     */
    public JButton getBackButton() {
        return backButton;
    }

    /**
     * Sets the text displayed on the file label.
     * The Controller calls this to update the UI after a file is selected or cleared.
     * @param text The text to display (e.g., file name or "No file selected").
     */
    public void setFileLabel(String text) {
        fileLabel.setText(text);
    }

    /**
     * Switches the main panel to the file upload panel.
     * The Controller calls this to change the visible UI state.
     */
    public void showUploadPanel() {
        cardLayout.show(mainPanel, "UploadPanel");
    }

    /**
     * Switches the main panel back to the semester selection panel.
     * The Controller calls this for navigation and to reset the UI.
     */
    public void showSemesterSelection() {
        cardLayout.show(mainPanel, "SemesterSelection");
        // Reset UI elements when going back to initial state
        setFileLabel("No file selected");
        semesterComboBox.setSelectedIndex(0); // Reset combo box to "--Select--"
    }

    /**
     * Displays an error message dialog to the user.
     * The Controller calls this when an error occurs in the Model or validation.
     * @param message The error message to display.
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays a success message dialog to the user.
     * The Controller calls this when an operation (like upload) is successful.
     * @param message The success message to display.
     */
    public void showSuccessMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Helper method to create a consistently styled JButton.
     * This is a UI styling responsibility of the View.
     */
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(new LineBorder(new Color(50, 90, 120), 1));
        return button;
    }

    /**
     * Main method to start the application.
     * It ensures the GUI is created and updated on the Event Dispatch Thread (EDT).
     * The View initiates the creation of the MVC components.
     */
    public static void main(String[] args) {
        // Ensure GUI updates are performed on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            // 1. Instantiate the Model
            UploadofSchemeModel model = new UploadofSchemeModel();
            // 2. Instantiate the View (which is 'this' in a non-static context, but here we create a new instance)
            UploadofSchemeView view = new UploadofSchemeView();
            // 3. Instantiate the Controller, passing references to the Model and View.
            // This establishes the communication links.
            new UploadofSchemeController(model, view); // The controller wires up listeners
            // 4. Make the View visible to the user.
            view.setVisible(true);
        });
    }
}