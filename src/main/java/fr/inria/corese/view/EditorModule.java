package fr.inria.corese.view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class EditorModule {

    /**
     * Creates an area showing line numbers for the given text area.
     * This method is called in {@link EditorView}.
     * <p>
     * She uses a {@link TextArea#textProperty()} listener to monitor changes in the {@link TextArea}.
     * </p>
     *
     * @param textArea the text area for which to create line numbers.
     * @return a {@link VBox} containing the line numbers.
     */
    public VBox createLineNumberArea(TextArea textArea) {
        VBox lineNumberArea = new VBox();
        lineNumberArea.setAlignment(Pos.TOP_RIGHT);
        lineNumberArea.setPadding(new Insets(15, 5, 0, 5));

        textArea.scrollTopProperty().addListener((observable, oldValue, newValue) -> {
            lineNumberArea.setTranslateY(-newValue.doubleValue());
        });

        textArea.textProperty().addListener((observable, oldValue, newValue) -> {
            int lineCount = textArea.getText().split("\n", -1).length;
            lineNumberArea.getChildren().clear();
            for (int i = 1; i <= lineCount; i++) {
                Label lineLabel = new Label(String.valueOf(i));
                lineLabel.setFont(Font.font("Arial", 14));
                lineLabel.setPadding(new Insets(0, 0, 1.1995, 0));
                lineNumberArea.getChildren().add(lineLabel);
            }
        });

        return lineNumberArea;
    }

    /**
     * Creates a {@link VBox} containing icons for file operations.
     * Each {@link FontIcon} is assigned a {@link Tooltip}.
     *
     *
     * @param primaryStage the primary stage of the application.
     * @param textArea the text area associated with the icons.
     * @return a {@link VBox} containing the file operation icons.
     *
     * @see VBox
     * @see FontIcon
     * @see Tooltip
     * @see TextArea
     */
    public VBox createIconsBox(Stage primaryStage, TextArea textArea, TabPane tabPane) {

        /* All the icon button */

        VBox iconsBox = new VBox(10);
        iconsBox.setPadding(new Insets(10));
        iconsBox.setAlignment(Pos.TOP_RIGHT);

        FontIcon saveIcon = new FontIcon(MaterialDesignC.CONTENT_SAVE_ALL_OUTLINE);
        saveIcon.setIconSize(34);
        saveIcon.setOnMouseClicked(event -> saveTextToFile(primaryStage, textArea, tabPane.getSelectionModel().getSelectedItem()));
        saveIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipSave = new Tooltip("Save files");
        tooltipSave.setFont(Font.font(14));
        Tooltip.install(saveIcon, tooltipSave);

        FontIcon openIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openIcon.setIconSize(34);
        openIcon.setOnMouseClicked(event -> loadTextFromFile(primaryStage, textArea, tabPane.getSelectionModel().getSelectedItem()));
        openIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipOpen = new Tooltip("Open files");
        tooltipOpen.setFont(Font.font(14));
        Tooltip.install(openIcon, tooltipOpen);

        FontIcon importIcon = new FontIcon(MaterialDesignA.APPLICATION_IMPORT);
        importIcon.setIconSize(34);
        importIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipImport = new Tooltip("Import");
        tooltipImport.setFont(Font.font(14));
        Tooltip.install(importIcon, tooltipImport);

        FontIcon undoIcon = new FontIcon(MaterialDesignU.UNDO_VARIANT);
        undoIcon.setIconSize(34);
        undoIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipUndo = new Tooltip("Undo");
        tooltipUndo.setFont(Font.font(14));
        Tooltip.install(undoIcon, tooltipUndo);

        FontIcon redoIcon = new FontIcon(MaterialDesignR.REDO_VARIANT);
        redoIcon.setIconSize(34);
        redoIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipRedo = new Tooltip("Redo");
        tooltipRedo.setFont(Font.font(14));
        Tooltip.install(redoIcon, tooltipRedo);

        FontIcon bookIcon = new FontIcon(MaterialDesignB.BOOK_OPEN_VARIANT);
        bookIcon.setIconSize(34);
        bookIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipBookOpen = new Tooltip("Open Book");
        tooltipBookOpen.setFont(Font.font(14));
        Tooltip.install(bookIcon, tooltipBookOpen);

        FontIcon infoIcon = new FontIcon(MaterialDesignI.INFORMATION_OUTLINE);
        infoIcon.setIconSize(34);
        infoIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipInfo = new Tooltip("Information");
        tooltipInfo.setFont(Font.font(14));
        Tooltip.install(infoIcon, tooltipInfo);

        iconsBox.getChildren().addAll(saveIcon, openIcon, importIcon, /*exportIcon, cleanIcon,*/ undoIcon, redoIcon, bookIcon, infoIcon);
        return iconsBox;
    }

    /* Method for actions */

    /**
     * Opens a {@link FileChooser} dialog for the user to select a location to save the text content from the provided {@link TextArea}.
     * <p>
     * If the user selects a file location, the content of the {@link TextArea} is saved to that {@link File}, and the tab's text is set to the file name.
     * </p>
     *
     * @param stage the primary stage of the application.
     * @param textArea the {@link TextArea} containing the text content to be saved.
     * @param tab the {@link Tab} associated with the text content being saved.
     *
     * @see FileChooser
     * @see TextArea
     * @see File
     * @see Tab
     */
    public void saveTextToFile(Stage stage, TextArea textArea, Tab tab) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                Files.write(file.toPath(), textArea.getText().getBytes());
                tab.setText(file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens a {@link FileChooser} dialog for the user to select a {@link File} to open.
     * <p>
     * If the user selects a {@link File}, its content is read and loaded into the {@link TextArea}, and the tab's text is set to the file name.
     * </p>
     *
     * @param stage the primary stage of the application.
     * @param textArea the {@link TextArea} where the content of the selected file will be loaded.
     * @param tab the {@link Tab} associated with the text content being loaded.
     *
     * @see FileChooser
     * @see File
     * @see TextArea
     * @see Tab
     */
    public void loadTextFromFile(Stage stage, TextArea textArea, Tab tab) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                textArea.setText(content);
                tab.setText(file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


}
