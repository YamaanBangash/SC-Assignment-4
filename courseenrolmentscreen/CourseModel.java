package courseenrolmentscreen;

import java.util.ArrayList;
import java.util.List;

public class CourseModel {
    private final List<String> allCourses;
    private final List<String> selectedCourses;

    public CourseModel() {
        allCourses = List.of("CS-414 AI", "CS-311 ADA", "CS-233 OS", "CS-322 SC", "BY-201", "STAT-101");
        selectedCourses = new ArrayList<>();
    }

    public List<String> getAllCourses() {
        return allCourses;
    }

    public void registerCourse(String course) {
        if (!selectedCourses.contains(course) && allCourses.contains(course)) {
            selectedCourses.add(course);
        }
    }

    public void dropCourse(String course) {
        selectedCourses.remove(course);
    }

    public List<String> getSelectedCourses() {
        return new ArrayList<>(selectedCourses);
    }

    public void clearSelectedCourses() {
        selectedCourses.clear();
    }
} 