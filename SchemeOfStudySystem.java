import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Insets;
import java.io.File;
import java.io.IOException; // Added for exception handling in View
import java.util.ArrayList; // No longer strictly needed for View, but kept for clarity if temporary lists are used
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

public class SchemeOfStudySystem extends JFrame {

    private CardLayout cardLayout;
    private JPanel mainPanel;

    private SchemeOfStudySystemController controller; // Reference to the controller

    private JComboBox<String> semesterTypeBox;
    private JPanel filesPanel;
    private JLabel messageLabel;

    private JTextArea fileContentArea;

    // These are no longer directly managed by the View
    // private String currentSemesterType;
    // private File currentFile;

    public SchemeOfStudySystem() {
        setTitle("Scheme of Study System");
        setSize(600, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Instantiate Model and Controller
        SchemeOfStudySystemModel model = new SchemeOfStudySystemModel();
        this.controller = new SchemeOfStudySystemController(model, this); // Pass 'this' (the view) to controller

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createSelectSemesterPanel(), "SelectSemester");
        mainPanel.add(createFileListPanel(), "FileList");
        mainPanel.add(createShowFilePanel(), "ShowFile");

        add(mainPanel);
        cardLayout.show(mainPanel, "SelectSemester");
    }

    // No longer needed here as setup is in Model
    // private void setupSampleFiles() { ... }

    private JPanel createSelectSemesterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new LineBorder(Color.GRAY, 2));
        panel.setBackground(new Color(230, 240, 255));

        JLabel label = new JLabel("Select Semester Type:");
        label.setFont(new Font("Segoe UI", Font.BOLD, 20));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));

        semesterTypeBox = new JComboBox<>(new String[]{"Spring", "Fall"});
        semesterTypeBox.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        semesterTypeBox.setPreferredSize(new Dimension(150, 30));

        JButton nextBtn = createBlueButton("Next");
        nextBtn.addActionListener(e -> {
            // Delegate the action to the controller
            String selectedSemester = (String) semesterTypeBox.getSelectedItem();
            controller.handleSemesterSelection(selectedSemester);
        });

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(new Color(230, 240, 255));
        centerPanel.add(semesterTypeBox);
        centerPanel.add(nextBtn);

        panel.add(label, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFileListPanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(230, 240, 255));
        outer.setBorder(new LineBorder(Color.GRAY, 2));

        // Back button action delegates to controller
        JPanel backBtnPanel = createTopRightBackButton(() -> controller.handleBackToFileList());
        outer.add(backBtnPanel, BorderLayout.NORTH);

        filesPanel = new JPanel();
        filesPanel.setLayout(new BoxLayout(filesPanel, BoxLayout.Y_AXIS));
        filesPanel.setBackground(new Color(230, 240, 255));
        filesPanel.setBorder(BorderFactory.createEmptyBorder(20, 60, 20, 60));

        JScrollPane scrollPane = new JScrollPane(filesPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        messageLabel = new JLabel("", SwingConstants.CENTER);
        messageLabel.setForeground(Color.RED);
        messageLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));

        outer.add(scrollPane, BorderLayout.CENTER);
        outer.add(messageLabel, BorderLayout.SOUTH);

        return outer;
    }

    /**
     * Public method for the Controller to update the files list displayed in the View.
     */
    public void displayFilesList(List<File> files, String semesterType) {
        filesPanel.removeAll();
        messageLabel.setText("");

        if (files.isEmpty()) {
            messageLabel.setText("No files available for " + semesterType + " semester.");
        } else {
            for (File file : files) {
                filesPanel.add(createFileEntryPanel(file));
                filesPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        filesPanel.revalidate();
        filesPanel.repaint();
    }

    private JPanel createFileEntryPanel(File file) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(230, 240, 255));
        panel.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        JLabel fileNameLabel = new JLabel(file.getName());
        fileNameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        fileNameLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(230, 240, 255));

        JButton downloadBtn = createBlueButton("Download");
        downloadBtn.addActionListener(e -> {
            // Delegate download action to controller
            controller.handleDownloadFile(file);
        });

        JButton showBtn = createBlueButton("Show");
        showBtn.addActionListener(e -> {
            // Delegate show action to controller
            controller.handleShowFile(file);
        });

        btnPanel.add(downloadBtn);
        btnPanel.add(showBtn);

        panel.add(fileNameLabel, BorderLayout.WEST);
        panel.add(btnPanel, BorderLayout.EAST);

        return panel;
    }

    // This method is now in the Model, the View should not handle file copying.
    // private void copyFile(File source, File dest) throws IOException { ... }

    private JPanel createShowFilePanel() {
        JPanel outer = new JPanel(new BorderLayout());
        outer.setBackground(new Color(230, 240, 255));
        outer.setBorder(new LineBorder(Color.GRAY, 2));

        // Back button action delegates to controller
        JPanel backBtnPanel = createTopRightBackButton(() -> controller.handleBackToShowFile());
        outer.add(backBtnPanel, BorderLayout.NORTH);

        fileContentArea = new JTextArea();
        fileContentArea.setEditable(false);
        fileContentArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        fileContentArea.setLineWrap(true);
        fileContentArea.setWrapStyleWord(true);
        fileContentArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(fileContentArea);
        scrollPane.setBorder(null);

        outer.add(scrollPane, BorderLayout.CENTER);

        return outer;
    }

    /**
     * Public method for the Controller to update the file content area in the View.
     */
    public void displayFileContent(String content) {
        fileContentArea.setText(content);
        fileContentArea.setCaretPosition(0); // Scroll to top
    }

    /**
     * Public method for the Controller to switch between panels in the View.
     */
    public void switchToPanel(String panelName) {
        cardLayout.show(mainPanel, panelName);
    }

    /**
     * Public method for the Controller to show error messages to the user.
     */
    public void showErrorMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Public method for the Controller to show success messages to the user.
     */
    public void showInformationMessage(String message) {
        JOptionPane.showMessageDialog(this, message, "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Public method for the Controller to get a file destination from the user.
     */
    public File promptForSaveLocation(String defaultFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(defaultFileName));
        int option = fileChooser.showSaveDialog(this);
        if (option == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile();
        }
        return null; // User cancelled
    }


    private JPanel createTopRightBackButton(Runnable onClick) {
        JButton backBtn = new JButton("← Back");
        backBtn.setPreferredSize(new Dimension(90, 28));
        backBtn.setMargin(new Insets(2, 6, 2, 6));
        backBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        backBtn.setBackground(new Color(70, 130, 180));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(new LineBorder(Color.GRAY));
        backBtn.addActionListener(e -> onClick.run()); // This onClick is now a controller method call

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnPanel.setBackground(new Color(230, 240, 255));
        btnPanel.add(backBtn);

        return btnPanel;
    }

    private JButton createBlueButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(new LineBorder(Color.GRAY));
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SchemeOfStudySystem().setVisible(true);
        });
    }
}