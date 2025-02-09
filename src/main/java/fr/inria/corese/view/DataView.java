package fr.inria.corese.view;

import fr.inria.corese.controller.DataController;
import fr.inria.corese.view.data.FilesContentView;
import fr.inria.corese.view.data.LogsContentView;
import fr.inria.corese.view.data.RdfContentView;
import fr.inria.corese.view.data.StatsContentView;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignC;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * {@link DataView} is a JavaFX component with the role of view in the code architecture.
 * <p>
 * It displays the content of the DataView tab of {@link fr.inria.corese.app.MenuUI}.
 * </p>
 *
 * @version 1.0
 * @since April 2024
 * @see DataView
 * @see fr.inria.corese.app.MenuUI
 */
public class DataView {

    public VBox vbCenter;

    private FilesContentView filesContentView;
    private RdfContentView rdfContentView;
    private LogsContentView logsContentView;
    private StatsContentView statsContentView;

    private DataController dataController;

    /**
     * Constructor for DataView.
     * <p>
     * Allows you to retrieve the instance of {@link DataController}.
     * </p>
     *
     * @param dataController the controller for managing data operations.
     */
    public DataView(DataController dataController) {
        this.dataController = dataController;
    }

    /**
     * Returns the {@link VBox } containing all {@link DataView} views.
     *
     * <p>
     * The method instantiates the contents of {@link DataView} in {@link fr.inria.corese.app.App}.
     * </p>
     *
     * @return {@link VBox} representing the main view of the DataView.
     *
     * @see VBox
     * @see DataView
     * @see fr.inria.corese.app.App
     */
    public VBox getView() {
        VBox data = createData();
        return data;
    }

    /* First display */

    /**
     * Creates the initial layout and content of the DataView.
     * <p>
     * This method initializes the content views (files, RDF, logs, stats) and arranges them within a grid layout using {@link FilesContentView}, {@link RdfContentView}, {@link LogsContentView}, {@link StatsContentView}.
     * </p>
     *
     * @return a {@link VBox} containing the DataView layout and content.
     *
     * @see FilesContentView
     * @see RdfContentView
     * @see LogsContentView
     * @see StatsContentView
     */
    public VBox createData()  {
        vbCenter = new VBox();
        vbCenter.setPadding(new Insets(20));

        /* Initialize the content of DataView */

        filesContentView = new FilesContentView(dataController);
        rdfContentView = new RdfContentView();
        logsContentView = new LogsContentView();
        statsContentView = new StatsContentView();

        VBox vbox = new VBox();
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.setSpacing(20);
        vbox.setPadding(new Insets(20));
        vbox.setBackground(new Background(new BackgroundFill(Color.rgb(162, 196, 201), CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        HBox hbButton = new HBox();
        hbButton.setSpacing(10);

        Button openProjectButton = new Button("Open Project");
        FontIcon openIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openIcon.setIconSize(34);
        openProjectButton.setGraphic(openIcon);
        openProjectButton.getStyleClass().add("main-button");
        openProjectButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Open Project");

            File selectedDirectory = directoryChooser.showDialog(null);

            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                File[] files = selectedDirectory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        if (file.isFile()) {
                            filesContentView.addFileItem(file);
                            logsContentView.logFileLoading(file);
                        }
                    }
                }

                showFilesLoadedPopup();
            }
        });

        Button saveAsButton = new Button("Save as");
        FontIcon saveIcon = new FontIcon(MaterialDesignC.CONTENT_SAVE_ALL_OUTLINE);
        saveIcon.setIconSize(34);
        saveAsButton.setGraphic(saveIcon);
        saveAsButton.getStyleClass().add("main-button");
        saveAsButton.setOnAction(event -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory to Save Files");

            File selectedDirectory = directoryChooser.showDialog(null);
            if (selectedDirectory != null && selectedDirectory.isDirectory()) {
                try {
                    for (File file : filesContentView.getFiles()) {
                        Path destination = selectedDirectory.toPath().resolve(file.getName());
                        Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        hbButton.getChildren().addAll(openProjectButton, saveAsButton);

        GridPane gridPane = new GridPane();
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        gridPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        gridPane.setPadding(new Insets(0, 0, 0, 0));
        gridPane.setHgap(20);
        gridPane.setVgap(20);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(66.67);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(33.33);
        gridPane.getColumnConstraints().addAll(col1, col2);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(66.67);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(33.33);
        gridPane.getRowConstraints().addAll(row1, row2);


        VBox vbFiles = filesContentView.createFilesContent(this, logsContentView);
        GridPane.setConstraints(vbFiles, 0, 0);

        VBox vbRdf = rdfContentView.createRdfContent(this);
        GridPane.setConstraints(vbRdf, 1, 0);

        VBox vbLogs = logsContentView.createLogsContent();
        GridPane.setConstraints(vbLogs, 0, 1);

        VBox vbStats = statsContentView.createStatsContent();
        GridPane.setConstraints(vbStats, 1, 1);


        gridPane.getChildren().addAll(vbFiles, vbLogs, vbRdf, vbStats);


        vbox.getChildren().addAll(hbButton, gridPane);

        vbCenter.getChildren().addAll(vbox);
        return vbCenter;
    }

    /* Files loaded Pop-Up */

    /**
     * Displays a pop-up {@link Alert} to indicate that files have been loaded.
     * <p>
     * The alert will automatically close after 2 seconds.
     * </p>
     *
     * @see Alert
     */
    public void showFilesLoadedPopup() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Alert");
        alert.setHeaderText(null);
        alert.setContentText("Files are loaded");
//        alert.setWidth(600);
//        alert.setHeight(100);

        alert.getDialogPane().setStyle("-fx-background-color: #B6D7A8;");

        Window ownerWindow = vbCenter.getScene().getWindow();

        double alertX = ownerWindow.getX() + ownerWindow.getWidth() - alert.getDialogPane().getWidth() - 90;
        double alertY = ownerWindow.getY() + ownerWindow.getHeight() - alert.getDialogPane().getHeight() - 160;
        alert.setX(alertX);
        alert.setY(alertY);

        alert.show();

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(2), e -> alert.close()));
        timeline.play();
    }
}
