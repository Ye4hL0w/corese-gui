package fr.inria.corese.app;

import fr.inria.corese.controller.DataController;
import fr.inria.corese.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * The {@link App} class is the main entry point for the JavaFX application.
 * <p>
 * It initializes and manages the primary user interface of the application, setting up the main window and providing navigation
 * between different views such as Data, RDF Editor, Validation, Query, and Settings.
 * </p>
 */
public class App extends Application {

    /**
     * Launches the JavaFX application.
     * <p>
     * This method is a static entry point that invokes the {@link Application#launch(String...)} method to start the JavaFX application lifecycle.
     * </p>
     *
     * @param args command-line arguments passed to the application.
     */
    public static void lancement(String[] args) {
        App.launch(args);
    }

    private BorderPane root;
    private DataView dataView;
    private EditorView editorView;
    private ValidationView validationView;
    private QueryView queryView;
    private SettingsView settingsView;

    /**
     * Initializes and displays the main stage of the JavaFX application.
     * <p>
     * This method sets up the primary window, including its size, minimum size, and title. It also initializes the main layout
     * with a {@link BorderPane} and configures the scene with various stylesheets. The method sets up the menu and initializes the
     * different views of the application. Finally, it displays the initial view on the stage.
     * </p>
     *
     * @param primaryStage the primary stage for this application, onto which the scene will be set.
     * @throws Exception if an error occurs during initialization.
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Corese");
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);
        primaryStage.setMinWidth(1420);
        primaryStage.setMinHeight(740);

        root = new BorderPane();
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());

        scene.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style/tabpane.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style/custom-icon.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style/main-buttons.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/smartgraph/smartgraph.css").toExternalForm());

        MenuUI menuUI = new MenuUI(this);
        root.setLeft(menuUI.getMenu());

        DataController dataController = new DataController();

        dataView = new DataView(dataController);
        editorView = new EditorView();
        validationView = new ValidationView();
        queryView = new QueryView(dataController);
        settingsView = new SettingsView();

        showDataView();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Displays the Data view in the center of the main layout.
     * <p>
     * This method sets the {@link DataView} as the central component of the {@link BorderPane}, replacing any previously displayed view.
     * </p>
     */
    public void showDataView() {
        root.setCenter(dataView.getView());
    }

    /**
     * Displays the RDF Editor view in the center of the main layout.
     * <p>
     * This method sets the {@link EditorView} as the central component of the {@link BorderPane}, replacing any previously displayed view.
     * </p>
     */
    public void showRdfEditorView() {
        root.setCenter(editorView.getView());
    }

    /**
     * Displays the Validation view in the center of the main layout.
     * <p>
     * This method sets the {@link ValidationView} as the central component of the {@link BorderPane}, replacing any previously displayed view.
     * </p>
     */
    public void showValidationView() {
        root.setCenter(validationView.getView());
    }

    /**
     * Displays the Query view in the center of the main layout.
     * <p>
     * This method sets the {@link QueryView} as the central component of the {@link BorderPane}, replacing any previously displayed view.
     * </p>
     */
    public void showQueryView() {
        root.setCenter(queryView.getView());
    }

    /**
     * Displays the Settings view in the center of the main layout.
     * <p>
     * This method sets the {@link SettingsView} as the central component of the {@link BorderPane}, replacing any previously displayed view.
     * </p>
     */
    public void showSettingsView() {
        root.setCenter(settingsView.getView());
    }
}
