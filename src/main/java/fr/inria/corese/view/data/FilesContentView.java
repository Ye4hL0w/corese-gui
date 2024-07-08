package fr.inria.corese.view.data;

import fr.inria.corese.controller.DataController;
import fr.inria.corese.view.DataView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.MaterialDesignB;
import org.kordamp.ikonli.materialdesign2.MaterialDesignD;
import org.kordamp.ikonli.materialdesign2.MaterialDesignF;
import org.kordamp.ikonli.materialdesign2.MaterialDesignR;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FilesContentView {
    public VBox vbFileItem;
    public Button clearGraphButton;
    public Button reloadButton;
    private List<File> currentFiles = new ArrayList<>();
    private DataController dataController = new DataController();


    public VBox createFilesContent(DataView dataView, LogsContentView logsContentView) {
        VBox vbox = new VBox();
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.setPadding(new Insets(20));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        vbFileItem = new VBox();
        VBox.setVgrow(vbFileItem, Priority.ALWAYS);

        HBox hbButton = new HBox();
        hbButton.setAlignment(Pos.BOTTOM_RIGHT);
        hbButton.setSpacing(10);

        clearGraphButton = new Button("Clear Graph");
        FontIcon clearIcon = new FontIcon(MaterialDesignB.BROOM);
        clearIcon.setIconSize(34);
        clearGraphButton.setGraphic(clearIcon);
        clearGraphButton.getStyleClass().add("main-button");
        clearGraphButton.setOnAction(event -> {
            showWarningPopup("Clear Graph", () -> {
                vbFileItem.getChildren().clear();
                clearGraphButton.setDisable(true);
                reloadButton.setDisable(true);
                dataController.clearGraph(); // controller action
            });
        });

        reloadButton = new Button("Reload files");
        FontIcon reloadIcon = new FontIcon(MaterialDesignR.RELOAD);
        reloadIcon.setIconSize(34);
        reloadButton.setGraphic(reloadIcon);
        reloadButton.getStyleClass().add("main-button");
        reloadButton.setOnAction(event -> {
            showWarningPopup("Reload Files", () -> {
                reloadFiles();
            });
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
                addFileItem(selectedFile);
            }

            dataView.showFilesLoadedPopup();
            logsContentView.logFileLoading(selectedFile);
        });

        /* For disable and enable the buttons */

        updateButtonState();

        hbButton.getChildren().addAll(clearGraphButton, reloadButton, findButton);

        vbox.getChildren().addAll(vbFileItem, hbButton);

        return vbox;
    }

    /* Method to display the new files upload */

    public void addFileItem(File file) {
        String[] allowedExtensions = { ".rdf", ".ttl", ".trig", ".jsonld", ".nt", ".nq", ".html" };

        boolean isAllowed = false;
        for (String ext : allowedExtensions) {
            if (file.getName().toLowerCase().endsWith(ext)) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            return;
        }

        try {
            BasicFileAttributes attrs = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            String createdTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(attrs.creationTime().toMillis()));
            String modifiedTime = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date(attrs.lastModifiedTime().toMillis()));

            HBox hbox = new HBox();
            hbox.setAlignment(Pos.CENTER_LEFT);
            hbox.setSpacing(10);
            hbox.setPadding(new Insets(10));
            hbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0, 0, 1, 0))));


            Label lblFileName = new Label(file.getName());
            lblFileName.setFont(new Font("Arial", 14));
            lblFileName.setPrefWidth(200);
            lblFileName.getStyleClass().add("hand-on-button");
            lblFileName.setOnMouseClicked(e -> {
                showFileInfoPopup(file);
            });

            Label lblCreatedTime = new Label("Created: " + createdTime);
            lblCreatedTime.setFont(new Font("Arial", 12));
            lblCreatedTime.setPrefWidth(200);

            Label lblModifiedTime = new Label("Modified: " + modifiedTime);
            lblModifiedTime.setFont(new Font("Arial", 12));
            lblModifiedTime.setPrefWidth(200);

            Pane spacer = new Pane();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            FontIcon deleteIcon = new FontIcon(MaterialDesignD.DELETE_OUTLINE);
            deleteIcon.setIconSize(34);
            deleteIcon.setOnMouseClicked(e -> {
                showWarningPopup("Delete File", () -> {
                    vbFileItem.getChildren().remove(hbox);
                    updateButtonState();
                });
            });

            deleteIcon.getStyleClass().add("custom-icon");
            Tooltip tooltipDelete = new Tooltip("Delete file");
            tooltipDelete.setFont(Font.font(14));
            Tooltip.install(deleteIcon, tooltipDelete);

            hbox.getChildren().addAll(lblFileName, lblCreatedTime, lblModifiedTime, spacer, deleteIcon);

            vbFileItem.getChildren().add(hbox);

            dataController.loadGraphFromFile(file); // controller action
            String str = String.valueOf(dataController.getGraph());
            System.out.println(str);
            System.out.println(dataController.getGraph().display());

            clearGraphButton.setDisable(false);
            reloadButton.setDisable(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* Alert Pop-Up */

    public void showWarningPopup(String actionType, Runnable onContinue) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Alert");
        alert.setHeaderText("");
        alert.setContentText("WARNING\nBe careful! This action will modify your current state.");

        ButtonType buttonTypeCancel = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType buttonTypeContinue = new ButtonType("Continue", ButtonBar.ButtonData.OK_DONE);

        alert.getButtonTypes().setAll(buttonTypeCancel, buttonTypeContinue);

        Button cancelButton = (Button) alert.getDialogPane().lookupButton(buttonTypeCancel);
        Button continueButton = (Button) alert.getDialogPane().lookupButton(buttonTypeContinue);

        cancelButton.setDefaultButton(true);
        continueButton.setDefaultButton(false);

        alert.showAndWait().ifPresent(type -> {
            if (type == buttonTypeContinue) {
                onContinue.run();  // Execute the provided action
                System.out.println(actionType + " action executed.");
            } else {
                System.out.println("Cancel pressed");
            }
        });
    }


    /* About file */

    public void showFileInfoPopup(File file) {
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

    /* Reload Files */

    public void reloadFiles() {
        vbFileItem.getChildren().clear();
        dataController.clearGraph(); // controller action
        for (File file : currentFiles) {
            addFileItem(file);
            dataController.loadGraphFromFile(file); // controller action
        }
        updateButtonState();
    }

    private void updateButtonState() {
        if (vbFileItem.getChildren().isEmpty()) {
            clearGraphButton.setDisable(true);
            reloadButton.setDisable(true);
        } else {
            clearGraphButton.setDisable(false);
            reloadButton.setDisable(false);
        }
    }
}
