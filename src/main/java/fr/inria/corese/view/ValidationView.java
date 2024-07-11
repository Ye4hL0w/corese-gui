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
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.Icon;
import org.kordamp.ikonli.materialdesign2.*;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationView {

    private EditorModule editorModule = new EditorModule();

    private Stage primaryStage;
    private Border border;
    private TabPane tabPane;

    private ObservableList<Tab> tabList = FXCollections.observableArrayList();

    /* Patterns for syntax highlighting */

    private static final String URI_PATTERN = "<[^>]*>";
    private static final String PREFIX_PATTERN = "@prefix\\s+[^\\s]*\\s*:";
    private static final String SHACL_KEYWORD_PATTERN = "\\b(?:a|sh:NodeShape|sh:targetClass|sh:path|sh:datatype)\\b";
    private static final String LITERAL_PATTERN = "\"[^\"]*\"";
    private static final String COMMENT_PATTERN = "#.*";
    private static final String CONSTRAINT_PATTERN = "\\b(?:minCount|maxCount|uniqueLang|pattern)\\b";

    private static final Pattern SHACL_PATTERN = Pattern.compile(
            "(?<URL>" + URI_PATTERN + ")"
                    + "|(?<PREFIX>" + PREFIX_PATTERN + ")"
                    + "|(?<KEYWORD>" + SHACL_KEYWORD_PATTERN + ")"
                    + "|(?<LITERAL>" + LITERAL_PATTERN + ")"
                    + "|(?<COMMENT>" + COMMENT_PATTERN + ")"
                    + "|(?<CONSTRAINT>" + CONSTRAINT_PATTERN + ")"
    );

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

        CodeArea codeArea = new CodeArea();
        codeArea.setPadding(new Insets(10));
        HBox.setHgrow(codeArea, Priority.ALWAYS);
        codeArea.setFocusTraversable(false);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));
        codeArea.setMaxHeight(600);
        codeArea.setStyle("-fx-font-size: 16px;");

        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

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
        VBox iconsBox = editorModule.createIconsBox(primaryStage, codeArea, tabPane);

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

    /* Method for syntax highlighting */

    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = SHACL_PATTERN.matcher(text);
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        int lastIndex = 0;
        while (matcher.find()) {
            String styleClass = null;
            if (matcher.group("URL") != null) {
                styleClass = "url";
            } else if (matcher.group("PREFIX") != null) {
                styleClass = "prefix";
            } else if (matcher.group("KEYWORD") != null) {
                styleClass = "keyword";
            } else if (matcher.group("LITERAL") != null) {
                styleClass = "literal";
            } else if (matcher.group("COMMENT") != null) {
                styleClass = "comment";
            } else if (matcher.group("CONSTRAINT") != null) {
                styleClass = "constraint";
            }
            if (styleClass != null) {
                spansBuilder.add(Collections.emptyList(), matcher.start() - lastIndex);
                spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
                lastIndex = matcher.end();
            }
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastIndex);

        return spansBuilder.create();
    }
}
