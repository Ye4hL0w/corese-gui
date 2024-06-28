package fr.inria.corese.view;

import fr.inria.corese.app.App;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

/**
 * {@link EditorView} is a JavaFX component with the role of view in the code architecture.
 * <p>
 * It displays the content of the RDF Editor tab of {@link fr.inria.corese.app.MenuUI}.
 * </p>
 *
 * @version 1.0
 * @since April 2024
 * @see EditorView
 * @see fr.inria.corese.app.MenuUI
 */
public class EditorView {

    private StackPane stackPane;
    private HBox mainView;
    private HBox editorContent;

    private Stage primaryStage;
    private Border border;
    private TabPane tabPane;

    private VBox vbFolder;

    private ObservableList<Tab> tabList = FXCollections.observableArrayList();

    private EditorModule editorModule = new EditorModule();

    /**
     * Constructor for {@link EditorView}.
     * <p>
     * Initializes the {@code mainView} with {@link #createRDFeditor()} and the {@code editorContent} with {@link #createEditorContent()}.
     * </p>
     * <p>
     * Shows the main view at startup.
     * </p>
     *
     * @see EditorView
     * @see #createRDFeditor()
     * @see #createEditorContent()
     */
    public EditorView() {
        stackPane = new StackPane();
        mainView = createRDFeditor();
        editorContent = createEditorContent();

        stackPane.getChildren().addAll(mainView, editorContent);
        mainView.setVisible(true);
        editorContent.setVisible(false);
    }

    /**
     * Returns the {@link StackPane} containing all {@link EditorView} views.
     * <p>
     * The method instantiates the content of {@link EditorView} in {@link fr.inria.corese.app.App} with the {@link App#showRdfEditorView()}  method.
     * </p>
     *
     * @return the main {@link StackPane} of the editor.
     *
     * @see StackPane
     * @see EditorView
     * @see fr.inria.corese.app.App
     * @see App#showRdfEditorView()
     */
    public StackPane getView() {
        return stackPane;
    }

    /* First display */

    /**
     * Creates the initial RDF editor view with buttons for creating new files and opening folders.
     * <p>
     * This view is instantiated in {@code mainview} by the constructor {@link EditorView}.
     * </p>
     *
     * @return an {@link HBox} containing the initial view.
     * @see EditorView
     * @see HBox
     */
    public HBox createRDFeditor() {
        HBox hbCenter = new HBox();
        hbCenter.setAlignment(Pos.CENTER);
        hbCenter.setSpacing(20);

        Button newFileButton = new Button("New File");
        FontIcon plusIcon = new FontIcon(MaterialDesignP.PLUS);
        plusIcon.setIconSize(34);
        newFileButton.setGraphic(plusIcon);
        newFileButton.getStyleClass().add("main-button");
        newFileButton.setOnAction(event -> {
            mainView.setVisible(false);
            editorContent.setVisible(true);
        });

        Button openFolderButton = new Button("Open Folder");
        FontIcon openIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openIcon.setIconSize(34);
        openFolderButton.setGraphic(openIcon);
        openFolderButton.getStyleClass().add("main-button");
        openFolderButton.setOnAction(event -> {
            loadFolderContent(primaryStage);
            mainView.setVisible(false);
            editorContent.setVisible(true);
        });

        hbCenter.getChildren().addAll(newFileButton, openFolderButton);

        return hbCenter;
    }

    /**
     * Creates the editor content view with a tab pane and file browsing section.
     * <p>
     * This method constructs an {@link HBox} containing various UI components, including a {@link TabPane} for managing tabs and a section for browsing files.
     * </p>
     *
     * @return an {@link HBox} containing the editor content.
     * @see HBox
     * @see TabPane
     *
     */
    public HBox createEditorContent() {
        border = new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2)));

        HBox hbCenter = new HBox();
        hbCenter.setAlignment(Pos.CENTER);
//        vbCenter.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, null)));

        /* File section */

        tabPane = new TabPane();
        tabPane.setPadding(new Insets(20, 20, 20, 20));
        tabPane.getStyleClass().add("MyTabPane");
        HBox.setHgrow(tabPane, Priority.ALWAYS);


        Tab plusTab = new Tab();
        plusTab.getStyleClass().add("plusTab");
        plusTab.setClosable(false);
        plusTab.setDisable(true);

        FontIcon plusIcon = new FontIcon(MaterialDesignP.PLUS);
        plusIcon.setIconSize(24);
        plusIcon.setOnMouseClicked(event -> {
            Tab newTab = new Tab("Untilted " + (tabPane.getTabs().size()), createTabContent());
            tabList.add(newTab);
            newTab.getStyleClass().add("MyTab");
            tabPane.getTabs().add(tabList.size() - 1, newTab);
            tabPane.getSelectionModel().select(newTab);
        });
        plusTab.setGraphic(plusIcon);

        Tab tab = new Tab("Untilted 1", createTabContent());
        tab.getStyleClass().add("MyTab");
        tabList.add(tab);
        tabPane.getTabs().addAll(tabList);

        tabPane.getSelectionModel().select(0);

        tabPane.getTabs().add(plusTab);

        VBox tabContent = new VBox();
        VBox.setVgrow(tabContent, Priority.ALWAYS);
        BorderPane.setAlignment(tabPane, Pos.CENTER);
        BorderPane.setMargin(tabPane, new Insets(10));

        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                tabContent.getChildren().clear();
                tabContent.getChildren().add(((BorderPane) newTab.getContent()).getCenter());
            }
        });

        /* File browsing section */

        vbFolder = new VBox();
        vbFolder.setPrefWidth(200);
        vbFolder.setAlignment(Pos.TOP_LEFT);
        vbFolder.setPadding(new Insets(18));
        vbFolder.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(0,2,0,2))));
        VBox.setVgrow(vbFolder, Priority.ALWAYS);

        VBox vbToogle = new VBox();
        vbToogle.setAlignment(Pos.CENTER);
        vbToogle.setPadding(new Insets(10));

        FontIcon toggleIcon = new FontIcon(MaterialDesignC.CHEVRON_LEFT);
        toggleIcon.setIconSize(34);
        toggleIcon.getStyleClass().add("custom-icon");
        toggleIcon.setOnMouseClicked(event -> {
            vbFolder.setVisible(!vbFolder.isVisible());
            if (vbFolder.isVisible()) {
                vbFolder.setManaged(true);
                toggleIcon.setIconCode(MaterialDesignC.CHEVRON_LEFT);
            } else {
                vbFolder.setManaged(false);
                toggleIcon.setIconCode(MaterialDesignC.CHEVRON_RIGHT);
            }
        });

        vbToogle.getChildren().add(toggleIcon);

        Button openFolderButton = new Button("Open Folder");
        FontIcon openIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openIcon.setIconSize(24);
        openFolderButton.setGraphic(openIcon);
        openFolderButton.getStyleClass().add("main-button");
        openFolderButton.setOnAction(event -> {
            loadFolderContent(primaryStage);
        });
        openFolderButton.setFocusTraversable(false);

        vbFolder.getChildren().addAll(openFolderButton);

        hbCenter.getChildren().addAll(vbToogle, vbFolder, tabPane);

        return hbCenter;
    }

    /**
     * This method is called in {@link #createEditorContent()} to create the content of a new {@link Tab}.
     * <p>
     * It instantiates a {@link TextArea} and a section for displaying secondary icons.
     * </p>
     *
     * @return a {@link VBox} containing the content of a new {@link Tab}.
     * @see #createEditorContent()
     * @see Tab
     * @see TextArea
     * @see VBox
     */
    private VBox createTabContent() {

        VBox tabContent = new VBox();
//        tabContent.setPadding(new Insets(20,0,0,0));
        VBox.setVgrow(tabContent, Priority.ALWAYS);
        tabContent.setBorder(border);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(20, 20, 20, 20));
        hbox.setBackground(new Background(new BackgroundFill(Color.rgb(162, 196, 201), CornerRadii.EMPTY, null)));
        VBox.setVgrow(hbox, Priority.ALWAYS);

        /* Editor section */

        TextArea textArea = new TextArea();
        textArea.setPadding(new Insets(10));
        textArea.setFont(Font.font("Arial", 14));
        HBox.setHgrow(textArea, Priority.ALWAYS);
        textArea.setFocusTraversable(false);

        GridPane textGrid = new GridPane();
        textGrid.add(editorModule.createLineNumberArea(textArea), 0, 0);
        textGrid.add(textArea, 1, 0);
        textGrid.setPadding(new Insets(0,10,0,0));
        GridPane.setHgrow(textArea, Priority.ALWAYS);
        GridPane.setVgrow(textArea, Priority.ALWAYS);
        HBox.setHgrow(textGrid, Priority.ALWAYS);

        /* All the icon button */
//        VBox iconsBox = createIconsBox(primaryStage, textArea);
        VBox iconsBox = editorModule.createIconsBox(primaryStage, textArea, tabPane);


        hbox.getChildren().addAll(textGrid, iconsBox);

        /* Line under the editor */
        HBox hboxInfoStrip = new HBox();
        hboxInfoStrip.setMinHeight(20);
        hboxInfoStrip.setAlignment(Pos.CENTER_RIGHT);
        hboxInfoStrip.setPadding(new Insets(0,10,0,0));
        hboxInfoStrip.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2, 0, 0, 0))));

        /* The position of the cursor */
        Text positionText = new Text();

        textArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            int caretPosition = newValue.intValue();
            int lineNumber = textArea.getText().substring(0, caretPosition).split("\n", -1).length;
            int columnNumber = caretPosition - textArea.getText().lastIndexOf('\n', caretPosition - 1);
            positionText.setText("Ln " + lineNumber + ", Col " + columnNumber);
        });

        hboxInfoStrip.getChildren().add(positionText);

        tabContent.getChildren().addAll(hbox, hboxInfoStrip);

        return tabContent;
    }

    /**
     * Creates an area showing line numbers for the given text area.
     * This method is called in {@link #createTabContent()}.
     * <p>
     * She uses a {@link TextArea#textProperty()} listener to monitor changes in the {@link TextArea}.
     * </p>
     *
     * @param textArea the text area for which to create line numbers.
     * @return a {@link VBox} containing the line numbers.
     */
    private VBox createLineNumberArea(TextArea textArea) {
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
    private VBox createIconsBox(Stage primaryStage, TextArea textArea) {

        /* All the icon button */

        VBox iconsBox = new VBox(10);
        iconsBox.setPadding(new Insets(10));
        iconsBox.setAlignment(Pos.TOP_RIGHT);

        FontIcon saveIcon = new FontIcon(MaterialDesignC.CONTENT_SAVE_ALL_OUTLINE);
        saveIcon.setIconSize(34);
        saveIcon.setOnMouseClicked(event -> editorModule.saveTextToFile(primaryStage, textArea, tabPane.getSelectionModel().getSelectedItem()));
        saveIcon.getStyleClass().add("custom-icon");
        Tooltip tooltipSave = new Tooltip("Save files");
        tooltipSave.setFont(Font.font(14));
        Tooltip.install(saveIcon, tooltipSave);

        FontIcon openIcon = new FontIcon(MaterialDesignF.FOLDER_OPEN);
        openIcon.setIconSize(34);
        openIcon.setOnMouseClicked(event -> editorModule.loadTextFromFile(primaryStage, textArea, tabPane.getSelectionModel().getSelectedItem()));
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
    private void saveTextToFile(Stage stage, TextArea textArea, Tab tab) {
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
    private void loadTextFromFile(Stage stage, TextArea textArea, Tab tab) {
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

    /* Method for the "open folder" button */

    /**
     * Opens a {@link DirectoryChooser} dialog for the user to select a folder.
     * <p>
     * If the user selects a folder, its content is displayed using the {@link #displayFolderContent(File)} method.
     * </p>
     * <p>
     * This method is used to {@code openFolderButton} action.
     * </p>
     *
     * @param primaryStage the primary stage of the application.
     *
     * @see DirectoryChooser
     * @see #displayFolderContent(File)
     */
    private void loadFolderContent(Stage primaryStage) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(primaryStage);
        if (selectedDirectory != null) {
            displayFolderContent(selectedDirectory);
        }
    }

    /**
     * Displays the content of the selected folder in the {@code vbFolder} {@link VBox}.
     * <p>
     * This method calls {@link #openFileInNewTab(File)} to display the contents of the double-clicked {@link File} in the editor.
     * </p>
     * <p>
     * And this method is used in {@link #loadFolderContent(Stage)} to link with the folder chosen by the user and display in a {@link ListView} the files contained with {@link Label}.
     * </p>
     *
     * @param folder the folder whose content will be displayed.
     *
     * @see VBox
     * @see #openFileInNewTab(File)
     * @see File
     * @see #loadFolderContent(Stage)
     * @see ListView
     * @see Label
     */
    private void displayFolderContent(File folder) {
        if (folder != null) {

            // Folder Name
            vbFolder.getChildren().clear();

            Label folderName = new Label(folder.getName());
            folderName.setFont(Font.font("Arial", 16));
            folderName.setPadding(new Insets(0, 0, 10, 0));

            FontIcon folderIcon = new FontIcon(MaterialDesignF.FOLDER_OUTLINE);
            folderIcon.setIconSize(20);

            folderName.setGraphic(folderIcon);

            vbFolder.getChildren().add(folderName);

            //List for nav in files
            ListView<File> fileListView = new ListView<>();
            fileListView.getStyleClass().add("custom-list-view");



            fileListView.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(File file, boolean empty) {
                    super.updateItem(file, empty);
                    if (empty || file == null) {
                        setText(null);
                        setGraphic(null);
                    } else {
                        FontIcon fileIcon = new FontIcon(MaterialDesignF.FILE_OUTLINE);
                        fileIcon.setIconSize(14);

                        setText(file.getName());
                        setGraphic(fileIcon);
                        getStyleClass().add("hand-on-button");
                    }
                }
            });

            fileListView.getItems().addAll(Objects.requireNonNull(folder.listFiles()));

            fileListView.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2) { // Double click to open
                    File selectedFile = fileListView.getSelectionModel().getSelectedItem();
                    if (selectedFile != null) {
                        openFileInNewTab(selectedFile);
                    }
                }
            });

            VBox.setMargin(fileListView, new Insets(0, 0, 0, 15));

            vbFolder.getChildren().add(fileListView);
        }
    }

    /**
     * Opens the content of the selected {@link File} in a new {@link Tab} for editing.
     * <p>
     * The file's content is loaded into a {@link TextArea} within the new {@link Tab}.
     * </p>
     * <p>
     * This method is called in {@link #displayFolderContent(File)} and uses {@link #createTabContent()} to display the new {@link Tab}.
     * </p>
     *
     * @param file the file to be opened in a new tab.
     *
     * @see File
     * @see Tab
     * @see TextArea
     * @see #displayFolderContent(File)
     * @see #createTabContent()
     */
    private void openFileInNewTab(File file) {
        try {
            String content = new String(Files.readAllBytes(file.toPath()));
            Tab newTab = new Tab(file.getName(), createTabContent());
            tabList.add(newTab);
            newTab.getStyleClass().add("MyTab");

            TextArea textArea = (TextArea) ((GridPane) ((HBox) ((VBox) newTab.getContent()).getChildren().get(0)).getChildren().get(0)).getChildren().get(1);
            textArea.setText(content);

            tabPane.getTabs().add(tabList.size() - 1, newTab);
            tabPane.getSelectionModel().select(newTab);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
