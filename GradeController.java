// GradeController.java
import java.util.List;

public class GradeController {
    private GradeModel model;

    public GradeController(String regId) {
        // Initialize the GradeModel with the provided registration ID
        this.model = new GradeModel(regId);
    }

    /**
     * Retrieves a list of semesters accessible to the student from the GradeModel.
     * @return A List of semester strings (e.g., "Fall 2023", "Spring 2024").
     */
    public List<String> getAccessibleSemesters() {
        return model.getAccessibleSemesters();
    }

    /**
     * Fetches and formats the semester GPA and individual course grades from the GradeModel.
     * The result is an HTML string.
     * @param semester The semester name (e.g., "Fall 2023").
     * @return An HTML formatted string with semester grades and GPA, or an error message.
     */
    public String getSemesterGPA(String semester) {
        return model.getSemesterGPA(semester);
    }

    /**
     * Fetches and formats the student's grade for a specific course from the GradeModel.
     * The result is an HTML string.
     * @param courseCode The code of the course (e.g., "CS101").
     * @return An HTML formatted string with course details and student's grade, or an error message.
     */
    public String getCourseGPA(String courseCode) {
        return model.getCourseGPA(courseCode);
    }
}