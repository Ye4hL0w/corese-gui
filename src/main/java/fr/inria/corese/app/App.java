package fr.inria.corese.app;

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
    private EditorView editorView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Corese");
        primaryStage.setWidth(1920);
        primaryStage.setHeight(1080);

        root = new BorderPane();
        Scene scene = new Scene(root, primaryStage.getWidth(), primaryStage.getHeight());

        scene.getStylesheets().add(getClass().getResource("/style/styles.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style/tabpane.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style/custom-icon.css").toExternalForm());
        scene.getStylesheets().add(getClass().getResource("/style/main-buttons.css").toExternalForm());

        MenuUI menuUI = new MenuUI(this);
        root.setLeft(menuUI.getMenu());

        editorView = new EditorView();

        showDataView();

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void showDataView() {
        root.setCenter(new DataView().getView());
    }

    public void showRdfEditorView() {
        root.setCenter(editorView.getView());
    }

    public void showValidationView() {
        root.setCenter(new ValidationView().getView());
    }

    public void showQueryView() {
        root.setCenter(new QueryView().getView());
    }

    public void showSettingsView() {
        root.setCenter(new SettingsView().getView());
    }
}
