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
import javafx.stage.Stage;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.Icon;
import org.kordamp.ikonli.materialdesign2.*;

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

    public VBox createValidation() {
        VBox vbValidation = new VBox();
        vbValidation.setSpacing(10);
        vbValidation.setPadding(new Insets(20));

        HBox hbox = createEditorContent();
        vbValidation.getChildren().addAll(hbox);

        return vbValidation;
    }

    /* Validation Editor */

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
        VBox.setVgrow(hbCenter, Priority.ALWAYS);
        hbCenter.setAlignment(Pos.CENTER);
//        vbCenter.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, null)));

        /* File section */

        tabPane = new TabPane();
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

        hbCenter.getChildren().addAll(tabPane);

        return hbCenter;
    }

    private VBox createTabContent() {

        VBox tabContent = new VBox();
        VBox.setVgrow(tabContent, Priority.ALWAYS);
        tabContent.setBorder(border);

        HBox hbox = new HBox();
        hbox.setPadding(new Insets(20, 20, 20, 20));
        hbox.setBackground(new Background(new BackgroundFill(Color.rgb(162, 196, 201), CornerRadii.EMPTY, null)));
        VBox.setVgrow(hbox, Priority.ALWAYS);

        /* Editor section */

        StackPane stackPane = new StackPane();

//        TextArea textArea = new TextArea();
//        textArea.setPadding(new Insets(10));
//        textArea.setFont(Font.font("Arial", 14));
//        HBox.setHgrow(textArea, Priority.ALWAYS);
//        textArea.setFocusTraversable(false);
//        textArea.setMaxHeight(600);

        CodeArea codeArea = new CodeArea();
        codeArea.setPadding(new Insets(10));
//        codeArea.setFont(Font.font("Arial", 14));
        HBox.setHgrow(codeArea, Priority.ALWAYS);
        codeArea.setFocusTraversable(false);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setMaxHeight(600);

        Button runButton = new Button("Run");
        FontIcon runIcon = new FontIcon(MaterialDesignP.PLAY_OUTLINE);
        runIcon.setIconSize(34);
        runButton.setGraphic(runIcon);
        runButton.setOnAction(event -> {

        });
        runButton.getStyleClass().add("main-button");

        StackPane.setAlignment(runButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(runButton, new Insets(0, 25, 25, 0));

        stackPane.getChildren().addAll(codeArea, runButton);

        VBox vbResult = createResultContent();
        VBox.setVgrow(vbResult, Priority.ALWAYS);
        vbResult.setPadding(new Insets(10, 0, 0, 0));

        GridPane textGrid = new GridPane();
//        textGrid.add(editorModule.createLineNumberArea(textArea), 0, 0);
        textGrid.add(stackPane, 1, 0);
        textGrid.add(vbResult, 1, 1);
        textGrid.setPadding(new Insets(0,10,0,0));
        textGrid.setVgap(20);
        GridPane.setHgrow(stackPane, Priority.ALWAYS);
        GridPane.setVgrow(stackPane, Priority.ALWAYS);
        HBox.setHgrow(textGrid, Priority.ALWAYS);

        RowConstraints row1 = new RowConstraints();
        row1.setPercentHeight(66.67);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(33.33);
        textGrid.getRowConstraints().addAll(row1, row2);

        /* All the icon button */
//        VBox iconsBox = createIconsBox(primaryStage, textArea);
        VBox iconsBox = editorModule.createIconsBox(primaryStage, new CodeArea(), tabPane);

        hbox.getChildren().addAll(textGrid, iconsBox);

        /* Line under the editor */
        HBox hboxInfoStrip = new HBox();
        hboxInfoStrip.setMinHeight(20);
        hboxInfoStrip.setAlignment(Pos.CENTER_RIGHT);
        hboxInfoStrip.setPadding(new Insets(0,10,0,0));
        hboxInfoStrip.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2, 0, 0, 0))));

        /* The position of the cursor */
        Text positionText = new Text();

        codeArea.caretPositionProperty().addListener((observable, oldValue, newValue) -> {
            int caretPosition = newValue.intValue();
            int lineNumber = codeArea.getText().substring(0, caretPosition).split("\n", -1).length;
            int columnNumber = caretPosition - codeArea.getText().lastIndexOf('\n', caretPosition - 1);
            positionText.setText("Ln " + lineNumber + ", Col " + columnNumber);
        });

        hboxInfoStrip.getChildren().add(positionText);

        tabContent.getChildren().addAll(hbox, hboxInfoStrip);

        return tabContent;
    }

    /* Validation result */

    public VBox createResultContent() {
        VBox vbox = new VBox();
        VBox.setVgrow(vbox, Priority.ALWAYS);
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        return vbox;
    }

}
