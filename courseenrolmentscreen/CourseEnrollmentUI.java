package courseenrolmentscreen;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class CourseEnrollmentUI extends JFrame {
    private final CourseModel courseModel;
    private final JList<String> availableCoursesList;
    private final JList<String> selectedCoursesList;
    
    public CourseEnrollmentUI() {
        courseModel = new CourseModel();
        
        setTitle("Course Enrollment System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Create main panel
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Available courses panel
        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.setBorder(BorderFactory.createTitledBorder("Available Courses"));
        availableCoursesList = new JList<>(courseModel.getAllCourses().toArray(new String[0]));
        availablePanel.add(new JScrollPane(availableCoursesList), BorderLayout.CENTER);
        
        // Selected courses panel
        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.setBorder(BorderFactory.createTitledBorder("Selected Courses"));
        selectedCoursesList = new JList<>(new DefaultListModel<>());
        selectedPanel.add(new JScrollPane(selectedCoursesList), BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        JButton registerButton = new JButton("Register >>");
        JButton dropButton = new JButton("<< Drop");
        JButton clearButton = new JButton("Clear All");
        
        registerButton.addActionListener(e -> registerSelectedCourse());
        dropButton.addActionListener(e -> dropSelectedCourse());
        clearButton.addActionListener(e -> clearAllCourses());
        
        buttonPanel.add(registerButton);
        buttonPanel.add(dropButton);
        buttonPanel.add(clearButton);
        
        // Add panels to main panel
        mainPanel.add(availablePanel);
        mainPanel.add(selectedPanel);
        
        // Add to frame
        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
        
        pack();
        setSize(600, 400);
        setLocationRelativeTo(null);
    }
    
    private void registerSelectedCourse() {
        String selected = availableCoursesList.getSelectedValue();
        if (selected != null) {
            courseModel.registerCourse(selected);
            updateSelectedCoursesList();
        }
    }
    
    private void dropSelectedCourse() {
        String selected = selectedCoursesList.getSelectedValue();
        if (selected != null) {
            courseModel.dropCourse(selected);
            updateSelectedCoursesList();
        }
    }
    
    private void clearAllCourses() {
        courseModel.clearSelectedCourses();
        updateSelectedCoursesList();
    }
    
    private void updateSelectedCoursesList() {
        DefaultListModel<String> model = new DefaultListModel<>();
        for (String course : courseModel.getSelectedCourses()) {
            model.addElement(course);
        }
        selectedCoursesList.setModel(model);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CourseEnrollmentUI().setVisible(true);
        });
    }
} 