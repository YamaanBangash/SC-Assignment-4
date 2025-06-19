import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * The Controller in the MVC pattern for the file upload feature.
 * It mediates between the View (user interface) and the Model (data and business logic).
 * It listens for user actions, updates the Model, and instructs the View to update.
 */
public class UploadofSchemeController {

    private UploadofSchemeModel model; // Reference to the Model
    private UploadofSchemeView view;   // Reference to the View

    /**
     * Constructor for the Controller.
     * Initializes the references to the Model and View.
     * Calls `initListeners()` to set up event handling.
     * @param model The UploadofSchemeModel instance.
     * @param view The UploadofSchemeView instance.
     */
    public UploadofSchemeController(UploadofSchemeModel model, UploadofSchemeView view) {
        this.model = model;
        this.view = view;
        initListeners(); // Set up event handlers
    }

    /**
     * Initializes all the ActionListeners for the View's components.
     * This is where the Controller connects to the View's interactive elements.
     */
    private void initListeners() {
        // Listener for the "Next" button on the semester selection panel
        view.getNextButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Get input from the View
                String selectedSemester = (String) view.getSemesterComboBox().getSelectedItem();

                // 2. Perform validation/logic using Model's capabilities (or simple local checks)
                if ("--Select--".equals(selectedSemester)) {
                    // 3. Instruct View to display error
                    view.showErrorMessage("Please select a semester.");
                } else {
                    // 3. Update the Model's state
                    model.setSelectedSemester(selectedSemester);
                    // 4. Instruct the View to change its display (show the upload panel)
                    view.showUploadPanel();
                }
            }
        });

        // Listener for the "Browse File" button on the upload panel
        view.getBrowseButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Create and configure a file chooser (UI interaction, but orchestrated by Controller)
                JFileChooser fileChooser = new JFileChooser();
                // Filter for .txt and .dox files (UI filter)
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "Text and Document Files", "txt", "dox");
                fileChooser.setFileFilter(filter);

                // 2. Show the dialog and get user's choice
                int returnValue = fileChooser.showOpenDialog(view); // 'view' is the parent component for the dialog

                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    // 3. Update the Model's state with the selected file
                    model.setSelectedFile(selectedFile);
                    // 4. Instruct the View to update the file label with the selected file's name
                    view.setFileLabel(selectedFile.getName());
                } else {
                    // 3. If cancelled, update Model to clear selected file
                    model.setSelectedFile(null);
                    // 4. Instruct the View to reset the file label
                    view.setFileLabel("No file selected");
                }
            }
        });

        // Listener for the "Upload Scheme" button on the upload panel
        view.getUploadButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // 1. Validate state by querying the Model
                if (model.getSelectedFile() == null) {
                    view.showErrorMessage("Please select a file to upload.");
                } else if (!model.isValidFileExtension()) { // Controller uses Model's validation logic
                    view.showErrorMessage("Invalid file type. Only .txt or .dox files are allowed.");
                    // If file is invalid, clear it from Model and update View
                    model.setSelectedFile(null);
                    view.setFileLabel("No file selected");
                } else {
                    // 2. Trigger the core business logic (upload) in the Model
                    boolean success = model.uploadScheme(); // Model performs DB operation

                    // 3. Based on the Model's result, instruct the View to display appropriate message and navigate
                    if (success) {
                        view.showSuccessMessage("Scheme uploaded successfully for " + model.getSelectedSemester() + "!");
                        view.showSemesterSelection(); // Go back to semester selection after successful upload
                    } else {
                        view.showErrorMessage("File upload failed. Please try again.");
                    }
                }
            }
        });

        // Listener for the "Back" button on the upload panel
        view.getBackButton().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Purely navigational control: Instruct View to switch panels
                view.showSemesterSelection();
            }
        });
    }

    /**
     * Main method to start the application.
     * It is responsible for creating and wiring together the Model, View, and Controller.
     * Ensures the GUI is run on the Event Dispatch Thread (EDT).
     */
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // 1. Instantiate the Model
                UploadofSchemeModel model = new UploadofSchemeModel();
                // 2. Instantiate the View
                UploadofSchemeView view = new UploadofSchemeView();
                // 3. Instantiate the Controller, passing references to the Model and View.
                // This establishes the communication links.
                UploadofSchemeController controller = new UploadofSchemeController(model, view);
                // 4. Make the View visible to the user.
                view.setVisible(true);
            }
        });
    }
}