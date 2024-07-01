package fr.inria.corese.view;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DataView {

    public VBox vbCenter;
    public VBox logContent;

    public VBox getView() {
        VBox data = createData();
        return data;
    }

    /* First display */

    public VBox createData() {
        vbCenter = new VBox();
        vbCenter.setPadding(new Insets(20));

        VBox vbox = new VBox();
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.setSpacing(20);
        vbox.setPadding(new Insets(20));
        vbox.setBackground(new Background(new BackgroundFill(Color.rgb(162, 196, 201), CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        HBox hbButton = new HBox();
        hbButton.setSpacing(10);
//        hbButton.setBackground(new Background(new BackgroundFill(Color.PALEGOLDENROD, CornerRadii.EMPTY, null)));

        Button openProjectButton = new Button("Open Project");
        FontIcon openIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openIcon.setIconSize(34);
        openProjectButton.setGraphic(openIcon);
        openProjectButton.getStyleClass().add("main-button");
        openProjectButton.setOnAction(event -> {

        });

        Button saveAsButton = new Button("Save as");
        FontIcon saveIcon = new FontIcon(MaterialDesignC.CONTENT_SAVE_ALL_OUTLINE);
        saveIcon.setIconSize(34);
        saveAsButton.setGraphic(saveIcon);
        saveAsButton.getStyleClass().add("main-button");
        saveAsButton.setOnAction(event -> {

        });

        hbButton.getChildren().addAll(openProjectButton, saveAsButton);

        GridPane gridPane = new GridPane();
        VBox.setVgrow(gridPane, Priority.ALWAYS);
        gridPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        gridPane.setPadding(new Insets(0, 0, 0, 0));
//        gridPane.setBackground(new Background(new BackgroundFill(Color.PALEGOLDENROD, CornerRadii.EMPTY, null)));
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

        VBox vbFiles = createFilesContent();
        GridPane.setConstraints(vbFiles, 0, 0);

        VBox vbRdf = createRdfContent();
        GridPane.setConstraints(vbRdf, 1, 0);

        VBox vbLogs = createLogsContent();
        GridPane.setConstraints(vbLogs, 0, 1);

        VBox vbStats = createStatsContent();
        GridPane.setConstraints(vbStats, 1, 1);

        gridPane.getChildren().addAll(vbFiles, vbLogs, vbRdf, vbStats);


        vbox.getChildren().addAll(hbButton, gridPane);

        vbCenter.getChildren().addAll(vbox);
        return vbCenter;
    }

    /* The methods for the content of Data */

    public VBox createFilesContent() {

        VBox vbox = new VBox();
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.setPadding(new Insets(20));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        VBox vbFileItem = new VBox();
        VBox.setVgrow(vbFileItem, Priority.ALWAYS);

        HBox hbButton = new HBox();
        hbButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbButton.setSpacing(10);

        Button clearGraphButton = new Button("Clear Graph");
        FontIcon clearIcon = new FontIcon(MaterialDesignB.BROOM);
        clearIcon.setIconSize(34);
        clearGraphButton.setGraphic(clearIcon);
        clearGraphButton.getStyleClass().add("main-button");
        clearGraphButton.setOnAction(event -> {
            showWarningPopup();
        });

        Button reloadButton = new Button("Reload files");
        FontIcon reloadIcon = new FontIcon(MaterialDesignR.RELOAD);
        reloadIcon.setIconSize(34);
        reloadButton.setGraphic(reloadIcon);
        reloadButton.getStyleClass().add("main-button");
        reloadButton.setOnAction(event -> {
            showWarningPopup();
        });

        /* Load files */

        Button findButton = new Button("Load files");
        FontIcon findIcon = new FontIcon(MaterialDesignF.FOLDER_SEARCH_OUTLINE);
        findIcon.setIconSize(34);
        findButton.setGraphic(findIcon);
        findButton.getStyleClass().add("main-button");
        findButton.setOnAction(event -> {

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open file");

            File selectedFile = fileChooser.showOpenDialog(null);

            if (selectedFile != null) {
                HBox hbox = new HBox();
//                hbox.setBackground(new Background(new BackgroundFill(Color.PALEGOLDENROD, CornerRadii.EMPTY, null)));

                Label fileNameLabel = new Label(selectedFile.getName());
                fileNameLabel.setFont(Font.font("Arial", 32));
                fileNameLabel.getStyleClass().add("hand-on-button");
                fileNameLabel.setOnMouseClicked( e -> {
                    showFileInfoPopup(selectedFile);
                });

                Pane spacer = new Pane();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                FontIcon deleteIcon = new FontIcon(MaterialDesignD.DELETE_OUTLINE);
                deleteIcon.setIconSize(34);
                deleteIcon.setOnMouseClicked(e -> {
                    showWarningPopup();
                });
                deleteIcon.getStyleClass().add("custom-icon");
                Tooltip tooltipDelete = new Tooltip("Delete file");
                tooltipDelete.setFont(Font.font(14));
                Tooltip.install(deleteIcon, tooltipDelete);

                hbox.getChildren().addAll(fileNameLabel, spacer, deleteIcon);

                vbFileItem.getChildren().add(hbox);

                clearGraphButton.setDisable(false);
                reloadButton.setDisable(false);
            }

            showFilesLoadedPopup();
            logFileLoading(selectedFile);
        });

        /* For disable and enable the buttons */

        if (vbFileItem.getChildren().isEmpty()) {
            clearGraphButton.setDisable(true);
            reloadButton.setDisable(true);
        } else {
            clearGraphButton.setDisable(false);
            reloadButton.setDisable(false);
        }


        hbButton.getChildren().addAll(clearGraphButton, reloadButton, findButton);

        vbox.getChildren().addAll(vbFileItem, hbButton);

        return vbox;
    }

    public VBox createLogsContent() {
        VBox vbLogs = new VBox();
        VBox.setVgrow(vbLogs, Priority.ALWAYS);
        vbLogs.setPadding(new Insets(10));
        vbLogs.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbLogs.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
//        scrollPane.getStyleClass().add("custom-scrollPane");

        logContent = new VBox();
        scrollPane.setContent(logContent);

        vbLogs.getChildren().add(scrollPane);

        addLogMessage("Initialization:", Color.BLACK);

        scrollPane.layout();
        scrollPane.setVvalue(1.0);

        return vbLogs;
    }

    public VBox createRdfContent() {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);
        scrollPane.getStyleClass().add("custom-scrollPane");


        CheckBox rdfsSubset = createHyperlinkCheckBox("RDFS Subset", "https://www.w3.org/TR/rdf-mt/");
        CheckBox rdfsRl = createHyperlinkCheckBox("RDFS RL", "https://owl-rl.readthedocs.io/en/latest/");
        CheckBox owlRl = createHyperlinkCheckBox("OWL RL", "https://owl-rl.readthedocs.io/en/latest/");
        CheckBox owlRlExtended = createHyperlinkCheckBox("OWL RL Extended", "https://owl-rl.readthedocs.io/en/latest/stubs/owlrl.html");
        CheckBox owlClean = createHyperlinkCheckBox("OWL Clean", "https://ebooks.iospress.nl/publication/3236");


        VBox rdfsContent = new VBox(rdfsSubset, rdfsRl);
        TitledPane rdfsPane = new TitledPane("RDFS", rdfsContent);
        rdfsPane.getStyleClass().add("titled-pane");

        VBox owlContent = new VBox(owlRl, owlRlExtended, owlClean);
        TitledPane owlPane = new TitledPane("OWL", owlContent);
        owlPane.getStyleClass().add("titled-pane");

        VBox titledPanesContainer = new VBox(rdfsPane, owlPane);

        scrollPane.setContent(titledPanesContainer);

        vbox.getChildren().add(scrollPane);

        return vbox;
    }

    public VBox createStatsContent() {
        VBox vbox = new VBox();
        vbox.setSpacing(10);
        vbox.setPadding(new Insets(10));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        int sementicElements = 0;
        int triplet = 0;
        int graph = 0;
        int rules = 0;

        Label label1 = new Label("Semantic elements loaded: " + sementicElements);
        label1.setFont(new Font("Arial", 26));

        Label label2 = new Label("Number of triplet: " + triplet);
        label2.setFont(new Font("Arial", 26));

        Label label3 = new Label("Number of graph: " + graph);
        label3.setFont(new Font("Arial", 26));

        Label label4 = new Label("Number of rules loaded: " + rules);
        label4.setFont(new Font("Arial", 26));

        vbox.getChildren().addAll(label1, label2, label3, label4);

        return vbox;
    }

    /* Alert Pop-Up */

    private void showWarningPopup() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alert");
        alert.setHeaderText("");
        alert.setContentText("WARNING\nBe careful the entire graph will be reset !");

        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeContinue = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeContinue);

        Button cancelButton = (Button) alert.getDialogPane().lookupButton(buttonTypeCancel);
        Button continueButton = (Button) alert.getDialogPane().lookupButton(buttonTypeContinue);

        cancelButton.setDefaultButton(true);
        continueButton.setDefaultButton(false);

        alert.showAndWait().ifPresent(type -> {
            if (type == buttonTypeContinue) {
                System.out.println("Continue pressed");
            } else {
                System.out.println("Cancel pressed");
            }
        });
    }

    /* Files loaded Pop-Up */

    private void showFilesLoadedPopup() {
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

    /* About file */

    private void showFileInfoPopup(File file) {
        try {
            long startTime = System.nanoTime();

            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            long fileSizeInBytes = attrs.size();
            double fileSizeInMB = fileSizeInBytes / (1024.0 * 1024.0);
            String lastModified = new SimpleDateFormat("EEEE dd MMMM yyyy").format(new Date(attrs.lastModifiedTime().toMillis()));

            long endTime = System.nanoTime();
            double loadingTime = (endTime - startTime) / 1_000_000_000.0;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("About File :");
            alert.setHeaderText(null);
            alert.setContentText(String.format(
                    "Name: %s\nType: %s\nMore information: %s\nModified: %s\nSize: %.1f Bytes\nFile size: %.1f MB\nLoading time: %.1f seconds",
                    file.getAbsolutePath(),
                    getFileExtension(file),
                    "null",
                    lastModified,
                    (float) fileSizeInBytes,
                    fileSizeInMB,
                    loadingTime
            ));
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getFileExtension(File file) {
        String fileName = file.getName();
        int dotIndex = fileName.lastIndexOf('.');
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /* Show the logs */

    private void addLogMessage(String message, Color color) {
        TextFlow logMessageFlow = new TextFlow();
        logMessageFlow.setMaxWidth(Double.MAX_VALUE);

        Text timestamp = new Text(getCurrentTimestamp());
        timestamp.setFont(Font.font("Arial", 16));
        timestamp.setFill(color);

        Text logText = new Text(message);
        logText.setFont(Font.font("Arial", 16));
        logText.setFill(color);

        logMessageFlow.getChildren().addAll(timestamp, logText);

        logContent.getChildren().add(logMessageFlow);
    }

    private String getCurrentTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return "[" + sdf.format(new Date()) + "] ";
    }

    public void logFileLoading(File file) {
//        String loadingStr = "Loading " + getFileExtension(file) + " File from path : ";
//        Text loadingText = new Text(loadingStr);
//        loadingText.setFill(Color.rgb(128,128,128));
        addLogMessage("Loading "+ getFileExtension(file) + " File from path: " + file.getAbsolutePath()  + "\n", Color.rgb(184,134,11));
        addLogMessage("Loading is done\n", Color.rgb(128,128,128));
//        addLogMessage(" ", Color.WHITE);
    }

    /* Create HyperText for rules */

    private CheckBox createHyperlinkCheckBox(String text, String url) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.getStyleClass().add("check-box");

        checkBox.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.SECONDARY) {
                try {
                    Desktop.getDesktop().browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        });

        return checkBox;
    }
}
