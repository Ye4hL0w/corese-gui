package fr.inria.corese.app;

import fr.inria.corese.controller.DataController;
import fr.inria.corese.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;


public class App extends Application {

    public static void lancement(String[] args) {
        App.launch(args);
    }

    private BorderPane root;
    private DataView dataView;
    private EditorView editorView;
    private ValidationView validationView;
    private QueryView queryView;
    private SettingsView settingsView;

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

    public void showDataView() {
        root.setCenter(dataView.getView());
    }

    public void showRdfEditorView() {
        root.setCenter(editorView.getView());
    }

    public void showValidationView() {
        root.setCenter(validationView.getView());
    }

    public void showQueryView() {
        root.setCenter(queryView.getView());
    }

    public void showSettingsView() {
        root.setCenter(settingsView.getView());
    }
}
