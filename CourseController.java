import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CourseController {
    private final CourseModel model;
    private Set<String> currentlySelectedCourseCodes; // To hold courses selected by the user, but not yet registered

    private static final int MAX_CREDIT_HOURS = 18; // Defined max credit hours here

    public CourseController(CourseModel model) {
        this.model = model;
        this.currentlySelectedCourseCodes = new HashSet<>();
    }

    // Public getter for the model (needed by UI classes to access model's methods)
    public CourseModel getModel() {
        return model;
    }

    // Methods related to retrieving course data from the model
    public List<Map<String, String>> getCoursesBySemester(int semester) {
        return model.getCoursesBySemester(semester);
    }

    public Map<String, String> getCourseDetails(String courseCode) {
        return model.getCourseDetails(courseCode);
    }

    public boolean isCourseAlreadyRegistered(String courseCode) {
        return model.isCourseAlreadyRegistered(courseCode);
    }

    // Methods for managing *currently selected* courses (pre-registration)

    public void addSelectedCourse(String courseCode) {
        currentlySelectedCourseCodes.add(courseCode);
    }

    public void removeSelectedCourse(String courseCode) {
        currentlySelectedCourseCodes.remove(courseCode);
    }

    public Set<String> getCurrentlySelectedCourseCodes() {
        return new HashSet<>(currentlySelectedCourseCodes); // Return a copy
    }

    public int getCurrentlySelectedTotalCredits() {
        int totalCredits = 0;
        for (String courseCode : currentlySelectedCourseCodes) {
            totalCredits += model.getCourseCredit(courseCode);
        }
        return totalCredits;
    }

    public void clearCurrentlySelectedCourseCodes() {
        currentlySelectedCourseCodes.clear();
    }

    // Validation method for 18 credit hour limit
    public boolean canEnrollTheseCourses(Set<String> coursesToAttemptEnroll) {
        int currentRegisteredCredits = model.getTotalRegisteredCredits();
        int prospectiveNewCredits = 0;

        for (String courseCode : coursesToAttemptEnroll) {
            // Only count credits for courses that are NOT already permanently registered
            if (!model.isCourseAlreadyRegistered(courseCode)) {
                prospectiveNewCredits += model.getCourseCredit(courseCode);
            }
        }
        return (currentRegisteredCredits + prospectiveNewCredits) <= MAX_CREDIT_HOURS;
    }

    // Registration method
    public boolean registerSelectedCourses() {
        if (currentlySelectedCourseCodes.isEmpty()) {
            System.out.println("Controller: No courses selected in cart to register.");
            return false;
        }

        // Filter out courses that are already permanently registered in the model
        Set<String> coursesToActuallyRegister = currentlySelectedCourseCodes.stream()
                .filter(code -> !model.isCourseAlreadyRegistered(code))
                .collect(Collectors.toSet());

        if (coursesToActuallyRegister.isEmpty()) {
            System.out.println("Controller: All selected courses are already registered or no new unique courses selected.");
            return true;
        }

        // Validate total credit hours *before* committing to model
        int currentRegisteredCredits = model.getTotalRegisteredCredits();
        int newCreditsFromThisBatch = 0;
        for (String code : coursesToActuallyRegister) {
            newCreditsFromThisBatch += model.getCourseCredit(code);
        }

        if ((currentRegisteredCredits + newCreditsFromThisBatch) > MAX_CREDIT_HOURS) {
            System.out.println("Controller: Cannot register these courses, exceeds MAX_CREDIT_HOURS.");
            return false; // Validation failed
        }

        model.registerCourses(coursesToActuallyRegister); // Pass only the truly new courses
        System.out.println("Controller: Successfully registered: " + coursesToActuallyRegister);
        clearCurrentlySelectedCourseCodes(); // Clear temporary selections after registration
        return true;
    }

    // Method to get detailed information about registered courses for ViewRegisteredCourses
    public List<Map<String, String>> getDetailedRegisteredCourses() {
        List<Map<String, String>> detailedCourses = new ArrayList<>();
        Set<String> registeredCodes = model.getRegisteredCoursesInModel(); // Get codes from model
        for (String code : registeredCodes) {
            Map<String, String> details = model.getCourseDetails(code);
            if (details != null) {
                detailedCourses.add(details);
            }
        }
        return detailedCourses;
    }

    // Method to drop a registered course
    public boolean dropCourse(String courseCode) {
        return model.dropCourse(courseCode);
    }

    // --- Scheme of Study related methods ---

    // Hardcoded content for the scheme PDFs (extracted textual content)
    private static final String SCHEME_2021_CONTENT =
        "Department of Computer Science\n" +
        "BS (Computer Science) Scheme of Study (Revised)\n" +
        "Effective From 2021 Semester\n" +
        "The following table:\n" +
        "\"(For eighth batch of BSCS admitted\n" +
        "\",\"students and onward future new batches)\n" +
        "\"\n" +
        "\"Semester 1\n" +
        "\",\"Semester 2\n" +
        "\"\n" +
        "\"EN-101: English - 1\n" +
        "3\n" +
        "\",\"EN-102: English - 2\n" +
        "3\n" +
        "\"\n" +
        "\"PS-101: Pakistan Studies\n" +
        "2\n" +
        "\",\"IS-101: Islamic Studies\n" +
        "2\n" +
        "\"\n" +
        "\"MA-101: Calculus & Analytical Geometry-I\n" +
        "3\n" +
        "\",\"MA-102: Calculus & Analytical Geometry-II\n" +
        "3\n" +
        "\"\n" +
        "\"PH-111: Introductory Mechanics & Waves\n" +
        "2\n" +
        "\",\"PH-112: Electricity, Magnetism and Thermal Physics\n" +
        "2\n" +
        "\"\n" +
        "\"PH-193: Introductory Mechanics & Waves Lab.\n" +
        "I\n" +
        "\",\"PH-194: Electricity, Magnetism and Thermal Physics Lab. 1\n" +
        "\"\n" +
        "\"CS-101: Introduction to Computing\n" +
        "3\n" +
        "\",\"MA 203: Discrete Mathematics\n" +
        "3\n" +
        "\"\n" +
        "\"CS-105: Problem Solving and Programming 3 (2, 1)\n" +
        "\",\"CS-121: Object Oriented Programming\n" +
        "4 (3,1)\n" +
        "\"\n" +
        "\"Total Credits\n" +
        "Semester 3\n" +
        "17\n" +
        "\",\"Total Credits\n" +
        "Semester 4\n" +
        "18\n" +
        "\"\n" +
        "\"CS 103: Introduction to Computer Organization\n" +
        "3\n" +
        "\",\"CH-100: General Chemistry\n" +
        "2\n" +
        "\"\n" +
        "\"EN-201: English - 3\n" +
        "3\n" +
        "\",\"CH-190: General Chemistry Lab.\n" +
        "1\n" +
        "\"\n" +
        "\"PY-101: Introduction to Psychology\n" +
        "3\n" +
        "\",\"MA-205: Differential Equations & Linear Algebra\n" +
        "3\n" +
        "CS-222: Analysis & Design for Software Systems\n" +
        "3\n" +
        "\"\n" +
        "\"CS-211: Data Structures\n" +
        "3\n" +
        "\",\"CS-225: Database System\n" +
        "3\n" +
        "\"\n" +
        "\"CS-291: Data Structures Lab.\n" +
        "1\n" +
        "\",\"CS-213: Computer Organization & Assembly Language 3\n" +
        "\"\n" +
        "\"CS-212: Human Computer Interaction\n" +
        "3\n" +
        "\",\"CS-293: Computer Organization & Assembly\n" +
        "Language Lab.\n" +
        "1\n" +
        "\"\n" +
        "\"16\n" +
        "Total Credits\n" +
        "\",\n" +
        ",\"Total Credits\n" +
        "16\n" +
        "\"\n" +
        "\"Semester 5\n" +
        "\",\"Semester 6\n" +
        "\"\n" +
        "\"ST-101: Probability & Statistics\n" +
        "3\n" +
        "\",\"EC-201: Economics\n" +
        "3\n" +
        "\"\n" +
        "\"BY:201: Introductory Biology\n" +
        "3\n" +
        "\",\"CS-331: Theory of Automata\n" +
        "3\n" +
        "\"\n" +
        "\"CS-223: Operating Systems\n" +
        "3\n" +
        "\",\"3\n" +
        "CS-312: Computer Communications & Networks\n" +
        "\"\n" +
        "\"CS-311: Analysis & Design of Algorithms\n" +
        "3\n" +
        "$3(2,1)$\n" +
        "CS-322: Software Construction\n" +
        "CS414-: Artificial Intelligence\n" +
        "3\n" +
        "\",\"CS423-: Computer Graphics\n" +
        "3\n" +
        "Elective Course-1 from CS Electives offered\n" +
        "3\n" +
        "\"\n" +
        "\"Total Credits\n" +
        "18\n" +
        "\",\"15\n" +
        "Total Credits\n" +
        "\"\n" +
        "\"Semester 7\n" +
        "\",\"Semester 8\n" +
        "\"\n" +
        "\"CS-489: Project-I\n" +
        "3\n" +
        "\",\"CS-490: Project-II\n" +
        "3\n" +
        "\"\n" +
        "\"CS-332: Net Centric Programming\n" +
        "3\n" +
        "\",\"CS-413: Introduction to Information Security\n" +
        "3\n" +
        "\"\n" +
        "\"CS-411: Compiler Construction\n" +
        "3\n" +
        "\",\"CS-449: ICT and Society\n" +
        "3\n" +
        "\"\n" +
        "\"Elective Course-2 from CS Electives offered\n" +
        "3\n" +
        "\",\"Elective Course-4 from CS Electives offered\n" +
        "3\n" +
        "\"\n" +
        "\"Elective Course-3 from CS Electives offered\n" +
        "3\n" +
        "Total Credits\n" +
        "15\n" +
        "\",\"Elective Course-5 from CS Electives offered\n" +
        "3\n" +
        "Total Credits\n" +
        "15\n" +
        "\"\n" +
        "\"BS(Computer Science) Elective Courses\n" +
        "3 (2, 1)\n" +
        "3 (2,1)\n" +
        "CS-324: Web Application Development\n" +
        "CS-442: Mobile Application Development\n" +
        "\",\"CS-471: Theory of Programming\n" +
        "3\n" +
        "3 (2,1)\n" +
        "CS-472: Information Interfaces\n" +
        "\"\n" +
        "\"CS-443: Network Architecture\n" +
        "3\n" +
        "\",\"CS-473: Multimedia Applications & Design\n" +
        "3\n" +
        "\"\n" +
        "\"CS-444: Knowledge Based Systems\n" +
        "3\n" +
        "\",\"CS-474: Software Testing Techniques\n" +
        "3\n" +
        "\"\n" +
        "\"CS-445: Information Systems\n" +
        "3\n" +
        "\",\"3\n" +
        "CS475-: Emerging Trends in Software Development\n" +
        "\"\n" +
        "\"CS-446: Introduction to Multimedia Communication\n" +
        "3\n" +
        "\",\"CS-476: Enterprise Information Infrastructure\n" +
        "3\n" +
        "\"\n" +
        "\"CS-448: Network Management\n" +
        "3\n" +
        "\",\"CS-478: Web Technologies\n" +
        "3\n" +
        "\"\n" +
        "\"CS-451: Introduction to Social Computing\n" +
        "3\n" +
        "\",\"CS-480: Selected Topics in CS\n" +
        "3\n" +
        "\"\n" +
        "\"3 (2,1)\n" +
        "CS-452: Introduction to Game Development\n" +
        "\",\"CS482-: Web Engineering\n" +
        "3\n" +
        "\"\n" +
        "\"CS-454: Introduction to Semantic Web\n" +
        "3\n" +
        "\",\"CS-483: Software Quality Assurance\n" +
        "3\n" +
        "\"\n" +
        "\"CS-455: Introduction to Natural Language\n" +
        "Processing 3\n" +
        "\",\"CS-484: Software Engineering\n" +
        "3\n" +
        "\"\n" +
        "\"3 (2, 1)\n" +
        "CS-456: Introduction to Web Services\n" +
        "\",\"CS-486: Software Project Management\n" +
        "3\n" +
        "\"\n" +
        "\"CS-457: Web Application Frameworks\n" +
        "3 (2,1)\n" +
        "CS-458: Introduction to Data Mining\n" +
        "3\n" +
        "\",\"CS487-: Formal Methods for Software Engineering\n" +
        "CS-488: Software Entrepreneurship\n" +
        "3\n" +
        "3\n" +
        "\"\n" +
        "\"CS-459: Introduction to Machine Learning\n" +
        "3\n" +
        "\",\"CS-491: Real Time Systems\n" +
        "3\n" +
        "\"\n" +
        "\"CS-462: Introduction to Cyber Security\n" +
        "CS464-: Modeling & Simulation\n" +
        "3\n" +
        "3\n" +
        "\",\"CS-497: Computing Case Studies\n" +
        "\"";

    private static final String SCHEME_2023_CONTENT =
        "Scheme of Study for semesters Fall 2023 onwards\n" +
        "Semester 1\n" +
        "Semester 2\n" +
        "The following table:\n" +
        "\"EN-100: Functional English\n" +
        "\",\"GE 3\n" +
        "\",\"EN-200: Expository Writing\n" +
        "\",\"GE 3\n" +
        "\"\n" +
        "\"PK-100: Ideology & Constitution of Pakistan\n" +
        "\",\"GE\n" +
        "2\n" +
        "\",\"IS-100: Islamic Studies / Ethics\n" +
        "\",\"GE 2\n" +
        "\"\n" +
        "\"MA-101: Calculus & Analytical Geometry\n" +
        "CSC-110: Applications of ICT\n" +
        "\",\"GE 3\n" +
        "GE\n" +
        "2+1\n" +
        "\",\"Social Sciences Elective\n" +
        "PH-110: Introductory Mechanics and Waves\n" +
        "\",\"GE 2\n" +
        "GE 3\n" +
        "\"\n" +
        "\"CSC-104: Problem Solving & Programming\n" +
        "CC\n" +
        "3+1\n" +
        "\",,\"MA-202: Multivariable Calculus\n" +
        "\",\"MS 3\n" +
        "\"\n" +
        ",,\"CSC-121: Object Oriented Programming\n" +
        "\",\"CC\n" +
        "3+1\n" +
        "\"\n" +
        "\"Credit Hours\n" +
        "\",\"15\n" +
        "\",\"Credit Hours\n" +
        "\",\"17\n" +
        "\"\n" +
        "\"Semester 3\n" +
        "\",,\"Semester 4\n" +
        "\",\n" +
        "\"MA-203: Discrete Mathematics\n" +
        "\",\"GE\n" +
        "3\n" +
        "\",\"Civics and Community Engagement\n" +
        "\",\"GE\n" +
        "2\n" +
        "\"\n" +
        "\"EN-2XX: Technical and Business Writing\n" +
        "\",\"MS\n" +
        "3\n" +
        "\",\"MA-302: Linear Algebra\n" +
        "\",\"MS\n" +
        "3\n" +
        "\"\n" +
        "\"CSC-103: Introduction to Computer Org.\n" +
        "\",\"3\n" +
        "CC\n" +
        "\",\"CSC-222: Analysis & Design of Software Systems\n" +
        "\",\"CC\n" +
        "3\n" +
        "\"\n" +
        "\"CSC-211: Data Structures\n" +
        "\",\"3+1\n" +
        "CC\n" +
        "\",\"CSC-224: Database Systems\n" +
        "\",\"3+1\n" +
        "CC\n" +
        "\"\n" +
        "\"CSC-212: Human Computer Interaction\n" +
        "\",\"DC\n" +
        "3\n" +
        "\",\"CSC-313: Computer Architecture\n" +
        "\",\"DC\n" +
        "3\n" +
        "\"\n" +
        "\"Credit Hours\n" +
        "\",\"16\n" +
        "\",\"Credit Hours\n" +
        "\",\"15\n" +
        "\"\n" +
        "\"Semester 5\n" +
        "\",,\"Semester 6\n" +
        "\",\n" +
        "\"ST-101: Probability and Statistics\n" +
        "\",\"MS\n" +
        "3\n" +
        "\",\"CSC-331: Theory of Automata\n" +
        "\",\"DC\n" +
        "3\n" +
        "\"\n" +
        "\"CSC-322: Software Construction\n" +
        "\",\"DC\n" +
        "2+1\n" +
        "\",\"CSC-325: Advanced Database Systems\n" +
        "\",\"DC\n" +
        "2+1\n" +
        "\"\n" +
        "\"CSC-226: Operating Systems\n" +
        "\",\"CC\n" +
        "2+1\n" +
        "\",\"CSC-215: Computer Org. & Assembly Language\n" +
        "\",\"CC\n" +
        "2+1\n" +
        "\"\n" +
        "\"CSC-311: Analysis & Design of Algorithms\n" +
        "\",\"CC\n" +
        "3\n" +
        "\",\"CSC-312: Computer Communications & Networks\n" +
        "CC\n" +
        "\",\"3\n" +
        "\"\n" +
        "\"CSC-414: Artificial Intelligence\n" +
        "\",\"CC 3\n" +
        "\",\"Domain Elective 2\n" +
        "\",\"DE\n" +
        "3\n" +
        "\"\n" +
        "\"Domain Elective 1\n" +
        "\",\"DE 3\n" +
        "\",\"Domain Elective 3\n" +
        "\",\"DE\n" +
        "3\n" +
        "\"\n" +
        "\"Credit Hours\n" +
        "\",\"18\n" +
        "\",\"Credit Hours\n" +
        "\",\"18\n" +
        "\"\n" +
        "\"Semester 7\n" +
        "\",,\"Semester 8\n" +
        "\",\n" +
        "\"Entrepreneurship\n" +
        "\",\"GE 2\n" +
        "\",\"Elective Supporting\n" +
        "\",\"ES 3\n" +
        "\"\n" +
        "\"Arts and Humanities Elective\n" +
        "\",\"2\n" +
        "GE\n" +
        "\",\"CSC-4XX: Introduction to Cyber Security\n" +
        "\",\"CC 3\n" +
        "\"\n" +
        "\"CSC-411: Compiler Construction\n" +
        "\",\"DC 3\n" +
        "\",\"CSC-490: Project-II\n" +
        "\",\"CC 4\n" +
        "\"\n" +
        "\"CSC-489: Project-I\n" +
        "\",\"CC 2\n" +
        "\",\"Domain Elective 6\n" +
        "\",\"DE 3\n" +
        "\"\n" +
        "\"Domain Elective 4\n" +
        "\",\"DE 3\n" +
        "\",\"Domain Elective 7\n" +
        "\",\"DE 3\n" +
        "\"\n" +
        "\"Domain Elective 5\n" +
        "\",\"DE 3\n" +
        "\",,\n" +
        "\"Credit Hours\n" +
        "\",\"15\n" +
        "\",\"Credit Hours\n" +
        "\",\"16\n" +
        "\"\n" +
        "The students must do an internship of six to eight weeks during their degree.\n" +
        "The internships will be coordinated by\n" +
        "the internship coordinator.\n" +
        "The following table:\n" +
        "\"Category\n" +
        "\",\"Abbr.\n" +
        "Cr. Hrs\n" +
        "\"\n" +
        "\"Computing Core\n" +
        "\",\"(CC) 46\n" +
        "\"\n" +
        "\"Domain Core\n" +
        "\",\"(DC)\n" +
        "18\n" +
        "\"\n" +
        "\"Domain Elective\n" +
        "\",\"(DE)\n" +
        "21\n" +
        "\"\n" +
        "\"Maths and Supporting\n" +
        "\",\"(MS)\n" +
        "12\n" +
        "\"\n" +
        "\"Elective Supporting\n" +
        "\",\"(ES)\n" +
        "3\n" +
        "\"\n" +
        "\"General Education\n" +
        "\",\"(GE)\n" +
        "30\n" +
        "\"";

    // Define File objects pointing to the expected PDF locations.
    // Assuming these PDF files (scheme-2021_copy.pdf and scheme-2023_copy.pdf)
    // are present in the directory where your Java application is run.
    private static final File SCHEME_2021_FILE = new File("scheme-2021_copy.pdf");
    private static final File SCHEME_2023_FILE = new File("scheme-2023_copy.pdf");


    // --- NEW: Grade Methods from GradeController ---

    /**
     * Loads dummy academic data specific to the student's admission year.
     * This will simulate a student's progress based on their start year.
     * This method needs to be called when a student's ID is set/changed.
     * @param regId The student's registration ID.
     */
    public void loadStudentGradeData(String regId) {
        model.loadDummyGradeDataForAdmissionYear(regId);
    }

    /**
     * Retrieves a list of semesters accessible to the student from the CourseModel.
     * @return A List of semester strings (e.g., "Fall 2023", "Spring 2024").
     */
    public List<String> getAccessibleGradeSemesters() {
        return model.getAccessibleGradeSemesters();
    }

    /**
     * Fetches and formats the semester GPA and individual course grades from the CourseModel.
     * The result is an HTML string.
     * @param semester The semester name (e.g., "Fall 2023").
     * @return An HTML formatted string with semester grades and GPA, or an error message.
     */
    public String getSemesterGPA(String semester) {
        return model.getSemesterGPA(semester);
    }

    /**
     * Fetches and formats the student's grade for a specific course from the CourseModel.
     * The result is an HTML string.
     * @param courseCode The code of the course (e.g., "CS101").
     * @return An HTML formatted string with course details and student's grade, or an error message.
     */
    public String getCourseGPA(String courseCode) {
        return model.getCourseGPA(courseCode);
    }

    public List<File> getSchemeFilesForSemester(String semesterType) {
        List<File> files = new ArrayList<>();
        if ("Spring".equalsIgnoreCase(semesterType)) {
            // Add the File object for the 2023 scheme
            files.add(SCHEME_2023_FILE);
        } else if ("Fall".equalsIgnoreCase(semesterType)) {
            // Add the File object for the 2021 scheme
            files.add(SCHEME_2021_FILE);
        }
        return files;
    }

    public String getSchemeFileContent(File file) throws IOException {
        String fileName = file.getName();
        if (SCHEME_2023_FILE.getName().equalsIgnoreCase(fileName)) {
            return SCHEME_2023_CONTENT;
        } else if (SCHEME_2021_FILE.getName().equalsIgnoreCase(fileName)) {
            return SCHEME_2021_CONTENT;
        }
        return "Content for '" + fileName + "' is not directly available or recognized by the application.";
    }

    public void downloadSchemeFile(File sourceFile, File destination) throws IOException {
        String contentToWrite;
        String fileName = sourceFile.getName();

        if (SCHEME_2023_FILE.getName().equalsIgnoreCase(fileName)) {
            contentToWrite = SCHEME_2023_CONTENT;
        } else if (SCHEME_2021_FILE.getName().equalsIgnoreCase(fileName)) {
            contentToWrite = SCHEME_2021_CONTENT;
        } else {
            throw new IOException("Content for '" + fileName + "' is not available for download.");
        }

        try (FileWriter writer = new FileWriter(destination)) {
            writer.write(contentToWrite);
        }
    }
}