import javax.swing.SwingUtilities;

public class MainApplication {
    public static void main(String[] args) {
        // Ensure GUI updates are done on the Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            UploadofSchemeView view = new UploadofSchemeView();
            UploadofSchemeModel model = new UploadofSchemeModel();
            new UploadofSchemeController(model, view); // Controller links Model and View
            view.setVisible(true); // Make the main window visible
        });
    }
}