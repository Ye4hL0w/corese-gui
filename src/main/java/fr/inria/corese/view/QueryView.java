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
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import io.github.palexdev.mfxcore.utils.fx.SwingFXUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public QueryView(DataController dataController) {
        this.queryController = new QueryController(dataController);
    }

    public VBox getView() {
        VBox query = createQuery();
        return query;
    }

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
        StackPane.setMargin(exportIcon, new Insets(60,25,0,0));

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

        textData.setText(queryController.getXMLResult());

        return vbox;
    }

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.setTitle(title);
        alert.showAndWait();
    }

    /* Method for syntax highlighting */

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
