import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class DropCourseScreen extends JFrame {
    private final CourseController controller;
    private JPanel coursesPanel; // Panel to hold course checkboxes
    private Map<String, JCheckBox> courseCheckboxes; // To map course codes to their checkboxes
    private JLabel totalCreditsLabel; // To show current total credits

    // --- Color Palette (Consistent with other screens) ---
    private static final Color PRIMARY_BLUE = new Color(70, 130, 180); // SteelBlue
    private static final Color LIGHT_BLUE = new Color(173, 216, 230); // LightBlue for background elements
    private static final Color DARK_GRAY = new Color(50, 50, 50);
    private static final Color TEXT_COLOR = new Color(30, 30, 30);
    private static final Color BUTTON_HOVER_COLOR = new Color(90, 150, 200); // Slightly darker blue for hover
    private static final Color BACK_BUTTON_COLOR = new Color(180, 50, 50); // Reddish for back
    private static final Color BACK_BUTTON_HOVER = new Color(200, 70, 70); // Darker reddish for hover

    public DropCourseScreen(CourseController controller) {
        this.controller = controller;
        courseCheckboxes = new HashMap<>();

        setTitle("Drop / Remove Course");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        setLocationRelativeTo(null); // Center the frame
        setLayout(new BorderLayout());
        getContentPane().setBackground(LIGHT_BLUE); // Set background for the frame's content pane

        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createCoursesListPanel(), BorderLayout.CENTER);
        add(createActionPanel(), BorderLayout.SOUTH);

        populateRegisteredCourses(); // Initial population of courses
        updateTotalCreditsDisplay(); // Initial display of total credits
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JLabel titleLabel = new JLabel("Registered Courses", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.CENTER);

        // Back button to return to OptionMenu
        JButton backBtn = createStyledButton("← Back to Main Menu", BACK_BUTTON_COLOR, Color.WHITE, BACK_BUTTON_HOVER);
        backBtn.addActionListener(e -> {
            dispose(); // Close this screen
            new OptionMenu(controller).setVisible(true); // Open the main menu
        });
        JPanel backBtnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        backBtnPanel.setOpaque(false); // Make it transparent so PRIMARY_BLUE background shows
        backBtnPanel.add(backBtn);
        headerPanel.add(backBtnPanel, BorderLayout.WEST);

        return headerPanel;
    }

   // CHANGE THE RETURN TYPE FROM JPanel TO JScrollPane
    private JScrollPane createCoursesListPanel() {
        coursesPanel = new JPanel();
        coursesPanel.setLayout(new BoxLayout(coursesPanel, BoxLayout.Y_AXIS));
        coursesPanel.setBackground(LIGHT_BLUE);
        coursesPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));

        JScrollPane scrollPane = new JScrollPane(coursesPanel);
        scrollPane.setBorder(BorderFactory.createLineBorder(DARK_GRAY.brighter(), 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        return scrollPane; // Now this matches the method's return type
    }

    private void populateRegisteredCourses() {
        coursesPanel.removeAll(); // Clear existing components
        courseCheckboxes.clear(); // Clear existing checkbox references

        List<Map<String, String>> registeredCourses = controller.getDetailedRegisteredCourses();

        if (registeredCourses.isEmpty()) {
            JLabel noCoursesLabel = new JLabel("No courses are currently registered.", SwingConstants.CENTER);
            noCoursesLabel.setFont(new Font("Segoe UI", Font.ITALIC, 18));
            noCoursesLabel.setForeground(DARK_GRAY);
            // Center the "No courses" message vertically
            coursesPanel.add(Box.createVerticalGlue());
            coursesPanel.add(noCoursesLabel);
            coursesPanel.add(Box.createVerticalGlue());
        } else {
            JLabel header = new JLabel("Select courses to drop:");
            header.setFont(new Font("Segoe UI", Font.BOLD, 18));
            header.setForeground(DARK_GRAY);
            header.setAlignmentX(Component.LEFT_ALIGNMENT); // Align header to left
            coursesPanel.add(header);
            coursesPanel.add(Box.createRigidArea(new Dimension(0, 15))); // Spacing below header

            for (Map<String, String> course : registeredCourses) {
                String courseCode = course.get("code");
                String courseName = course.get("name");
                String credits = course.get("credits");

                JPanel courseEntryPanel = new JPanel(new BorderLayout(10, 0)); // Add horizontal gap
                courseEntryPanel.setBackground(Color.WHITE);
                courseEntryPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(PRIMARY_BLUE.brighter(), 1),
                        BorderFactory.createEmptyBorder(10, 15, 10, 15) // Inner padding
                ));
                courseEntryPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60)); // Fixed height for each entry

                JCheckBox checkBox = new JCheckBox();
                checkBox.setBackground(Color.WHITE);
                checkBox.setToolTipText("Select to drop " + courseCode); // Tooltip on hover
                courseCheckboxes.put(courseCode, checkBox); // Store reference for later retrieval

                JLabel courseLabel = new JLabel(String.format("<html><b>%s</b> - %s <br><i>Credits: %s</i></html>", courseCode, courseName, credits));
                courseLabel.setFont(new Font("Segoe UI", Font.PLAIN, 15));
                courseLabel.setForeground(TEXT_COLOR);

                courseEntryPanel.add(checkBox, BorderLayout.WEST);
                courseEntryPanel.add(courseLabel, BorderLayout.CENTER);

                coursesPanel.add(courseEntryPanel);
                coursesPanel.add(Box.createRigidArea(new Dimension(0, 10))); // Spacing between course entries
            }
        }
        coursesPanel.revalidate(); // Re-layout the components
        coursesPanel.repaint(); // Redraw the panel
    }

    private JPanel createActionPanel() {
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 20)); // Spacing between components
        actionPanel.setBackground(PRIMARY_BLUE);
        actionPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        totalCreditsLabel = new JLabel("Total Registered Credits: 0", SwingConstants.CENTER);
        totalCreditsLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalCreditsLabel.setForeground(Color.WHITE);
        actionPanel.add(totalCreditsLabel);

        JButton dropSelectedBtn = createStyledButton("Drop Selected Courses", Color.ORANGE.darker(), Color.WHITE, Color.ORANGE.darker().darker());
        dropSelectedBtn.addActionListener(e -> dropSelectedCourses());
        actionPanel.add(dropSelectedBtn);

        return actionPanel;
    }

    private void dropSelectedCourses() {
        List<String> coursesToDrop = new ArrayList<>();
        for (Map.Entry<String, JCheckBox> entry : courseCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                coursesToDrop.add(entry.getKey());
            }
        }

        if (coursesToDrop.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select at least one course to drop.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Confirmation dialog
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to drop the following courses?\n" + String.join("\n", coursesToDrop),
                "Confirm Drop", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean allDroppedSuccessfully = true;
            for (String courseCode : coursesToDrop) {
                if (!controller.dropCourse(courseCode)) { // Delegate drop action to controller
                    allDroppedSuccessfully = false;
                    JOptionPane.showMessageDialog(this, "Failed to drop " + courseCode + ". It might not be registered.", "Drop Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            if (allDroppedSuccessfully) {
                JOptionPane.showMessageDialog(this, "Selected course(s) dropped successfully!", "Drop Success", JOptionPane.INFORMATION_MESSAGE);
            }
            populateRegisteredCourses(); // Refresh the list of courses displayed
            updateTotalCreditsDisplay(); // Update the total credits shown
        }
    }

    private void updateTotalCreditsDisplay() {
        int totalCredits = controller.getModel().getTotalRegisteredCredits(); // Get total credits from the model
        totalCreditsLabel.setText("Total Registered Credits: " + totalCredits);
    }

    // Helper method to apply consistent styling to JButtons.
    private JButton createStyledButton(String text, Color bgColor, Color fgColor, Color hoverColor) {
        JButton button = new JButton(text);
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setFont(new Font("Segoe UI", Font.BOLD, 14));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(hoverColor);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
            }
        });
        return button;
    }
}
