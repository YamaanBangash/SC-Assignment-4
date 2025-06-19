import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator; // For sorting semesters
import java.util.Collections; // For Collections.unmodifiableMap

public class GradeModel {
    private String regId;
    private int admissionYear;

    // Stores grades: semester name -> (course code -> grade points)
    private final Map<String, Map<String, Double>> semesterGrades;
    // Stores course information: course code -> CourseInfo (name, credit hours)
    private final Map<String, CourseInfo> courseCatalog;

    public GradeModel(String regId) {
        this.regId = regId;
        this.admissionYear = extractAdmissionYear(regId);

        semesterGrades = new HashMap<>();
        courseCatalog = new HashMap<>();
        loadCourseCatalog(); // Load all possible courses once
        loadDummyDataForAdmissionYear(); // Load data specific to the student's admission year
    }

    /**
     * Extracts the admission year from the registration ID.
     * Assumes the first four digits of the registration ID represent the admission year.
     * @param regId The student's registration ID.
     * @return The admission year, or a default of 2000 if parsing fails.
     */
    private int extractAdmissionYear(String regId) {
        try {
            // Check if regId is long enough to extract a 4-digit year
            if (regId != null && regId.length() >= 4) {
                return Integer.parseInt(regId.substring(0, 4));
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing admission year from regId: " + regId + ". Using default. " + e.getMessage());
        }
        return 2000; // default/fallback
    }

    /**
     * Loads a generic course catalog.
     * This data is independent of the student's admission year.
     */
    private void loadCourseCatalog() {
        courseCatalog.put("CS101", new CourseInfo("Intro to CS", "CS101", 3));
        courseCatalog.put("MA101", new CourseInfo("Calculus I", "MA101", 3));
        courseCatalog.put("PH101", new CourseInfo("Physics I", "PH101", 3));
        courseCatalog.put("CS102", new CourseInfo("Data Structures", "CS102", 3));
        courseCatalog.put("EE101", new CourseInfo("Basic Electrical Eng.", "EE101", 2));
        courseCatalog.put("UR101", new CourseInfo("Urdu", "UR101", 3));
        courseCatalog.put("IS101", new CourseInfo("Islamic Studies", "IS101", 2));
        courseCatalog.put("CS201", new CourseInfo("Object-Oriented Prog.", "CS201", 3));
        courseCatalog.put("MA201", new CourseInfo("Calculus II", "MA201", 3));
        courseCatalog.put("PH201", new CourseInfo("Physics II", "PH201", 3));
        courseCatalog.put("CS202", new CourseInfo("Operating Systems", "CS202", 3));
        courseCatalog.put("DE101", new CourseInfo("Digital Electronics", "DE101", 3));
        courseCatalog.put("PK101", new CourseInfo("Pakistan Studies", "PK101", 2));
        courseCatalog.put("EC101", new CourseInfo("Engineering Economics", "EC101", 3));
        courseCatalog.put("DM301", new CourseInfo("Discrete Mathematics", "DM301", 3));
        courseCatalog.put("DB301", new CourseInfo("Database Systems", "DB301", 3));
        courseCatalog.put("NW301", new CourseInfo("Computer Networks", "NW301", 3));
    }

    /**
     * Loads dummy academic data specific to the student's admission year.
     * This will simulate a student's progress based on their start year.
     */
    private void loadDummyDataForAdmissionYear() {
        // Clear previous dummy data if any, to ensure data is specific to this regId
        semesterGrades.clear();

        // Generate grades for a few semesters based on admission year
        // We'll simulate 4-5 semesters of data for any given student
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int semestersToGenerate = Math.min(5, (currentYear - admissionYear) * 2 + 2); // Max 5 semesters or till current

        for (int i = 0; i < semestersToGenerate; i++) {
            String semesterName;
            Map<String, Double> gradesForThisSemester = new HashMap<>();

            int yearOffset = i / 2; // 0 for Fall X, Spring X+1; 1 for Fall X+1, Spring X+2
            int academicYear = admissionYear + yearOffset;

            if (i % 2 == 0) { // Even index: Fall semester
                semesterName = "Fall " + academicYear;
            } else { // Odd index: Spring semester
                semesterName = "Spring " + (academicYear + 1);
            }
            
            // Add some generic courses for each "simulated" semester
            // This ensures every generated semester has some data
            if (i == 0) { // First semester (Fall of admission year)
                gradesForThisSemester.put("CS101", 3.5 + (admissionYear % 5 * 0.1)); // Vary grades slightly by year
                gradesForThisSemester.put("MA101", 3.0 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("PH101", 3.2 + (admissionYear % 5 * 0.1));
            } else if (i == 1) { // Second semester (Spring of admission year + 1)
                gradesForThisSemester.put("CS102", 3.3 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("EE101", 3.0 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("UR101", 3.8 + (admissionYear % 5 * 0.1));
            } else if (i == 2) { // Third semester (Fall of admission year + 1)
                gradesForThisSemester.put("CS201", 4.0 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("MA201", 3.7 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("IS101", 3.5 + (admissionYear % 5 * 0.1));
            } else if (i == 3) { // Fourth semester (Spring of admission year + 2)
                gradesForThisSemester.put("CS202", 3.3 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("PH201", 2.7 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("DE101", 3.0 + (admissionYear % 5 * 0.1));
            } else if (i == 4) { // Fifth semester (Fall of admission year + 2)
                gradesForThisSemester.put("DM301", 3.6 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("DB301", 3.9 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("NW301", 3.4 + (admissionYear % 5 * 0.1));
            }

            // Ensure grades are clamped between 0.0 and 4.0
            gradesForThisSemester.replaceAll((k, v) -> Math.min(4.0, Math.max(0.0, v)));

            if (!gradesForThisSemester.isEmpty()) {
                semesterGrades.put(semesterName, Collections.unmodifiableMap(new HashMap<>(gradesForThisSemester)));
            }
        }
    }

    /**
     * Generates a list of semesters accessible to the student based on their admission year
     * and the current academic year.
     * This method no longer filters by `semesterGrades.keySet()`, as it's meant to show
     * all plausible semesters for the student.
     * @return A sorted list of accessible semester strings (e.g., "Fall 2022", "Spring 2023").
     */
    public List<String> getAccessibleSemesters() {
        List<String> accessibleSemesters = new ArrayList<>();
        int currentCalendarYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH); // Calendar.JANUARY is 0

        // Determine the current academic semester based on month
        // Assume Fall starts Sept, Spring starts Feb, Summer starts June
        String currentAcademicSemester = "";
        if (currentMonth >= Calendar.SEPTEMBER || currentMonth <= Calendar.JANUARY) { // Fall (Sept-Jan)
            currentAcademicSemester = "Fall " + currentCalendarYear;
        } else if (currentMonth >= Calendar.FEBRUARY && currentMonth <= Calendar.MAY) { // Spring (Feb-May)
            currentAcademicSemester = "Spring " + currentCalendarYear;
        } else if (currentMonth >= Calendar.JUNE && currentMonth <= Calendar.AUGUST) { // Summer (June-Aug)
            currentAcademicSemester = "Summer " + currentCalendarYear;
        }


        // Generate semesters from admission year up to the current academic semester
        // Iterate slightly beyond current year to capture spring/summer of current academic year
        for (int year = admissionYear; year <= currentCalendarYear + 1; year++) {
            String fallSem = "Fall " + year;
            String springSem = "Spring " + (year + 1); // Spring semester is in the next calendar year
            String summerSem = "Summer " + year; // Summer in the current calendar year

            // Only add semesters that are in the past or currently ongoing
            // Adjust condition for each semester type
            if (isSemesterBeforeOrEqual(fallSem, currentAcademicSemester)) {
                accessibleSemesters.add(fallSem);
            }
            if (isSemesterBeforeOrEqual(springSem, currentAcademicSemester)) {
                accessibleSemesters.add(springSem);
            }
            // Add Summer if it's relevant to the current year and before/equal to current academic semester
            if (year >= admissionYear && isSemesterBeforeOrEqual(summerSem, currentAcademicSemester)) {
                 accessibleSemesters.add(summerSem);
            }
        }

        // Sort semesters chronologically for a clean display
        accessibleSemesters.sort(new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                int year1 = Integer.parseInt(s1.substring(s1.lastIndexOf(" ") + 1));
                int year2 = Integer.parseInt(s2.substring(s2.lastIndexOf(" ") + 1));

                if (year1 != year2) {
                    return Integer.compare(year1, year2);
                }

                String type1 = s1.substring(0, s1.indexOf(" "));
                String type2 = s2.substring(0, s2.indexOf(" "));

                // Custom order: Fall, Spring, Summer (for same calendar year)
                // Note: Spring of Y+1 comes after Fall of Y
                // So, order for comparison: Fall Y, Summer Y, Spring Y+1
                Map<String, Integer> order = new HashMap<>();
                order.put("Fall", 1);
                order.put("Summer", 2);
                order.put("Spring", 3); // Spring of Y+1 chronologically comes after Fall/Summer of Y

                return Integer.compare(order.getOrDefault(type1, 0), order.getOrDefault(type2, 0));
            }
        });

        // Remove duplicates and ensure only unique semesters are returned
        // (can happen if logic for Spring/Summer in next calendar year overlaps with prev year's current year logic)
        List<String> uniqueSortedSemesters = new ArrayList<>();
        for (String sem : accessibleSemesters) {
            if (!uniqueSortedSemesters.contains(sem)) {
                uniqueSortedSemesters.add(sem);
            }
        }
        return uniqueSortedSemesters;
    }

    /**
     * Helper to compare semesters chronologically.
     * @param sem1 Semester string (e.g., "Fall 2023")
     * @param sem2 Semester string (e.g., "Spring 2024")
     * @return true if sem1 is chronologically before or the same as sem2.
     */
    private boolean isSemesterBeforeOrEqual(String sem1, String sem2) {
        // Basic format check
        if (!sem1.matches("(Fall|Spring|Summer) \\d{4}") || !sem2.matches("(Fall|Spring|Summer) \\d{4}")) {
            return false;
        }

        int year1 = Integer.parseInt(sem1.substring(sem1.lastIndexOf(" ") + 1));
        int year2 = Integer.parseInt(sem2.substring(sem2.lastIndexOf(" ") + 1));
        String type1 = sem1.substring(0, sem1.indexOf(" "));
        String type2 = sem2.substring(0, sem2.indexOf(" "));

        if (year1 < year2) return true;
        if (year1 > year2) return false;

        // Same year, compare semester type
        Map<String, Integer> order = new HashMap<>();
        order.put("Fall", 1);
        order.put("Spring", 3); // Spring is usually after Fall/Summer in *academic* year context
        order.put("Summer", 2);

        // If semesters are in the same calendar year, compare their order
        return order.getOrDefault(type1, 0) <= order.getOrDefault(type2, 0);
    }


    /**
     * Calculates and formats the GPA for a given semester, including course grades and credit hours.
     * @param semester The semester name (e.g., "Fall 2023").
     * @return A formatted HTML string displaying semester grades and GPA, or an error message.
     */
    public String getSemesterGPA(String semester) {
        if (!semesterGrades.containsKey(semester)) {
            return "<html><body style='font-family: Segoe UI;'><h3>No grades available</h3>" +
                   "<p style='color: #E74C3C;'>No grades recorded for " + semester + ".</p></body></html>";
        }

        Map<String, Double> grades = semesterGrades.get(semester);
        double totalGradePoints = 0;
        int totalCreditHours = 0;
        StringBuilder sb = new StringBuilder("<html><body style='font-family: Segoe UI;'>");
        sb.append("<h3>Grades for ").append(semester).append("</h3>");
        sb.append("<table border='1' cellpadding='5' cellspacing='0' style='width:100%; border-collapse: collapse;'>"
                 + "<tr style='background-color:#E0E0E0; text-align:left;'>")
          .append("<th style='padding: 8px;'>Course Code</th>")
          .append("<th style='padding: 8px;'>Course Name</th>")
          .append("<th style='padding: 8px;'>Grade (GPA)</th>")
          .append("<th style='padding: 8px;'>Credit Hours</th></tr>");

        boolean dataFound = false;
        for (Map.Entry<String, Double> entry : grades.entrySet()) {
            String courseCode = entry.getKey();
            Double gradePoints = entry.getValue();
            CourseInfo courseInfo = courseCatalog.get(courseCode);

            if (courseInfo != null) {
                totalGradePoints += (gradePoints * courseInfo.creditHours);
                totalCreditHours += courseInfo.creditHours;
                sb.append("<tr>")
                  .append("<td style='padding: 8px;'>").append(courseCode).append("</td>")
                  .append("<td style='padding: 8px;'>").append(courseInfo.name).append("</td>")
                  .append("<td style='padding: 8px;'>").append(String.format("%.2f", gradePoints)).append("</td>")
                  .append("<td style='padding: 8px;'>").append(courseInfo.creditHours).append("</td>")
                  .append("</tr>");
                dataFound = true;
            } else {
                // If course info is not found, still list the grade points from the semester,
                // but indicate missing info.
                sb.append("<tr>")
                  .append("<td style='padding: 8px;'>").append(courseCode).append("</td>")
                  .append("<td style='padding: 8px; color: #E74C3C;'><i>Info Missing</i></td>")
                  .append("<td style='padding: 8px;'>").append(String.format("%.2f", gradePoints)).append("</td>")
                  .append("<td style='padding: 8px; color: #E74C3C;'><i>N/A</i></td>")
                  .append("</tr>");
            }
        }

        if (!dataFound && grades.isEmpty()) { // Check if no courses or no valid courses were processed
             return "<html><body style='font-family: Segoe UI;'><h3>No grades available</h3>" +
                   "<p style='color: #E74C3C;'>No valid course data found for " + semester + ".</p></body></html>";
        }

        double semesterGPA = (totalCreditHours > 0) ? (totalGradePoints / totalCreditHours) : 0.0;
        sb.append("</table>");
        sb.append("<br><p><b>Overall Semester GPA: <span style='color: ").append(
                  semesterGPA >= 3.5 ? "#2ECC71" : (semesterGPA >= 3.0 ? "#F1C40F" : "#E74C3C")
              ).append(";'>").append(String.format("%.2f", semesterGPA)).append("</span></b></p></body></html>");
        return sb.toString();
    }

    /**
     * Retrieves the student's specific grade for a given course code.
     * It iterates through all semesters to find the grade for the specified course.
     * @param courseCode The code of the course (e.g., "CS101").
     * @return A formatted HTML string displaying the course information and the student's grade,
     * or an error message if the course or grade is not found.
     */
    public String getCourseGPA(String courseCode) {
        CourseInfo info = courseCatalog.get(courseCode.toUpperCase()); // Ensure case-insensitivity for lookup
        if (info == null) {
            return "<html><body style='font-family: Segoe UI;'>" +
                   "<p style='color: #E74C3C;'>Course code <b>" + courseCode + "</b> not found in catalog.</p></body></html>";
        }

        Double studentGrade = null;
        String semesterFoundIn = null;
        for (Map.Entry<String, Map<String, Double>> semEntry : semesterGrades.entrySet()) {
            if (semEntry.getValue().containsKey(courseCode.toUpperCase())) {
                studentGrade = semEntry.getValue().get(courseCode.toUpperCase());
                semesterFoundIn = semEntry.getKey();
                break; // Found the grade, no need to search further
            }
        }

        if (studentGrade == null) {
            return "<html><body style='font-family: Segoe UI;'>" +
                   "<p style='color: #E74C3C;'>You have no grade recorded for course: <b>" + courseCode + "</b> (" + info.name + ").</p></body></html>";
        }

        return "<html><body style='font-family: Segoe UI;'>" +
               "<h3>Course Details:</h3>" +
               "<table border='1' cellpadding='5' cellspacing='0' style='width:100%; border-collapse: collapse;'>" +
               "<tr style='background-color:#E0E0E0; text-align:left;'><th style='padding: 8px;'>Detail</th><th style='padding: 8px;'>Value</th></tr>" +
               "<tr><td style='padding: 8px;'>Course Name</td><td style='padding: 8px;'><b>" + info.name + "</b></td></tr>" +
               "<tr><td style='padding: 8px;'>Course Code</td><td style='padding: 8px;'><b>" + info.code + "</b></td></tr>" +
               "<tr><td style='padding: 8px;'>Credit Hours</td><td style='padding: 8px;'>" + info.creditHours + "</td></tr>" +
               "<tr><td style='padding: 8px;'>Your Grade (GPA)</td><td style='padding: 8px;'><b><span style='color: " +
                  (studentGrade >= 3.5 ? "#2ECC71" : (studentGrade >= 3.0 ? "#F1C40F" : "#E74C3C"))
               + ";'>" + String.format("%.2f", studentGrade) + "</span></b></td></tr>" +
               "<tr><td style='padding: 8px;'>Semester Taken</td><td style='padding: 8px;'>" + semesterFoundIn + "</td></tr>" +
               "</table></body></html>";
    }

    /**
     * Private static nested class to hold course information including credit hours.
     */
    private static class CourseInfo {
        String name;
        String code;
        int creditHours;

        CourseInfo(String name, String code, int creditHours) {
            this.name = name;
            this.code = code;
            this.creditHours = creditHours;
        }
    }
}