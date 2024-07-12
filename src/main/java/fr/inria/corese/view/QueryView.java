package fr.inria.corese.view;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import fr.inria.corese.aDemo.MaterialFx;
import fr.inria.corese.controller.DataController;
import fr.inria.corese.controller.QueryController;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.mfxcore.utils.fx.SwingFXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import org.fxmisc.richtext.model.StyleSpans;
import org.fxmisc.richtext.model.StyleSpansBuilder;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The {@link QueryView} class represents the view for executing and displaying the results of SPARQL queries.
 * <p>
 * It provides a user interface for inputting queries, running them, and viewing the results in various formats.
 * </p>
 */
public class QueryView {

    private EditorModule editorModule = new EditorModule();

    private Stage primaryStage;
    private Border border;
    private TabPane tabPane;

    private Label textData;
    private MFXTableView<MaterialFx.Person> mfxTableView;
    private SmartGraphPanel<String, String> graphView;

    private ObservableList<Tab> tabList = FXCollections.observableArrayList();

    private DataController dataController = new DataController();
    private QueryController queryController;

    /* Patterns for syntax highlighting */

    private static final String PREFIX_PATTERN = "(?i)PREFIX\\s+[\\w:]+\\s*";
    private static final String URL_PATTERN = "<[^>]+>";
    private static final String IRI_PATTERN = "(?:[\\w:]+\\s*)?\\<[^>]+\\>";
    private static final String LITERAL_PATTERN = "\"(?:[^\"\\\\]|\\\\.)*\"";
    private static final String COMMENT_PATTERN = "#.*";
    private static final String VARIABLE_PATTERN = "(\\?\\w+|\\$\\w+)";

    private static final String SPARQL_KEYWORD_PATTERN = "(?i)\\b(SELECT|WHERE|FILTER|OPTIONAL|UNION|GROUP\\s+BY|ORDER\\s+BY|LIMIT|OFFSET|DESCRIBE|CONSTRUCT|ASK)\\b";
    private static final String SPARQL_FUNCTION_PATTERN = "(?i)\\b(STR|LANG|LANGMATCHES|DATATYPE|BOUND|IRI|URI|BNODE|RAND|ABS|CEIL|FLOOR|ROUND|CONCAT|SUBSTR|STRLEN|UCASE|LCASE|ENCODE_FOR_URI|REPLACE|EXISTS|REGEX)\\b";

    private static final Pattern SPARQL_PATTERN = Pattern.compile(
            "(?<KEYWORD>" + SPARQL_KEYWORD_PATTERN + ")" +
                    "|(?<FUNCTION>" + SPARQL_FUNCTION_PATTERN + ")" +
                    "|(?<VARIABLE>" + VARIABLE_PATTERN + ")" +
                    "|(?<URL>" + URL_PATTERN + ")" +
                    "|(?<IRI>" + IRI_PATTERN + ")" +
                    "|(?<LITERAL>" + LITERAL_PATTERN + ")" +
                    "|(?<COMMENT>" + COMMENT_PATTERN + ")" +
                    "|(?<PREFIX>" + PREFIX_PATTERN + ")"
    );

    /**
     * Constructs a new QueryView.
     * <p>
     * Initializes the layout and components of the view.
     * </p>
     * <p>
     * Allows you to retrieve the instance of {@link DataController}.
     * </p>
     *
     * @param dataController the controller for managing data operations.
     *
     */
    public QueryView(DataController dataController) {
        this.queryController = new QueryController(dataController);
    }

    /**
     * Gets the main view of the query interface.
     *
     * @return a {@link VBox} containing the query interface.
     */
    public VBox getView() {
        VBox query = createQuery();
        return query;
    }

    /**
     * Creates the main query interface.
     *
     * @return a {@link VBox} containing the query interface.
     */
    public VBox createQuery(){
        VBox vbQuery = new VBox();
        vbQuery.setSpacing(10);
        vbQuery.setPadding(new Insets(10));

        HBox hBox = createEditorContent();
        vbQuery.getChildren().add(hBox);

        return vbQuery;
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

    /**
     * This method is called in {@link #createEditorContent()} to create the content of a new {@link Tab}.
     * <p>
     * It instantiates a {@link CodeArea}.
     * </p>
     *
     * @return a {@link VBox} containing the content of a new {@link Tab}.
     *
     * @see #createEditorContent()
     * @see Tab
     * @see CodeArea
     * @see VBox
     */
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
        codeArea.setStyle("-fx-font-size: 16px;");
        HBox.setHgrow(codeArea, Priority.ALWAYS);
        codeArea.setFocusTraversable(false);
        codeArea.setParagraphGraphicFactory(LineNumberFactory.get(codeArea));

        codeArea.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .successionEnds(Duration.ofMillis(500))
                .subscribe(ignore -> codeArea.setStyleSpans(0, computeHighlighting(codeArea.getText())));

        /* Import icon */

        FontIcon importIcon = new FontIcon(MaterialDesignA.APPLICATION_IMPORT);
        importIcon.setIconSize(34);
        importIcon.setOnMouseClicked(event -> {
            importFile(codeArea, tabContent.getScene().getWindow(), tabPane.getSelectionModel().getSelectedItem());
        });
        importIcon.getStyleClass().add("custom-icon");
        Tooltip importTooltip = new Tooltip("Import file");
        importTooltip.setFont(Font.font(14));
        Tooltip.install(importIcon, importTooltip);

        StackPane.setAlignment(importIcon, Pos.TOP_RIGHT);
        StackPane.setMargin(importIcon, new Insets(25,25,0,0));

        /* Export icon */

        FontIcon exportIcon = new FontIcon(MaterialDesignA.APPLICATION_EXPORT);
        exportIcon.setIconSize(34);
        exportIcon.setOnMouseClicked(event -> {
            exportFile(codeArea, tabContent.getScene().getWindow());
        });
        exportIcon.getStyleClass().add("custom-icon");
        Tooltip exportTooltip = new Tooltip("Export file");
        exportTooltip.setFont(Font.font(14));
        Tooltip.install(exportIcon, exportTooltip);

        StackPane.setAlignment(exportIcon, Pos.TOP_RIGHT);
        StackPane.setMargin(exportIcon, new Insets(65,25,0,0));

        /* Run icon */

        Button runButton = new Button("Run");
        FontIcon runIcon = new FontIcon(MaterialDesignP.PLAY_OUTLINE);
        runIcon.setIconSize(34);
        runButton.setGraphic(runIcon);
        runButton.setOnAction(event -> {
            String query = codeArea.getText();
            queryController.handleQueryExecution(query);
        });
        runButton.getStyleClass().add("main-button");

        StackPane.setAlignment(runButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(runButton, new Insets(0, 25, 25, 0));

        stackPane.getChildren().addAll(codeArea, importIcon, exportIcon, runButton);

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
        row1.setPercentHeight(33.33);
        RowConstraints row2 = new RowConstraints();
        row2.setPercentHeight(66.67);
        textGrid.getRowConstraints().addAll(row1, row2);

        hbox.getChildren().addAll(textGrid);

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

    /**
     * Creates the result content view for displaying query results.
     *
     * @return a {@link VBox} containing the result content.
     */
    public VBox createResultContent() {
        VBox vbox = new VBox();
        VBox.setVgrow(vbox, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane();
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        TabPane tbResult = new TabPane();
        VBox.setVgrow(tbResult, Priority.ALWAYS);

        Tooltip tooltipDrag = new Tooltip("Move me");
        tooltipDrag.setFont(Font.font(14));

        Tab text = new Tab("", createTextResult());
        FontIcon textIcon = new FontIcon(MaterialDesignF.FORMAT_TEXT);
        textIcon.setIconSize(34);
        text.setGraphic(textIcon);
        text.setClosable(false);
        text.getStyleClass().add("query-tab");
        Tooltip.install(textIcon, tooltipDrag);

        Tab table = new Tab("", createTableResult());
        FontIcon tableIcon = new FontIcon(MaterialDesignT.TABLE);
        tableIcon.setIconSize(34);
        table.setGraphic(tableIcon);
        table.setClosable(false);
        table.getStyleClass().add("query-tab");
        Tooltip.install(tableIcon, tooltipDrag);

        Tab graph = new Tab("", createGraphResult());
        FontIcon graphIcon = new FontIcon(MaterialDesignG.GRAPH_OUTLINE);
        graphIcon.setIconSize(34);
        graph.setGraphic(graphIcon);
        graph.setClosable(false);
        graph.getStyleClass().add("query-tab");
        Tooltip.install(graphIcon, tooltipDrag);

        tbResult.getTabs().addAll(text, table, graph);

        vbox.getChildren().add(splitPane);
        splitPane.getItems().add(tbResult);

        for (Tab tab : tbResult.getTabs()) {
            addDragAndDropSupport(tab, tbResult, splitPane);
        }

        return vbox;
    }

    /**
     * Adds drag-and-drop support to a given tab within a {@link TabPane} and {@link SplitPane}.
     * <p>
     * Allows the tab to be dragged out of the tab pane into a new tab pane within the split pane.
     * </p>
     *
     * @param tab the tab to which drag-and-drop support is added
     * @param tabPane the original tab pane containing the tab
     * @param splitPane the split pane that may contain the new tab pane
     */
    private void addDragAndDropSupport(Tab tab, TabPane tabPane, SplitPane splitPane) {
        tab.getGraphic().setOnDragDetected(event -> {
            tabPane.getTabs().remove(tab);
            TabPane newPane = new TabPane(tab);
            splitPane.getItems().add(newPane);
        });

        tab.getGraphic().setOnDragDone(event -> {
            if (event.getTransferMode() == TransferMode.MOVE) {
                splitPane.getItems().remove(tab.getContent());
                tabPane.getTabs().add(tab);
                removeEmptyTabPane(splitPane);
            }
        });
    }

    /**
     * Removes empty tab panes from a {@link SplitPane}.
     *
     * @param splitPane the split pane from which empty tab panes are removed
     */
    private void removeEmptyTabPane(SplitPane splitPane) {
        for (Iterator<Node> iterator = splitPane.getItems().iterator(); iterator.hasNext(); ) {
            Node node = iterator.next();
            if (node instanceof TabPane) {
                TabPane pane = (TabPane) node;
                if (pane.getTabs().isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Creates a {@link VBox} containing text result with export functionality and file type selection.
     *
     * @return a {@link VBox} containing the text result
     */
    private VBox createTextResult() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox hBox = new HBox();

        ComboBox<String> cbTypeOfFile = new ComboBox<>();
        cbTypeOfFile.getItems().addAll("XML", "JSON", "CSV", "TSV");
        cbTypeOfFile.setValue("XML");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon exportIcon = new FontIcon(MaterialDesignA.APPLICATION_EXPORT);
        exportIcon.setIconSize(34);
        exportIcon.setOnMouseClicked(event -> {
            exportText();
        });
        exportIcon.getStyleClass().add("custom-icon");
        Tooltip exportTooltip = new Tooltip("Export file");
        exportTooltip.setFont(Font.font(14));
        Tooltip.install(exportIcon, exportTooltip);

        hBox.getChildren().addAll(cbTypeOfFile, spacer, exportIcon);

        textData  = new Label("Text");
        vbox.getChildren().addAll(hBox, textData);

//        textData.setText(queryController.getXMLResult());

        return vbox;
    }

    /**
     * Creates a {@link VBox} containing a table result with export functionality.
     * <p>
     * Use a {@link MFXTableView} to display the table.
     * </p>
     *
     * @return a {@link VBox} containing the table result
     *
     * @see MFXTableView
     */
    private VBox createTableResult() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        StackPane stackPane = new StackPane();
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        FontIcon exportIcon = new FontIcon(MaterialDesignA.APPLICATION_EXPORT);
        exportIcon.setIconSize(34);
        exportIcon.setOnMouseClicked(event -> {
            exportTable();
        });
        exportIcon.getStyleClass().add("custom-icon");
        Tooltip exportTooltip = new Tooltip("Export file");
        exportTooltip.setFont(Font.font(14));
        Tooltip.install(exportIcon, exportTooltip);

        StackPane.setAlignment(exportIcon, Pos.TOP_RIGHT);

        // TABLE
        MFXTableView<Map<fr.inria.corese.kgram.api.core.Node, fr.inria.corese.kgram.api.core.Node>> mfxTableView = queryController.getTableView();
//        mfxTableView.setPrefWidth(600);
//        mfxTableView.setPrefHeight(450);
//        mfxTableView.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        StackPane.setAlignment(mfxTableView, Pos.CENTER);

        stackPane.getChildren().addAll(exportIcon, mfxTableView);

        vbox.getChildren().add(stackPane);

        return vbox;
    }

    /**
     * Creates a {@link VBox} containing a graph result with export and zoom functionalities.
     * <p>
     * Use a {@link Digraph} to display the graph.
     * </p>
     *
     * @return a {@link VBox} containing the graph result
     *
     * @see Digraph
     */
    private VBox createGraphResult() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        StackPane stackPane = new StackPane();
        stackPane.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        FontIcon exportIcon = new FontIcon(MaterialDesignA.APPLICATION_EXPORT);
        exportIcon.setIconSize(34);
        exportIcon.setOnMouseClicked(event -> {
            exportGraphAsImage();
        });
        exportIcon.getStyleClass().add("custom-icon");
        Tooltip exportTooltip = new Tooltip("Export file");
        exportTooltip.setFont(Font.font(14));
        Tooltip.install(exportIcon, exportTooltip);

        StackPane.setAlignment(exportIcon, Pos.TOP_RIGHT);

        VBox vbDimension = new VBox();
        vbDimension.setPadding(new Insets(2));
        vbDimension.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        vbDimension.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
        vbDimension.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));

        FontIcon zoomInIcon = new FontIcon(MaterialDesignM.MAGNIFY_PLUS_OUTLINE);
        zoomInIcon.setIconSize(34);
        zoomInIcon.setOnMouseClicked(event -> {
            graphView.setScaleX(graphView.getScaleX() * 1.1);
            graphView.setScaleY(graphView.getScaleY() * 1.1);
        });
        zoomInIcon.getStyleClass().add("custom-icon");
        Tooltip zoomInTooltip = new Tooltip("Zoom In");
        zoomInTooltip.setFont(Font.font(14));
        Tooltip.install(zoomInIcon, zoomInTooltip);

        FontIcon zoomOutIcon = new FontIcon(MaterialDesignM.MAGNIFY_MINUS_OUTLINE);
        zoomOutIcon.setIconSize(34);
        zoomOutIcon.setOnMouseClicked(event -> {
            graphView.setScaleX(graphView.getScaleX() / 1.1);
            graphView.setScaleY(graphView.getScaleY() / 1.1);
        });
        zoomOutIcon.getStyleClass().add("custom-icon");
        Tooltip zoomOutTooltip = new Tooltip("Zoom Out");
        zoomOutTooltip.setFont(Font.font(14));
        Tooltip.install(zoomOutIcon, zoomOutTooltip);

        FontIcon recenterIcon = new FontIcon(MaterialDesignC.CROP_FREE);
        recenterIcon.setIconSize(34);
        recenterIcon.setOnMouseClicked(event -> {
            graphView.setScaleX(1);
            graphView.setScaleY(1);
        });
        recenterIcon.getStyleClass().add("custom-icon");
        Tooltip recenterTooltip = new Tooltip("Recenter");
        recenterTooltip.setFont(Font.font(14));
        Tooltip.install(recenterIcon, recenterTooltip);

        vbDimension.getChildren().addAll(zoomInIcon, zoomOutIcon, recenterIcon);

        StackPane.setAlignment(vbDimension, Pos.BOTTOM_RIGHT);

        // GRAPH
        Digraph<String, String> graph = new DigraphEdgeList<>();

        Vertex<String> vertexA = graph.insertVertex("ex:Alice");
        Vertex<String> vertexB = graph.insertVertex("ex:Person");
        Vertex<String> vertexC = graph.insertVertex("ex:KitKat");

        graph.insertEdge(vertexA, vertexB, "rdf:type");
        graph.insertEdge(vertexA, vertexC, "ex:worksFor");


        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        graphView = new SmartGraphPanel<>(graph, strategy);

        graphView.getStylableVertex("ex:Alice").setStyleClass("myVertex");

        stackPane.getChildren().addAll(graphView, exportIcon, vbDimension);

        vbox.getChildren().add(stackPane);

        graphView.setAutomaticLayout(true);

        /*Digraph<String, String> graph = queryController.getDigraph();

        if (graph != null) {
            graphView = new SmartGraphPanel<>(graph, new SmartCircularSortedPlacementStrategy());
            graphView.setAutomaticLayout(true);

            stackPane.getChildren().addAll(graphView, exportIcon, vbDimension);
        } else {
            Label noGraphLabel = new Label("No graph data available.");
            stackPane.getChildren().add(noGraphLabel);
        }

        vbox.getChildren().add(stackPane);

        if (graphView != null) {
            graphView.setAutomaticLayout(true);
        }*/

        return vbox;
    }

    /* Import method */

    /**
     * Imports a query file and loads its content into the provided {@link CodeArea}.
     *
     * @param codeArea the {@link CodeArea} to load the content into.
     * @param ownerWindow the parent window.
     * @param tab the currently selected tab.
     */
    private void importFile(CodeArea codeArea, Window ownerWindow, Tab tab) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showOpenDialog(ownerWindow);
        if (file != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                codeArea.clear();
                String line;
                while ((line = reader.readLine()) != null) {
                    codeArea.appendText(line + System.lineSeparator());
                }

                String fileName = file.getName();
                tab.setText(fileName);

            } catch (IOException e) {
                showAlert("Error", "An error occurred while importing the file: " + e.getMessage());
            }
        }
    }

    /* Export methods */

    /**
     * Exports the content of the provided {@link CodeArea} to a query file.
     *
     * @param codeArea the {@link CodeArea} containing the content to export.
     * @param ownerWindow the parent window.
     */
    private void exportFile(CodeArea codeArea, Window ownerWindow) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save File");
        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(ownerWindow);
        if (file != null) {
            // Ensure the file has the correct extension
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(codeArea.getText());
            } catch (IOException e) {
                showAlert("Error", "An error occurred while exporting the file: " + e.getMessage());
            }
        }
    }

    /**
     * Exports the text data to a .txt file using a {@link FileChooser} dialog.
     * If the user selects a file without a .txt extension, the method automatically adds the .txt extension.
     * Displays an error alert if an IOException occurs during file writing.
     */
    private void exportText() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Text File");

        fileChooser.getExtensionFilters().add(new ExtensionFilter("Text Files", "*.txt"));

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            if (!file.getName().endsWith(".txt")) {
                file = new File(file.getAbsolutePath() + ".txt");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(String.valueOf(textData));
            } catch (IOException e) {
                showAlert("Error", "An error occurred while saving the file: " + e.getMessage());
            }        }
    }

    /**
     * Exports the table data to a .csv file using a {@link FileChooser} dialog.
     * If the user selects a file without a .csv extension, the method automatically adds the .csv extension.
     * The table data is written in CSV format.
     * Displays an error alert if an IOException occurs during file writing.
     */
    private void exportTable() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save CSV File");
        fileChooser.getExtensionFilters().add(
                new ExtensionFilter("CSV Files", "*.csv"));

        File file = fileChooser.showSaveDialog(null);
        if (file != null) {
            if (!file.getName().endsWith(".csv")) {
                file = new File(file.getAbsolutePath() + ".csv");
            }
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("First Name,Last Name,Age");
                writer.newLine();
                for (MaterialFx.Person person : mfxTableView.getItems()) {
                    writer.write(String.format("%s,%s,%d",
                            person.getFirstName(),
                            person.getLastName(),
                            person.getAge()));
                    writer.newLine();
                }
            } catch (IOException e) {
                showAlert("Error", "An error occurred while saving the CSV file: " + e.getMessage());
            }
        }
    }

    /**
     * Exports the current graph view as a PNG image using a {@link FileChooser} dialog.
     * If the user selects a file without a .png extension, the method automatically adds the .png extension.
     * Takes a snapshot of the graph view and saves it as an image file.
     * Displays an error alert if an IOException occurs during image saving.
     */
    private void exportGraphAsImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.getExtensionFilters().addAll(
                new ExtensionFilter("PNG Images", "*.png")
//                new ExtensionFilter("JPG Images", "*.jpg"),
//                new ExtensionFilter("All Images", "*.*")
        );

        File file = fileChooser.showSaveDialog(null);

        if (file != null) {
            String extension = fileChooser.getSelectedExtensionFilter().getExtensions().get(0).replace("*.", "").toLowerCase();
            WritableImage writableImage = new WritableImage((int) graphView.getWidth(), (int) graphView.getHeight());
            graphView.snapshot(new SnapshotParameters(), writableImage);
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);
            try {
                ImageIO.write(bufferedImage, extension, file);
            } catch (IOException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "An error occurred while saving the file: " + e.getMessage(), ButtonType.OK);
                alert.showAndWait();
            }
        }
    }

    /* Alert method */

    /**
     * Displays an {@link Alert} dialog with the given title and message.
     *
     * @param title   the title of the alert dialog
     * @param message the message to be displayed in the alert dialog
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    /* Method for syntax highlighting */

    /**
     * Computes syntax highlighting spans for the given SPARQL query text.
     *
     * @param text the SPARQL query text.
     * @return a {@link StyleSpans<Collection<String>>} containing the syntax highlighting spans.
     */
    private StyleSpans<Collection<String>> computeHighlighting(String text) {
        Matcher matcher = SPARQL_PATTERN.matcher(text);
        int lastKwEnd = 0;
        StyleSpansBuilder<Collection<String>> spansBuilder = new StyleSpansBuilder<>();

        while (matcher.find()) {
            String styleClass =
                    matcher.group("KEYWORD") != null ? "keyword" :
                            matcher.group("FUNCTION") != null ? "function" :
                                    matcher.group("VARIABLE") != null ? "variable" :
                                            matcher.group("URL") != null ? "url" :
                                                    matcher.group("IRI") != null ? "iri" :
                                                            matcher.group("LITERAL") != null ? "literal" :
                                                                    matcher.group("COMMENT") != null ? "comment" :
                                                                            matcher.group("PREFIX") != null ? "prefix" :
                                                                                    null;
            assert styleClass != null;
            spansBuilder.add(Collections.emptyList(), matcher.start() - lastKwEnd);
            spansBuilder.add(Collections.singleton(styleClass), matcher.end() - matcher.start());
            lastKwEnd = matcher.end();
        }
        spansBuilder.add(Collections.emptyList(), text.length() - lastKwEnd);
        return spansBuilder.create();
    }
}
