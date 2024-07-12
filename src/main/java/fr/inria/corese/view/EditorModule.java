package fr.inria.corese.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * The {@link EditorModule} class provides functionalities for creating a user interface with icons for file operations
 * (save, open, import, undo, redo, open book, information) and methods to handle these operations.
 * <p>
 * This module is reused in various views such as {@link EditorView}, {@link ValidationView}, and {@link QueryView}
 * to maintain consistency and reusability across the application.
 *
 * @see EditorView
 * @see ValidationView
 * @see QueryView
 *
 * </p>
 */
public class EditorModule {

    /**
     * Creates a {@link VBox} containing icons for file operations.
     * Each {@link FontIcon} is assigned a {@link Tooltip}.
     *
     *
     * @param primaryStage the primary stage of the application.
     * @param codeArea the text area associated with the icons.
     * @param tabPane the pane in which the icons are contained.
     * @return a {@link VBox} containing the file operation icons.
     *
     * @see VBox
     * @see FontIcon
     * @see Tooltip
     * @see TextArea
     */
    public VBox createIconsBox(Stage primaryStage, CodeArea codeArea, TabPane tabPane) {

        /* All the icon button */

        VBox iconsBox = new VBox(10);
        iconsBox.setPadding(new Insets(10));
        iconsBox.setAlignment(Pos.TOP_RIGHT);

        FontIcon saveIcon = new FontIcon(MaterialDesignC.CONTENT_SAVE_ALL_OUTLINE);
        saveIcon.setIconSize(34);
        saveIcon.setOnMouseClicked(event -> saveTextToFile(primaryStage, codeArea, tabPane.getSelectionModel().getSelectedItem()));
        saveIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipSave = new Tooltip("Save files");
        tooltipSave.setFont(Font.font(14));
        Tooltip.install(saveIcon, tooltipSave);

        FontIcon openIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openIcon.setIconSize(34);
        openIcon.setOnMouseClicked(event -> loadTextFromFile(primaryStage, codeArea, tabPane.getSelectionModel().getSelectedItem()));
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
     * Opens a {@link FileChooser} dialog for the user to select a location to save the text content from the provided {@link CodeArea}.
     * <p>
     * If the user selects a file location, the content of the {@link CodeArea} is saved to that {@link File}, and the tab's text is set to the file name.
     * </p>
     *
     * @param stage the primary stage of the application.
     * @param codeArea the {@link CodeArea} containing the text content to be saved.
     * @param tab the {@link Tab} associated with the text content being saved.
     *
     * @see FileChooser
     * @see CodeArea
     * @see File
     * @see Tab
     */
    public void saveTextToFile(Stage stage, CodeArea codeArea, Tab tab) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text");
        File file = fileChooser.showSaveDialog(stage);
        if (file != null) {
            try {
                Files.write(file.toPath(), codeArea.getText().getBytes());
                tab.setText(file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Opens a {@link FileChooser} dialog for the user to select a {@link File} to open.
     * <p>
     * If the user selects a {@link File}, its content is read and loaded into the {@link CodeArea}, and the tab's text is set to the file name.
     * </p>
     *
     * @param stage the primary stage of the application.
     * @param codeArea the {@link CodeArea} where the content of the selected file will be loaded.
     * @param tab the {@link Tab} associated with the text content being loaded.
     *
     * @see FileChooser
     * @see File
     * @see CodeArea
     * @see Tab
     */
    public void loadTextFromFile(Stage stage, CodeArea codeArea, Tab tab) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        File file = fileChooser.showOpenDialog(stage);
        if (file != null) {
            try {
                String content = new String(Files.readAllBytes(file.toPath()));
                codeArea.replaceText(content);
                tab.setText(file.getName());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
