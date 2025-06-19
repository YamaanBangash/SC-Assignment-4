import java.io.File;
import java.io.IOException;
import java.util.List;

public class SchemeOfStudySystemController {

    private SchemeOfStudySystemModel model;
    private SchemeOfStudySystem view; // Reference to the View

    public SchemeOfStudySystemController(SchemeOfStudySystemModel model, SchemeOfStudySystem view) {
        this.model = model;
        this.view = view;
    }

    /**
     * Handles the event when a semester is selected and the "Next" button is clicked.
     */
    public void handleSemesterSelection(String semesterType) {
        List<File> files = model.getFilesForSemester(semesterType);
        view.displayFilesList(files, semesterType); // Tell the view to update its file list
        view.switchToPanel("FileList"); // Tell the view to switch to the file list panel
    }

    /**
     * Handles the event when the "Download" button is clicked for a specific file.
     */
    public void handleDownloadFile(File sourceFile) {
        File destination = view.promptForSaveLocation(sourceFile.getName());
        if (destination != null) { // User selected a location
            try {
                model.copyFile(sourceFile, destination);
                view.showInformationMessage("File downloaded successfully to: " + destination.getAbsolutePath());
            } catch (IOException ex) {
                view.showErrorMessage("Error downloading file: " + ex.getMessage());
            }
        }
    }

    /**
     * Handles the event when the "Show" button is clicked for a specific file.
     */
    public void handleShowFile(File file) {
        try {
            String content = model.loadFileContent(file);
            view.displayFileContent(content); // Tell the view to display the file content
            view.switchToPanel("ShowFile"); // Tell the view to switch to the show file panel
        } catch (IOException e) {
            view.showErrorMessage("Error loading file content: " + e.getMessage());
        }
    }

    /**
     * Handles the event when the "Back" button is clicked from the file list panel.
     */
    public void handleBackToFileList() {
        view.switchToPanel("SelectSemester");
    }

    /**
     * Handles the event when the "Back" button is clicked from the show file panel.
     */
    public void handleBackToShowFile() {
        view.switchToPanel("FileList");
    }
}