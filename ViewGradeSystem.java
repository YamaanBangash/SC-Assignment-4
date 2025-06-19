import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List; // Import List for getAccessibleSemesters

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JEditorPane; // Changed from JTextArea
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class ViewGradeSystem {
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel cardPanel;

    private JTextField regField;
    private GradeController controller; // Instance of our new GradeController

    // Define a consistent color palette
    private final Color PRIMARY_BLUE = new Color(52, 152, 219); // A vibrant blue
    private final Color ACCENT_GREEN = new Color(46, 204, 113); // For success/action
    private final Color BACKGROUND_LIGHT = new Color(240, 243, 244); // Light background
    private final Color TEXT_DARK = new Color(44, 62, 80); // Dark text
    private final Color BUTTON_HOVER_BLUE = new Color(41, 128, 185); // Slightly darker blue for hover
    private final Color BUTTON_HOVER_RED = new Color(200, 50, 50); // Darker red for hover
    private final Color ERROR_RED = new Color(231, 76, 60); // For error messages

    // Define a consistent font
    private final Font LABEL_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);
    private final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private final Font RESULT_FONT = new Font("Segoe UI", Font.PLAIN, 14); // Adjusted for JEditorPane content


    public static void main(String[] args) {
        SwingUtilities.invokeLater(ViewGradeSystem::new);
    }

    public ViewGradeSystem() {
        frame = new JFrame("Student Grade Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 450);
        frame.setMinimumSize(new Dimension(550, 400));
        frame.setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setBackground(BACKGROUND_LIGHT);

        cardPanel.add(createRegistrationPanel(), "registration");
        cardPanel.add(createSelectionPanel(), "selection");

        frame.add(cardPanel);
        frame.setVisible(true);

        cardLayout.show(cardPanel, "registration");
    }

    private JPanel createRegistrationPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));

        JLabel titleLabel = new JLabel("Welcome to Grade Viewer", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(TEXT_DARK);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(BACKGROUND_LIGHT);
        inputPanel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE, 1), "Enter Student Registration ID"));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 5, 10, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel regLabel = new JLabel("Registration ID:");
        regLabel.setFont(LABEL_FONT);
        regLabel.setForeground(TEXT_DARK);
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(regLabel, gbc);

        regField = new JTextField(20);
        regField.setFont(LABEL_FONT);
        regField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(PRIMARY_BLUE),
                new EmptyBorder(5, 5, 5, 5)));
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(regField, gbc);

        JButton submitButton = new JButton("View Grades");
        styleButton(submitButton, PRIMARY_BLUE, Color.WHITE);
        submitButton.addActionListener(e -> {
            String regId = regField.getText().trim();
            if (regId.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Registration ID cannot be empty.", "Input Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            // Initialize the GradeController with the entered Registration ID
            controller = new GradeController(regId);
            cardLayout.show(cardPanel, "selection");
        });
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        inputPanel.add(submitButton, gbc);

        panel.add(inputPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(new EmptyBorder(30, 50, 30, 50));

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_LIGHT);

        JLabel instructionLabel = new JLabel("Select an option:", SwingConstants.CENTER);
        instructionLabel.setFont(TITLE_FONT.deriveFont(Font.BOLD, 20));
        instructionLabel.setForeground(TEXT_DARK);
        headerPanel.add(instructionLabel, BorderLayout.CENTER);

        JButton backToRegBtn = new JButton("Back");
        styleSmallButton(backToRegBtn, ERROR_RED, Color.WHITE);
        backToRegBtn.addActionListener(e -> cardLayout.show(cardPanel, "registration"));
        headerPanel.add(backToRegBtn, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20));
        optionsPanel.setBackground(BACKGROUND_LIGHT);

        JButton semesterBtn = new JButton("View Grades by Semester");
        styleButton(semesterBtn, PRIMARY_BLUE, Color.WHITE);
        semesterBtn.addActionListener(e -> showSemesterSelection());
        optionsPanel.add(semesterBtn);

        JButton courseBtn = new JButton("View GPA by Course");
        styleButton(courseBtn, PRIMARY_BLUE, Color.WHITE);
        courseBtn.addActionListener(e -> showCourseInput());
        optionsPanel.add(courseBtn);

        panel.add(optionsPanel, BorderLayout.CENTER);

        return panel;
    }

    private void showSemesterSelection() {
        JFrame resultFrame = new JFrame("Semester Grades");
        resultFrame.setSize(650, 450);
        resultFrame.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_BLUE);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setOpaque(false);

        JLabel selectLabel = new JLabel("Select Semester:");
        selectLabel.setFont(LABEL_FONT);
        selectLabel.setForeground(Color.WHITE);
        controlsPanel.add(selectLabel);

        JComboBox<String> semesterBox = new JComboBox<>();
        semesterBox.setFont(LABEL_FONT);
        semesterBox.setBackground(Color.WHITE);
        if (controller != null) {
            List<String> semesters = controller.getAccessibleSemesters();
            if (semesters.isEmpty()) {
                semesterBox.addItem("No semesters available");
                semesterBox.setEnabled(false);
            } else {
                for (String sem : semesters) {
                    semesterBox.addItem(sem);
                }
            }
        } else {
            semesterBox.addItem("Controller not initialized.");
            semesterBox.setEnabled(false);
        }
        controlsPanel.add(semesterBox);

        JButton viewBtn = new JButton("View Grades");
        styleButton(viewBtn, ACCENT_GREEN, Color.WHITE);
        viewBtn.setFont(BUTTON_FONT.deriveFont(12f));
        viewBtn.setBorder(new EmptyBorder(5, 10, 5, 10));
        controlsPanel.add(viewBtn);

        topPanel.add(controlsPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        styleSmallButton(backBtn, ERROR_RED, Color.WHITE);
        backBtn.addActionListener(e -> resultFrame.dispose());
        topPanel.add(backBtn, BorderLayout.EAST);

        // --- Change from JTextArea to JEditorPane ---
        JEditorPane resultDisplayArea = new JEditorPane();
        resultDisplayArea.setContentType("text/html"); // Set content type for HTML rendering
        resultDisplayArea.setEditable(false); // Make it read-only
        resultDisplayArea.setBackground(BACKGROUND_LIGHT);
        resultDisplayArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE); // Use panel's font
        resultDisplayArea.setFont(RESULT_FONT); // Set a default font, HTML content might override
        // --- End Change ---

        JScrollPane scrollPane = new JScrollPane(resultDisplayArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));

        viewBtn.addActionListener(e -> {
            String selected = (String) semesterBox.getSelectedItem();
            if (controller != null && selected != null && !selected.equals("No semesters available") && !selected.equals("Controller not initialized.")) {
                String resultHtml = controller.getSemesterGPA(selected);
                if (resultHtml == null || resultHtml.isEmpty()) {
                    resultDisplayArea.setText("<html><body style='font-family: Segoe UI; color:" + toHex(ERROR_RED) + ";'>No grades found for the selected semester.</body></html>");
                } else {
                    resultDisplayArea.setText(resultHtml);
                }
            } else {
                resultDisplayArea.setText("<html><body style='font-family: Segoe UI; color:" + toHex(ERROR_RED) + ";'>Please select a valid semester.</body></html>");
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        resultFrame.setContentPane(panel);
        resultFrame.setVisible(true);
    }

    private void showCourseInput() {
        JFrame resultFrame = new JFrame("Course GPA");
        resultFrame.setSize(550, 350);
        resultFrame.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(BACKGROUND_LIGHT);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(PRIMARY_BLUE);
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        controlsPanel.setOpaque(false);

        JLabel courseLabel = new JLabel("Enter Course Code:");
        courseLabel.setFont(LABEL_FONT);
        courseLabel.setForeground(Color.WHITE);
        controlsPanel.add(courseLabel);

        JTextField courseField = new JTextField(15);
        courseField.setFont(LABEL_FONT);
        courseField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                new EmptyBorder(5, 5, 5, 5)));
        controlsPanel.add(courseField);

        JButton viewBtn = new JButton("Get GPA");
        styleButton(viewBtn, ACCENT_GREEN, Color.WHITE);
        viewBtn.setFont(BUTTON_FONT.deriveFont(12f));
        viewBtn.setBorder(new EmptyBorder(5, 10, 5, 10));
        controlsPanel.add(viewBtn);

        topPanel.add(controlsPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        styleSmallButton(backBtn, ERROR_RED, Color.WHITE);
        backBtn.addActionListener(e -> resultFrame.dispose());
        topPanel.add(backBtn, BorderLayout.EAST);

        // --- Change from JTextArea to JEditorPane ---
        JEditorPane resultDisplayArea = new JEditorPane();
        resultDisplayArea.setContentType("text/html"); // Set content type for HTML rendering
        resultDisplayArea.setEditable(false); // Make it read-only
        resultDisplayArea.setBackground(BACKGROUND_LIGHT);
        resultDisplayArea.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE); // Use panel's font
        resultDisplayArea.setFont(RESULT_FONT); // Set a default font, HTML content might override
        // --- End Change ---

        JScrollPane scrollPane = new JScrollPane(resultDisplayArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(PRIMARY_BLUE, 1));

        viewBtn.addActionListener(e -> {
            String courseCode = courseField.getText().trim();
            if (courseCode.isEmpty()) {
                resultDisplayArea.setText("<html><body style='font-family: Segoe UI; color:" + toHex(ERROR_RED) + ";'>Course code cannot be empty.</body></html>");
                return;
            }
            if (controller != null) {
                String resultHtml = controller.getCourseGPA(courseCode);
                if (resultHtml == null || resultHtml.isEmpty()) {
                    resultDisplayArea.setText("<html><body style='font-family: Segoe UI; color:" + toHex(ERROR_RED) + ";'>No GPA found for course: " + courseCode + "</body></html>");
                } else {
                    resultDisplayArea.setText(resultHtml);
                }
            } else {
                resultDisplayArea.setText("<html><body style='font-family: Segoe UI; color:" + toHex(ERROR_RED) + ";'>Controller not initialized. Please go back and enter Registration ID.</body></html>");
            }
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        resultFrame.setContentPane(panel);
        resultFrame.setVisible(true);
    }

    // Helper method to style standard buttons
    private void styleButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(BUTTON_FONT);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor.darker(), 1),
                new EmptyBorder(10, 20, 10, 20)));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(BUTTON_HOVER_BLUE);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    // Helper method to style smaller buttons for corners
    private void styleSmallButton(JButton button, Color bgColor, Color fgColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(BUTTON_FONT.deriveFont(10f));
        button.setFocusPainted(false);
        button.setBorder(new EmptyBorder(5, 10, 5, 10));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (bgColor.equals(ERROR_RED)) {
                    button.setBackground(BUTTON_HOVER_RED);
                } else {
                    button.setBackground(bgColor.darker());
                }
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    // Helper method to convert Color to Hex string for HTML
    private String toHex(Color color) {
        return String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
    }
}