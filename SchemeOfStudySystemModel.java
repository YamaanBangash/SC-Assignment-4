import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.file.Files; // Still useful for reading all lines of a text file
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Removed PDFBox imports:
// import org.apache.pdfbox.pdmodel.PDDocument;
// import org.apache.pdfbox.text.PDFTextStripper;

public class SchemeOfStudySystemModel {

    private Map<String, List<File>> semesterFiles = new HashMap<>();

    public SchemeOfStudySystemModel() {
        setupSampleFiles();
    }

    private void setupSampleFiles() {
        semesterFiles.put("Spring", new ArrayList<>());
        semesterFiles.put("Fall", new ArrayList<>());

        try {
            // For text file
            File f1 = new File("Spring_Scheme1.txt");
            if (!f1.exists()) {
                try (PrintWriter pw = new PrintWriter(f1)) {
                    pw.println("Spring Scheme of Study - File 1 (TXT)\nCourse list: Math, Physics, CS...");
                }
            }
            semesterFiles.get("Spring").add(f1);

            // Removed dummy PDF file creation and addition
            // File pdfFile = new File("Spring_Scheme_PDF.pdf");
            // if (!pdfFile.exists()) {
            //     try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            //         String dummyPdfContent = "%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj 2 0 obj<</Type/Pages/Count 0>>endobj\nxref\n0 3\n0000000000 65535 f\n0000000009 00000 n\n0000000052 00000 n\ntrailer<</Size 3/Root 1 0 R>>startxref\n108\n%%EOF";
            //         fos.write(dummyPdfContent.getBytes());
            //     }
            // }
            // semesterFiles.get("Spring").add(pdfFile); // Removed adding the dummy PDF file

            File f3 = new File("Fall_Scheme1.txt");
            if (!f3.exists()) {
                try (PrintWriter pw = new PrintWriter(f3)) {
                    pw.println("Fall Scheme of Study - File 1 (TXT)\nCourse list: English, History...");
                }
            }
            semesterFiles.get("Fall").add(f3);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<File> getFilesForSemester(String semesterType) {
        return semesterFiles.getOrDefault(semesterType, new ArrayList<>());
    }

    public String loadFileContent(File file) throws IOException {
        // Now only assumes text files
        // No more checking for PDF extension
        List<String> lines = Files.readAllLines(file.toPath());
        return String.join("\n", lines);
    }

    // Removed the loadPdfContent method as it's no longer needed
    // private String loadPdfContent(File pdfFile) throws IOException { ... }

    public void copyFile(File source, File dest) throws IOException {
        try (InputStream is = new FileInputStream(source);
             OutputStream os = new FileOutputStream(dest)) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
    }
}