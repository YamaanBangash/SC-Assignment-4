import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.Vector;

public class ViewRegisteredCourses extends JFrame {
    private final CourseController controller;
    private JTable coursesTable;
    private DefaultTableModel tableModel;
    private JLabel totalCreditsLabel; // Declared here

    // --- Color Palette (consistency) ---
    private static final Color QAU_DARK_BLUE = new Color(30, 45, 75);
    private static final Color QAU_LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color TEXT_LIGHT = Color.WHITE;
    private static final Color BUTTON_BLUE = new Color(0x1E3A8A);
    private static final Color BUTTON_BLUE_HOVER = new Color(0x2552AA);

    public ViewRegisteredCourses(CourseController controller) {
        this.controller = controller;

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
        loadRegisteredCourses();
        updateTotalCreditsDisplay();
    }

    private void initializeUI() {
        setTitle("View Registered Courses");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout(15, 15));
        setBackground(QAU_LIGHT_GRAY);

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(QAU_DARK_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20)); // Adjusted padding

        JLabel titleLabel = new JLabel("Your Currently Registered Courses", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(TEXT_LIGHT);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        // Course Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20)); // Adjusted padding
        tablePanel.setBackground(QAU_LIGHT_GRAY);

        String[] columnNames = {"Course Code", "Course Name", "Faculty", "Credits", "Semester"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        coursesTable = new JTable(tableModel);
        coursesTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        coursesTable.setRowHeight(25);
        coursesTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 15));
        coursesTable.getTableHeader().setBackground(QAU_DARK_BLUE.brighter());
        coursesTable.getTableHeader().setForeground(TEXT_LIGHT);
        coursesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow only single row selection

        // Center align text in table cells
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < coursesTable.getColumnCount(); i++) {
            coursesTable.getColumnModel().getColumn(i).setCellRenderer(renderer);
        }

        JScrollPane scrollPane = new JScrollPane(coursesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(QAU_DARK_BLUE.brighter(), 1));
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        add(tablePanel, BorderLayout.CENTER);

        // Bottom Panel (Total Credits & Buttons)
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(QAU_DARK_BLUE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        totalCreditsLabel = new JLabel("Total Registered Credits: 0", SwingConstants.LEFT); // Initialized here
        totalCreditsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalCreditsLabel.setForeground(TEXT_LIGHT);
        bottomPanel.add(totalCreditsLabel, BorderLayout.WEST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(QAU_DARK_BLUE);

        // Removed the "Drop Selected Course" button
        /*
        JButton dropCourseButton = new JButton("Drop Selected Course");
        customizeButton(dropCourseButton, new Color(180, 50, 50), TEXT_LIGHT, new Color(200, 70, 70));
        dropCourseButton.addActionListener(e -> dropSelectedCourse());
        buttonPanel.add(dropCourseButton);
        */

        JButton backButton = new JButton("Back to Main Menu");
        customizeButton(backButton, BUTTON_BLUE, TEXT_LIGHT, BUTTON_BLUE_HOVER);
        backButton.addActionListener(e -> {
            dispose();
            new OptionMenu(controller).setVisible(true);
        });
        buttonPanel.add(backButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void loadRegisteredCourses() {
        tableModel.setRowCount(0); // Clear existing rows
        List<Map<String, String>> registeredCourses = controller.getDetailedRegisteredCourses();

        if (registeredCourses.isEmpty()) {
            tableModel.addRow(new Object[]{"N/A", "No courses registered yet.", "", "", ""});
        } else {
            for (Map<String, String> course : registeredCourses) {
                Vector<String> row = new Vector<>();
                row.add(course.get("code"));
                row.add(course.get("name"));
                row.add(course.get("faculty"));
                row.add(course.get("credits"));
                row.add(course.get("semester"));
                tableModel.addRow(row);
            }
        }
    }

    // dropSelectedCourse method is no longer needed since the button is removed.
    // However, if the "Remove / Drop Course" option in OptionMenu will lead to a new screen
    // that uses this functionality, it might need to be in the controller or a new class.
    // For now, I'll comment it out, assuming the separate menu option will handle dropping.
    /*
    private void dropSelectedCourse() {
        int selectedRow = coursesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a course to drop.",
                    "No Course Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String courseCodeToDrop = (String) tableModel.getValueAt(selectedRow, 0);

        if ("N/A".equals(courseCodeToDrop)) {
            JOptionPane.showMessageDialog(this,
                    "Cannot drop the 'No courses registered' placeholder.",
                    "Invalid Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to drop " + courseCodeToDrop + "?",
                "Confirm Drop",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = controller.dropCourse(courseCodeToDrop);
            if (success) {
                JOptionPane.showMessageDialog(this,
                        courseCodeToDrop + " successfully dropped.",
                        "Course Dropped",
                        JOptionPane.INFORMATION_MESSAGE);
                loadRegisteredCourses(); // Reload the table to reflect changes
                updateTotalCreditsDisplay(); // Recalculate total credits
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to drop " + courseCodeToDrop + ". It might not be registered.",
                        "Drop Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    */

    private void updateTotalCreditsDisplay() {
        int totalCredits = controller.getModel().getTotalRegisteredCredits(); // Access model via controller
        totalCreditsLabel.setText("Total Registered Credits: " + totalCredits);
        // Visual cue if total credits are above 18 (though this screen is for *registered* credits, not selections)
        if (totalCredits > 18) {
             totalCreditsLabel.setForeground(Color.RED);
        } else {
             totalCreditsLabel.setForeground(TEXT_LIGHT);
        }
    }

    /**
     * Helper method to apply consistent styling to JButtons.
     */
    private void customizeButton(JButton button, Color bgColor, Color fgColor, Color hoverColor) {
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 15));
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            CourseModel model = new CourseModel();
            CourseController controller = new CourseController(model);

            // Add some dummy registered courses for testing purposes
            Set<String> preRegisteredForTest = new HashSet<>();
            preRegisteredForTest.add("CS-104");
            preRegisteredForTest.add("EN-200");
            preRegisteredForTest.add("MA-202");
            preRegisteredForTest.add("CS-211"); // Add more to test scroll and drop
            model.registerCourses(preRegisteredForTest);

            new ViewRegisteredCourses(controller).setVisible(true);
        });
    }
}