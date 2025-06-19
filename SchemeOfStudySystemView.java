import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

/**
 * The View in the MVC pattern.
 * Responsible for rendering the user interface and handling user input events.
 * It displays data provided by the Controller and informs the Controller of user actions.
 * It has no direct knowledge of the Model's data or business logic.
 */
public class SchemeOfStudySystemView extends JFrame { // Renamed from SchemeOfStudySystem for clarity

    // CardLayout and main panel for switching between different views
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Reference to the Controller. The View tells the Controller about user actions.
    private SchemeOfStudySystemController controller;

    // UI components for the "Select Semester" panel
    private JComboBox<String> semesterTypeBox;
    private JButton nextBtn;

    // UI components for the "File List" panel
    private JPanel filesPanel; // Panel where file buttons are dynamically added
    private JLabel messageLabel; // For displaying messages like "No files available"

    // UI components for the "Show File Content" panel
    private JTextArea fileContentArea; // Displays the content of a selected file
    private JButton showFileBackBtn; // Back button specific to show file panel

    /**
     * Constructor for the View.
     * Initializes the GUI components and sets up the layout.
     * It also instantiates the Model and Controller to establish the MVC triad.
     */
    public SchemeOfStudySystemView() { // Renamed constructor
        setTitle("Scheme of Study System");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window

        // 1. Instantiate the Model (data and business logic)
        SchemeOfStudySystemModel model = new SchemeOfStudySystemModel();
        // 2. Instantiate the Controller, passing references to both Model and View.
        // This establishes the communication channels.
        this.controller = new SchemeOfStudySystemController(model, this);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        // Add different panels (views) to the CardLayout
        mainPanel.add(createSelectSemesterPanel(), "SelectSemester");
        mainPanel.add(createFileListPanel(), "FileList");
        mainPanel.add(createShowFilePanel(), "ShowFile");

        add(mainPanel); // Add the main panel to the JFrame
        cardLayout.show(mainPanel, "SelectSemester"); // Show the initial panel
    }

    /**
     * Creates the panel for selecting the semester type.
     * This method is purely for UI construction.
     * @return The JPanel for semester selection.
     */
    private JPanel createSelectSemesterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(Color.GRAY, 2));
        panel.setBackground(new Color(230, 240, 255)); // Light blueish background

        JLabel label = new JLabel("Select Semester Type:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        semesterTypeBox = new JComboBox<>(new String[]{"Spring", "Fall"});
        semesterTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        semesterTypeBox.setPreferredSize(new Dimension(150, 30));

        nextBtn = createBlueButton("Next");
        // Attach ActionListener: When 'Next' is clicked, tell the Controller.
        // The View DOES NOT decide what happens next; it delegates to the Controller.
        nextBtn.addActionListener(e -> {
            String selectedSemester = (String) semesterTypeBox.getSelectedItem();
            controller.handleSemesterSelection(selectedSemester); // Controller method call
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(230, 240, 255));
        centerPanel.add(semesterTypeBox);
        centerPanel.add(nextBtn);

        panel.add(label, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Creates the panel that displays the list of files for a selected semester.
     * This panel is dynamically populated by the Controller.
     * @return The JPanel for file list display.
     */
    private JPanel createFileListPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(230, 240, 255));
        outer.setBorder(new LineBorder(Color.GRAY, 2));

        // Back button for returning to semester selection.
        // Its action is delegated to the Controller.
        JPanel backBtnPanel = createTopRightBackButton(() -> controller.handleBackToFileList());
        outer.add(backBtnPanel, BorderLayout.NORTH);

        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        filesPanel.setBackground(new Color(230, 240, 255));
        filesPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JScrollPane scrollPane = new JScrollPane(filesPanel);
        scrollPane.setBorder(null); // No border for the scroll pane itself
        scrollPane.getVerticalScrollBar().setUnitIncrement(16); // Smooth scrolling

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED); // For displaying messages like "No files available"
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        outer.add(scrollPane, BorderLayout.CENTER);
        outer.add(messageLabel, BorderLayout.SOUTH);

        return outer;
    }

    /**
     * Creates a single entry panel for a file in the list.
     * This method is purely for UI construction.
     * @param file The File object for which to create the entry.
     * @return A JPanel representing a file entry with 'Download' and 'Show' buttons.
     */
    private JPanel createFileEntryPanel(File file) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40)); // Fixed height

        JLabel fileNameLabel = new JLabel(file.getName());
        fileNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        fileNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(230, 240, 255));

        JButton downloadBtn = createBlueButton("Download");
        // Delegate download action to controller. View just reports the click.
        downloadBtn.addActionListener(e -> {
            controller.handleDownloadFile(file); // Controller method call, passing the specific file
        });

        JButton showBtn = createBlueButton("Show");
        // Delegate show action to controller. View just reports the click.
        showBtn.addActionListener(e -> {
            controller.handleShowFile(file); // Controller method call, passing the specific file
        });

        btnPanel.add(downloadBtn);
        btnPanel.add(showBtn);

        panel.add(fileNameLabel, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    /**
     * Creates the panel for displaying the content of a selected file.
     * @return The JPanel for showing file content.
     */
    private JPanel createShowFilePanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(230, 240, 255));
        outer.setBorder(new LineBorder(Color.GRAY, 2));

        // Back button for returning to the file list.
        // Its action is delegated to the Controller.
        showFileBackBtn = createTopRightBackButton(() -> controller.handleBackToShowFile());
        outer.add(showFileBackBtn, BorderLayout.NORTH);

        fileContentArea = new JTextArea();
        fileContentArea.setEditable(false); // Content should not be editable by user
        fileContentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fileContentArea.setLineWrap(true);
        fileContentArea.setWrapStyleWord(true);
        fileContentArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(fileContentArea);
        scrollPane.setBorder(null);

        outer.add(scrollPane, BorderLayout.CENTER);

        return outer;
    }

    // --- Public methods for the Controller to instruct the View ---

    /**
     * Clears and populates the file list panel with new file entries.
     * This method is called by the Controller to update the View's display.
     * @param files A List of File objects to display.
     * @param semesterType The current semester type, used for displaying messages.
     */
    public void displayFilesList(List<File> files, String semesterType) {
        filesPanel.removeAll(); // Clear existing buttons/labels
        messageLabel.setText(""); // Clear previous messages

        if (files.isEmpty()) {
            messageLabel.setText("No files available for " + semesterType + " semester.");
        } else {
            for (File file : files) {
                filesPanel.add(createFileEntryPanel(file));
                filesPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between entries
            }
        }
        // Repaint to ensure UI updates are visible
        filesPanel.revalidate();
        filesPanel.repaint();
    }

    /**
     * Sets the text content of the file display area.
     * This method is called by the Controller to update the View's display.
     * @param content The string content to display in the text area.
     */
    public void displayFileContent(String content) {
        fileContentArea.setText(content);
        fileContentArea.setCaretPosition(0); // Scroll to top of the content
    }

    /**
     * Switches the currently visible panel in the main frame.
     * This method is called by the Controller to navigate between different views.
     * @param panelName The name of the panel to switch to (e.g., "SelectSemester", "FileList", "ShowFile").
     */
    public void switchToPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    /**
     * Displays an error message dialog to the user.
     * This method is called by the Controller to inform the user about errors.
     * @param message The error message to display.
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Displays an informational message dialog to the user.
     * This method is called by the Controller to inform the user about successful operations or general information.
     * @param message The information message to display.
     */
    public void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Prompts the user to select a save location for a file.
     * The View provides the UI component for file selection, and returns the result to the Controller.
     * @param defaultFileName The suggested default name for the file.
     * @return A File object representing the chosen save location, or null if the user cancels.
     */
    public File promptForSaveLocation(String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultFileName)); // Suggest a default file name
        int option = fileChooser.showSaveDialog(this); // Show save dialog, parented by this frame
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile(); // Return the selected file to the Controller
        }
        return null; // User cancelled the dialog
    }

    // --- Helper methods for UI styling (purely View responsibility) ---

    private JPanel createTopRightBackButton(Runnable onClick) {
        JButton backBtn = new JButton("← Back");
        backBtn.setPreferredSize(new Dimension(90, 28));
        backBtn.setMargin(new Insets(2, 6, 2, 6));
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(70, 130, 180)); // Steel Blue
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(new LineBorder(Color.GRAY));
        // The action listener simply executes the provided lambda (which is a Controller method call)
        backBtn.addActionListener(e -> onClick.run());

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(230, 240, 255));
        btnPanel.add(backBtn);

        return btnPanel;
    }

    private JButton createBlueButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180)); // Steel Blue
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new LineBorder(Color.GRAY));
        return button;
    }

    /**
     * Main method to start the application.
     * It ensures the GUI is created and updated on the Event Dispatch Thread (EDT).
     * The View initiates the creation of the MVC components.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SchemeOfStudySystemView().setVisible(true); // Create and show the main View
        });
    }
}