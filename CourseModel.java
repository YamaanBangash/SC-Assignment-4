import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors; // Added for grade data filtering


public class CourseModel {
    private List<Map<String, String>> allCourses;
    private Set<String> registeredCourses;
    private Map<String, List<File>> semesterSchemeFiles; // For Scheme of Study

    // --- NEW: Grade Data ---
    private String studentRegId; // To store the current student's registration ID for grade data
    // Stores grades: semester name -> (course code -> grade points)
    private final Map<String, Map<String, Double>> semesterGrades;
    // Stores course information: course code -> CourseInfo (name, credit hours)
    private final Map<String, CourseInfo> gradeCourseCatalog; // Renamed to avoid clash

    public CourseModel() {
        // Default constructor for general app initialization without a specific student ID
        this(null); // Call the parameterized constructor with null
    }

    public CourseModel(String regId) {
        this.studentRegId = regId; // Set the registration ID

        allCourses = new ArrayList<>();
        registeredCourses = new HashSet<>();
        semesterSchemeFiles = new HashMap<>();

        // Initialize grade-related fields
        semesterGrades = new HashMap<>();
        gradeCourseCatalog = new HashMap<>();

        initializeDummyCourses(); // Populate with course data
        setupSampleSchemeFiles(); // Populate with scheme of study dummy data
        loadGradeCourseCatalog(); // NEW: Load all possible courses for grades
        if (this.studentRegId != null) {
            loadDummyGradeDataForAdmissionYear(this.studentRegId); // NEW: Load grades specific to the student
        }
    }

    // Existing method for courses
    private void initializeDummyCourses() {
        allCourses.clear();

        // Semester 1
        allCourses.add(createCourse("CS-104", "Problem Solving & Programming", "Dr. Syed M Naqi", "3", 1));

        // Semester 2
        allCourses.add(createCourse("EN-200", "Expository Writing", "Ms. Saima Noor", "3", 2));
        allCourses.add(createCourse("IS-100", "Islamic Studies / Ethics", "Dr. Abdul Wahid", "2", 2));
        allCourses.add(createCourse("SO-101", "Social Sciences", "Dr. Abdul Rasheed", "3", 2));
        allCourses.add(createCourse("PH-110", "Introduction to Mechanics and Waves", "Ms. Maria", "3", 2));
        allCourses.add(createCourse("MA-202", "Multivariable Calculus", "Dr. Waseem", "3", 2));
        allCourses.add(createCourse("CS-120", "Object Oriented Programming", "Dr. Ghazanfar Farooq", "3", 2));

        // Semester 3
        allCourses.add(createCourse("CS-211", "Data Structures", "Dr. Ghazanfar Farooq/ Ms.Fatima Ijaz", "3", 3));

        // Semester 4
        allCourses.add(createCourse("SW-100", "Civics and Community Engagement", "Ms. Maria Jamshid", "1", 4));
        allCourses.add(createCourse("MA-302", "Linear Algebra", "Ms. Farwa Haider", "3", 4));
        allCourses.add(createCourse("CS-222", "Analysis and Design of Software Systems", "Dr. Onaiza Maqbool", "3", 4));
        allCourses.add(createCourse("CS-224", "Database Systems", "Dr. Shuaib Karim/ Ms. Maham", "3", 4));
        allCourses.add(createCourse("CS-313", "Computer Architecture", "Ms. Ifrah Farrukh Khan", "3", 4));

        // Semester 5
        allCourses.add(createCourse("CS-223", "Operating Systems", "Dr. Muazzam Khattak", "3", 5));
        allCourses.add(createCourse("CS-311", "Analysis and Design of Algorithms", "Dr. Akmal Khattak", "3", 5));
        allCourses.add(createCourse("CS-322", "Software Construction", "Dr. Onaiza Maqbool", "3", 5));
        allCourses.add(createCourse("CS-414", "Artificial Intelligence", "Dr. Ayaz Hussain", "3", 5));
        allCourses.add(createCourse("BY-201", "Introductory Biology", "Dr. Ambreen Rashid", "3", 5));
        allCourses.add(createCourse("ST-101", "Probability and Statistics", "Mr. Abdullah Iqbal", "3", 5));

        // Semester 6
        allCourses.add(createCourse("EC-201", "Economics", "Ms. Anum Fatima", "3", 6));
        allCourses.add(createCourse("CS-331", "Theory of Automata", "Dr. Umer Rashid", "3", 6));
        allCourses.add(createCourse("CS-312", "Computer Communications and Networks", "Ms. Ifrah Farrukh Khan", "3", 6));
        allCourses.add(createCourse("CS-423", "Computer Graphics", "Ms. Moomona Afhseen", "3", 6));
        allCourses.add(createCourse("CS-324", "Web Application Development", "Dr. Rabeeh Ayaz Abbas / Mr. Rashid", "3", 6));

        // Semester 7
        allCourses.add(createCourse("CS-489", "Project 1", "Dr. Adeel ur Rehman", "3", 7));
        allCourses.add(createCourse("CS-332", "Net Centric Programming", "Dr. Adeel ur Rehman", "3", 7));
        allCourses.add(createCourse("CS-411", "Compiler Construction", "Dr. S. M. Naqi", "3", 7));
        allCourses.add(createCourse("CS-457", "Web Application Framework", "Dr. Akmal Saeed Khattak / Mr. Rashid", "3", 7));
        allCourses.add(createCourse("CS-424", "Mobile Application Development", "Mr. Shahid Janjua", "3", 7));

        // Semester 8
        allCourses.add(createCourse("CS-490", "Project II", "Dr. Mudasar Sindhu", "3", 8));
        allCourses.add(createCourse("CS-474", "Software Testing Techniques", "Dr. Shuaib Karim", "3", 8));
        allCourses.add(createCourse("CS-449", "ICT and Society", "Dr. Muazzam Khattak", "3", 8));
        allCourses.add(createCourse("CS-413", "Introduction to Information Security", "Mr. Waqas Saleem", "3", 8));
        allCourses.add(createCourse("CS-535", "Cloud DevOps", "Mr. Waqas Saleem", "3", 8));
    }

    private Map<String, String> createCourse(String code, String name, String faculty, String credits, int semester) {
        Map<String, String> course = new HashMap<>();
        course.put("code", code);
        course.put("name", name);
        course.put("faculty", faculty);
        course.put("credits", credits);
        course.put("semester", String.valueOf(semester));
        return course;
    }

    public List<Map<String, String>> getCoursesBySemester(int semester) {
        List<Map<String, String>> coursesInSemester = new ArrayList<>();
        for (Map<String, String> course : allCourses) {
            if (Integer.parseInt(course.get("semester")) == semester) {
                coursesInSemester.add(course);
            }
        }
        return coursesInSemester;
    }

    public Map<String, String> getCourseDetails(String courseCode) {
        for (Map<String, String> course : allCourses) {
            if (course.get("code").equalsIgnoreCase(courseCode)) {
                return course;
            }
        }
        return null;
    }

    public boolean isCourseAlreadyRegistered(String courseCode) {
        return registeredCourses.contains(courseCode);
    }

    public void registerCourses(Set<String> coursesToRegister) {
        this.registeredCourses.addAll(coursesToRegister);
        System.out.println("Registered courses updated in model: " + registeredCourses);
    }

    public Set<String> getRegisteredCoursesInModel() {
        return new HashSet<>(registeredCourses);
    }

    public int getCourseCredit(String courseCode) {
        Map<String, String> course = getCourseDetails(courseCode);
        if (course != null && course.containsKey("credits")) {
            try {
                return Integer.parseInt(course.get("credits"));
            } catch (NumberFormatException e) {
                System.err.println("Invalid credits format for course " + courseCode + ": " + course.get("credits"));
                return 0;
            }
        }
        return 0;
    }

    public int getTotalRegisteredCredits() {
        int totalCredits = 0;
        for (String courseCode : registeredCourses) {
            totalCredits += getCourseCredit(courseCode);
        }
        return totalCredits;
    }

    public boolean dropCourse(String courseCode) {
        return registeredCourses.remove(courseCode); // Changed to return boolean directly
    }

    public List<Map<String, String>> getRegisteredCourses() {
        // Return detailed registered courses
        List<Map<String, String>> detailedCourses = new ArrayList<>();
        for (String code : registeredCourses) {
            Map<String, String> details = getCourseDetails(code);
            if (details != null) {
                detailedCourses.add(details);
            }
        }
        return detailedCourses;
    }


    // --- Scheme of Study related methods ---

    private void setupSampleSchemeFiles() {
        semesterSchemeFiles.put("Spring", new ArrayList<>());
        semesterSchemeFiles.put("Fall", new ArrayList<>());

        try {
            // These files will be empty, but their names will be passed to the controller
            // The controller will then provide the hardcoded content based on these names.
            File f_spring_2023 = new File("scheme-2023_copy.pdf");
            if (!f_spring_2023.exists()) {
                f_spring_2023.createNewFile(); // Create empty file
            }
            File f_fall_2021 = new File("scheme-2021_copy.pdf");
            if (!f_fall_2021.exists()) {
                f_fall_2021.createNewFile(); // Create empty file
            }

            semesterSchemeFiles.get("Spring").add(f_spring_2023);
            semesterSchemeFiles.get("Fall").add(f_fall_2021);

        } catch (Exception e) {
            System.err.println("Error setting up scheme files: " + e.getMessage());
        }
    }

    public List<File> getSchemeFilesForSemester(String semesterType) {
        return semesterSchemeFiles.getOrDefault(semesterType, new ArrayList<>());
    }

    // loadSchemeFileContent will not read from file, but from hardcoded string
    public String loadSchemeFileContent(File file) throws IOException {
        // This method will be handled by the CourseController, which will provide
        // the hardcoded content based on the file name. Model here provides minimal role.
        return ""; // Or throw an exception if the controller is truly responsible for content
    }

    // copySchemeFile will not copy binary, but write hardcoded string
    public void copySchemeFile(File source, File dest) throws IOException {
        // This method will be handled by the CourseController, which will write
        // the hardcoded content to the destination. Model here provides minimal role.
    }


    // --- NEW: Grade Methods from GradeModel ---

    /**
     * Extracts the admission year from the registration ID.
     * Assumes the first four digits of the registration ID represent the admission year.
     * @param regId The student's registration ID.
     * @return The admission year, or a default of 2000 if parsing fails.
     */
    private int extractAdmissionYear(String regId) {
        try {
            if (regId != null && regId.length() >= 4) {
                return Integer.parseInt(regId.substring(0, 4));
            }
        } catch (NumberFormatException e) {
            System.err.println("Error parsing admission year from regId: " + regId + ". Using default. " + e.getMessage());
        }
        return 2000; // default/fallback
    }

    /**
     * Loads a generic course catalog for grade display.
     * This data is independent of the student's admission year.
     */
    private void loadGradeCourseCatalog() {
        gradeCourseCatalog.put("CS101", new CourseInfo("Intro to CS", "CS101", 3));
        gradeCourseCatalog.put("MA101", new CourseInfo("Calculus I", "MA101", 3));
        gradeCourseCatalog.put("PH101", new CourseInfo("Physics I", "PH101", 3));
        gradeCourseCatalog.put("CS102", new CourseInfo("Data Structures", "CS102", 3));
        gradeCourseCatalog.put("EE101", new CourseInfo("Basic Electrical Eng.", "EE101", 2));
        gradeCourseCatalog.put("UR101", new CourseInfo("Urdu", "UR101", 3));
        gradeCourseCatalog.put("IS101", new CourseInfo("Islamic Studies", "IS101", 2));
        gradeCourseCatalog.put("CS201", new CourseInfo("Object-Oriented Prog.", "CS201", 3));
        gradeCourseCatalog.put("MA201", new CourseInfo("Calculus II", "MA201", 3));
        gradeCourseCatalog.put("PH201", new CourseInfo("Physics II", "PH201", 3));
        gradeCourseCatalog.put("CS202", new CourseInfo("Operating Systems", "CS202", 3));
        gradeCourseCatalog.put("DE101", new CourseInfo("Digital Electronics", "DE101", 3));
        gradeCourseCatalog.put("PK101", new CourseInfo("Pakistan Studies", "PK101", 2));
        gradeCourseCatalog.put("EC101", new CourseInfo("Engineering Economics", "EC101", 3));
        gradeCourseCatalog.put("DM301", new CourseInfo("Discrete Mathematics", "DM301", 3));
        gradeCourseCatalog.put("DB301", new CourseInfo("Database Systems", "DB301", 3));
        gradeCourseCatalog.put("NW301", new CourseInfo("Computer Networks", "NW301", 3));
    }

    /**
     * Loads dummy academic data specific to the student's admission year.
     * This will simulate a student's progress based on their start year.
     */
    public void loadDummyGradeDataForAdmissionYear(String regId) {
        this.studentRegId = regId;
        int admissionYear = extractAdmissionYear(regId);
        semesterGrades.clear(); // Clear previous dummy data

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int semestersToGenerate = Math.min(5, (currentYear - admissionYear) * 2 + 2);

        for (int i = 0; i < semestersToGenerate; i++) {
            String semesterName;
            Map<String, Double> gradesForThisSemester = new HashMap<>();

            int yearOffset = i / 2;
            int academicYear = admissionYear + yearOffset;

            if (i % 2 == 0) {
                semesterName = "Fall " + academicYear;
            } else {
                semesterName = "Spring " + (academicYear + 1);
            }

            // Add some generic courses for each "simulated" semester
            if (i == 0) {
                gradesForThisSemester.put("CS101", 3.5 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("MA101", 3.0 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("PH101", 3.2 + (admissionYear % 5 * 0.1));
            } else if (i == 1) {
                gradesForThisSemester.put("CS102", 3.3 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("EE101", 3.0 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("UR101", 3.8 + (admissionYear % 5 * 0.1));
            } else if (i == 2) {
                gradesForThisSemester.put("CS201", 4.0 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("MA201", 3.7 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("IS101", 3.5 + (admissionYear % 5 * 0.1));
            } else if (i == 3) {
                gradesForThisSemester.put("CS202", 3.3 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("PH201", 2.7 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("DE101", 3.0 + (admissionYear % 5 * 0.1));
            } else if (i == 4) {
                gradesForThisSemester.put("DM301", 3.6 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("DB301", 3.9 + (admissionYear % 5 * 0.1));
                gradesForThisSemester.put("NW301", 3.4 + (admissionYear % 5 * 0.1));
            }

            gradesForThisSemester.replaceAll((k, v) -> Math.min(4.0, Math.max(0.0, v)));

            if (!gradesForThisSemester.isEmpty()) {
                semesterGrades.put(semesterName, Collections.unmodifiableMap(new HashMap<>(gradesForThisSemester)));
            }
        }
    }

    /**
     * Generates a list of semesters accessible to the student based on their admission year
     * and the current academic year.
     * @return A sorted list of accessible semester strings (e.g., "Fall 2022", "Spring 2023").
     */
    public List<String> getAccessibleGradeSemesters() {
        List<String> accessibleSemesters = new ArrayList<>();
        int admissionYear = extractAdmissionYear(studentRegId); // Use student-specific admission year

        int currentCalendarYear = Calendar.getInstance().get(Calendar.YEAR);
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH);

        String currentAcademicSemester = "";
        if (currentMonth >= Calendar.SEPTEMBER || currentMonth <= Calendar.JANUARY) {
            currentAcademicSemester = "Fall " + currentCalendarYear;
        } else if (currentMonth >= Calendar.FEBRUARY && currentMonth <= Calendar.MAY) {
            currentAcademicSemester = "Spring " + currentCalendarYear;
        } else if (currentMonth >= Calendar.JUNE && currentMonth <= Calendar.AUGUST) {
            currentAcademicSemester = "Summer " + currentCalendarYear;
        }

        for (int year = admissionYear; year <= currentCalendarYear + 1; year++) {
            String fallSem = "Fall " + year;
            String springSem = "Spring " + (year + 1);
            String summerSem = "Summer " + year;

            if (isSemesterBeforeOrEqualForGrades(fallSem, currentAcademicSemester)) {
                accessibleSemesters.add(fallSem);
            }
            if (isSemesterBeforeOrEqualForGrades(springSem, currentAcademicSemester)) {
                accessibleSemesters.add(springSem);
            }
            if (year >= admissionYear && isSemesterBeforeOrEqualForGrades(summerSem, currentAcademicSemester)) {
                accessibleSemesters.add(summerSem);
            }
        }

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

                Map<String, Integer> order = new HashMap<>();
                order.put("Fall", 1);
                order.put("Summer", 2);
                order.put("Spring", 3);

                return Integer.compare(order.getOrDefault(type1, 0), order.getOrDefault(type2, 0));
            }
        });

        return accessibleSemesters.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Helper to compare semesters chronologically for grades.
     * @param sem1 Semester string (e.g., "Fall 2023")
     * @param sem2 Semester string (e.g., "Spring 2024")
     * @return true if sem1 is chronologically before or the same as sem2.
     */
    private boolean isSemesterBeforeOrEqualForGrades(String sem1, String sem2) {
        if (!sem1.matches("(Fall|Spring|Summer) \\d{4}") || !sem2.matches("(Fall|Spring|Summer) \\d{4}")) {
            return false;
        }

        int year1 = Integer.parseInt(sem1.substring(sem1.lastIndexOf(" ") + 1));
        int year2 = Integer.parseInt(sem2.substring(sem2.lastIndexOf(" ") + 1));
        String type1 = sem1.substring(0, sem1.indexOf(" "));
        String type2 = sem2.substring(0, sem2.indexOf(" "));

        if (year1 < year2) return true;
        if (year1 > year2) return false;

        Map<String, Integer> order = new HashMap<>();
        order.put("Fall", 1);
        order.put("Summer", 2);
        order.put("Spring", 3);

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
            CourseInfo courseInfo = gradeCourseCatalog.get(courseCode); // Use gradeCourseCatalog

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
                sb.append("<tr>")
                  .append("<td style='padding: 8px;'>").append(courseCode).append("</td>")
                  .append("<td style='padding: 8px; color: #E74C3C;'><i>Info Missing</i></td>")
                  .append("<td style='padding: 8px;'>").append(String.format("%.2f", gradePoints)).append("</td>")
                  .append("<td style='padding: 8px; color: #E74C3C;'><i>N/A</i></td>")
                  .append("</tr>");
            }
        }

        if (!dataFound && grades.isEmpty()) {
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
        CourseInfo info = gradeCourseCatalog.get(courseCode.toUpperCase()); // Ensure case-insensitivity for lookup
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