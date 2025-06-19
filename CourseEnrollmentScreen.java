import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class CourseEnrollmentScreen extends JFrame {
    private final CourseController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JComboBox<Integer> semesterDropdown;
    private JLabel creditLabel;
    private JButton confirmRegistrationButton;

    // Define the ORIGINAL color palette for a professional look
    private static final Color PRIMARY_BLUE = new Color(70, 130, 180); // SteelBlue
    private static final Color LIGHT_BLUE = new Color(173, 216, 230); // LightBlue
    private static final Color DARK_GRAY = new Color(50, 50, 50);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 204, 0); // Gold for accents

    public CourseEnrollmentScreen(CourseController controller) {
        this.controller = controller;

        // Set Nimbus Look and Feel for a modern UI
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.err.println("Nimbus LookAndFeel not found. Using default.");
        }
        initializeUI();
        // Initial load and display update after UI components are fully initialized
        loadCoursesForSemester((Integer) semesterDropdown.getSelectedItem()); // Default to semester 1
        updateTotalCreditsDisplay();
    }

    private void initializeUI() {
        setTitle("Course Enrollment System");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(10, 10)); // Add some padding

        // --- Top Panel ---
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10)); // Added padding
        topPanel.setBackground(PRIMARY_BLUE);
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding inside panel

        JLabel semesterLabel = new JLabel("Select Semester:");
        semesterLabel.setForeground(Color.WHITE);
        semesterLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPanel.add(semesterLabel);

        semesterDropdown = new JComboBox<>();
        for (int i = 1; i <= 8; i++) {
            semesterDropdown.addItem(i);
        }
        semesterDropdown.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        semesterDropdown.setBackground(LIGHT_GRAY);
        semesterDropdown.setForeground(TEXT_COLOR);
        semesterDropdown.addActionListener(e -> {
            // When semester changes, load and re-evaluate selections
            loadCoursesForSemester((Integer) semesterDropdown.getSelectedItem());
            updateTotalCreditsDisplay(); // Refresh total credits based on controller's state
        });
        topPanel.add(semesterDropdown);

        creditLabel = new JLabel("Total Selected Credit Hours: " + controller.getCurrentlySelectedTotalCredits());
        creditLabel.setForeground(Color.WHITE);
        creditLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        topPanel.add(creditLabel);

        add(topPanel, BorderLayout.NORTH);

        // --- Table Panel ---
        tableModel = new DefaultTableModel(new Object[]{"Select", "Code", "Name", "Faculty", "Credits"}, 0) {
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Boolean.class : String.class;
            }

            @Override
            public boolean isCellEditable(int row, int column) {
                // Disable editing of "Select" column if the course is already registered in the model
                if (column == 0) {
                    String courseCode = (String) getValueAt(row, 1);
                    return !controller.isCourseAlreadyRegistered(courseCode);
                }
                return false; // Other columns are not editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(25); // Make rows a bit taller
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 14));
        table.getTableHeader().setBackground(DARK_GRAY);
        table.getTableHeader().setForeground(Color.WHITE);
        table.setFillsViewportHeight(true); // Table fills the entire height of the scroll pane
        table.setSelectionBackground(HIGHLIGHT_COLOR);
        table.setSelectionForeground(TEXT_COLOR);

        // Center align text in table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Add a listener to the table for changes in the checkbox column
        table.getModel().addTableModelListener(e -> {
            if (e.getColumn() == 0) { // Only interested in changes to the "Select" column
                int row = e.getFirstRow();
                String courseCode = (String) tableModel.getValueAt(row, 1);
                boolean isChecked = (Boolean) tableModel.getValueAt(row, 0);

                if (isChecked) {
                    // Check for 18 credit hour limit before adding to selection
                    int courseCredit = controller.getModel().getCourseCredit(courseCode);
                    if ((controller.getCurrentlySelectedTotalCredits() + controller.getModel().getTotalRegisteredCredits() + courseCredit) > 18) {
                        JOptionPane.showMessageDialog(this,
                                "Adding this course would exceed the 18 credit hour limit (Total: " + (controller.getCurrentlySelectedTotalCredits() + controller.getModel().getTotalRegisteredCredits() + courseCredit) + ").",
                                "Credit Limit Exceeded",
                                JOptionPane.WARNING_MESSAGE);
                        // Revert checkbox state
                        SwingUtilities.invokeLater(() -> tableModel.setValueAt(false, row, 0));
                    } else {
                        controller.addSelectedCourse(courseCode);
                    }
                } else {
                    controller.removeSelectedCourse(courseCode);
                }
                updateTotalCreditsDisplay();
            }
        });


        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10), // Outer padding
                BorderFactory.createLineBorder(PRIMARY_BLUE, 2) // Inner border
        ));
        scrollPane.setBackground(LIGHT_GRAY);
        add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Panel (Buttons) ---
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10)); // Align right, add padding
        bottomPanel.setBackground(PRIMARY_BLUE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        confirmRegistrationButton = new JButton("Confirm Registration");
        customizeButton(confirmRegistrationButton, HIGHLIGHT_COLOR, DARK_GRAY, HIGHLIGHT_COLOR.darker());
        confirmRegistrationButton.addActionListener(e -> {
            handleConfirmRegistration();
        });
        bottomPanel.add(confirmRegistrationButton);

        // Added "Back to Main Menu" button
        JButton backToMainMenuButton = new JButton("Back to Main Menu");
        customizeButton(backToMainMenuButton, new Color(180, 50, 50), Color.WHITE, new Color(200, 70, 70));
        backToMainMenuButton.addActionListener(e -> {
            dispose(); // Close current screen
            new OptionMenu(controller).setVisible(true); // Open the main menu again
        });
        bottomPanel.add(backToMainMenuButton);


        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void customizeButton(JButton button, Color bgColor, Color fgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Add a subtle hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    private void loadCoursesForSemester(int semester) {
        tableModel.setRowCount(0); // Clear existing rows
        List<Map<String, String>> courses = controller.getCoursesBySemester(semester);
        Set<String> currentlySelected = controller.getCurrentlySelectedCourseCodes();
        Set<String> alreadyRegisteredInModel = controller.getModel().getRegisteredCoursesInModel(); // Get from model directly

        for (Map<String, String> course : courses) {
            String code = course.get("code");
            boolean isAlreadyRegistered = alreadyRegisteredInModel.contains(code);
            boolean isCurrentlySelected = currentlySelected.contains(code);

            // If already registered, it should be checked and disabled.
            // Otherwise, it should be checked only if it's in the temporary selection.
            boolean isChecked = isAlreadyRegistered || isCurrentlySelected;

            Object[] rowData = {isChecked, code, course.get("name"), course.get("faculty"), course.get("credits")};
            tableModel.addRow(rowData);
        }
    }

    private void updateTotalCreditsDisplay() {
        int totalSelectedCredits = controller.getCurrentlySelectedTotalCredits(); // These are temporarily selected courses
        int totalPermanentlyRegisteredCredits = controller.getModel().getTotalRegisteredCredits(); // These are courses already registered in the model
        int grandTotal = totalSelectedCredits + totalPermanentlyRegisteredCredits;

        creditLabel.setText("Total Selected Credit Hours: " + totalSelectedCredits + " (Registered: " + totalPermanentlyRegisteredCredits + ", Grand Total: " + grandTotal + ")");

        // Visual cue if approaching or exceeding limit
        if (grandTotal > 18) {
            creditLabel.setForeground(Color.RED);
        } else if (grandTotal >= 15) { // Nearing limit
            creditLabel.setForeground(Color.ORANGE);
        } else {
            creditLabel.setForeground(Color.WHITE);
        }
    }

    private void handleConfirmRegistration() {
        Set<String> coursesToRegister = controller.getCurrentlySelectedCourseCodes();

        if (coursesToRegister.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No courses selected for registration.",
                    "No Courses to Confirm",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Validate 18 credit hour limit one last time
        if (!controller.canEnrollTheseCourses(coursesToRegister)) {
            JOptionPane.showMessageDialog(this,
                    "Failed to register courses: Total credit hours exceed 18 after this registration.",
                    "Credit Limit Exceeded",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Check if any selected course is already permanently registered (should ideally be handled by UI disabling)
        Set<String> alreadyRegisteredPermanently = coursesToRegister.stream()
                .filter(controller::isCourseAlreadyRegistered)
                .collect(java.util.stream.Collectors.toSet());

        if (!alreadyRegisteredPermanently.isEmpty()) {
            StringBuilder message = new StringBuilder("The following selected courses are already permanently registered and will not be re-registered:\n");
            for (String code : alreadyRegisteredPermanently) {
                message.append("- ").append(code).append("\n");
            }
            JOptionPane.showMessageDialog(this,
                    message.toString(),
                    "Already Registered Courses Ignored",
                    JOptionPane.INFORMATION_MESSAGE);

            // Remove these from the current batch for registration, but let the process continue for others
            coursesToRegister.removeAll(alreadyRegisteredPermanently);
            if (coursesToRegister.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No new courses left to register after removing already registered ones.",
                        "No New Courses",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }

        boolean success = controller.registerSelectedCourses(); // Controller handles the actual registration and internal validation

        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Courses have been successfully registered!",
                    "Registration Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose(); // Close this screen
            new OptionMenu(controller).setVisible(true); // Open the main menu
        } else {
            JOptionPane.showMessageDialog(this,
                    "Failed to register courses. Please check selections and credit limits.",
                    "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CourseModel model = new CourseModel();
            CourseController controller = new CourseController(model);

            // Pre-register some courses for testing the "already registered" check
            Set<String> preRegisteredForTest = new HashSet<>();
            preRegisteredForTest.add("CS-104"); // Already registered from S1 (3 credits)
            preRegisteredForTest.add("EN-200"); // Already registered from S2 (3 credits)
            model.registerCourses(preRegisteredForTest); // Register in the model directly for test

            new CourseEnrollmentScreen(controller).setVisible(true);
        });
    }
}