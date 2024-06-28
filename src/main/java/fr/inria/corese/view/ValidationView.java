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

public class ValidationView {

    private EditorModule editorModule = new EditorModule();

    private Stage primaryStage;
    private Border border;
    private TabPane tabPane;

    private ObservableList<Tab> tabList = FXCollections.observableArrayList();

    public VBox getView() {
        VBox validation = createValidation();
        return validation;
    }
    /* First display */


    public VBox createValidation() {
        VBox vbValidation = new VBox();

        HBox hBox = createEditorContent(tabPane);

        vbValidation.getChildren().add(hBox);

        return vbValidation;
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
    public HBox createEditorContent(TabPane tabPane) {
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
        TabPane finalTabPane = tabPane;
        plusIcon.setOnMouseClicked(event -> {
            Tab newTab = new Tab("Untilted " + (finalTabPane.getTabs().size()), createTabContent());
            tabList.add(newTab);
            newTab.getStyleClass().add("MyTab");
            finalTabPane.getTabs().add(tabList.size() - 1, newTab);
            finalTabPane.getSelectionModel().select(newTab);
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


        hbCenter.getChildren().addAll(tabPane);

        return hbCenter;
    }

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



}
