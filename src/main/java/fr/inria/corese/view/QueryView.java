package fr.inria.corese.view;

import com.brunomnsilva.smartgraph.graph.Digraph;
import com.brunomnsilva.smartgraph.graph.DigraphEdgeList;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartPlacementStrategy;
import fr.inria.corese.aDemo.MaterialFx;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.materialdesign2.*;

import java.util.Comparator;

public class QueryView {

    private EditorModule editorModule = new EditorModule();

    private Stage primaryStage;
    private Border border;
    private TabPane tabPane;

    private ObservableList<Tab> tabList = FXCollections.observableArrayList();

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

        TextArea textArea = new TextArea();
        textArea.setPadding(new Insets(10));
        textArea.setFont(Font.font("Arial", 14));
        HBox.setHgrow(textArea, Priority.ALWAYS);
        textArea.setFocusTraversable(false);

        /* Import icon */

        FontIcon importIcon = new FontIcon(MaterialDesignA.APPLICATION_IMPORT);
        importIcon.setIconSize(34);
        importIcon.setOnMouseClicked(event -> {

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

        });
        runButton.getStyleClass().add("main-button");

        StackPane.setAlignment(runButton, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(runButton, new Insets(0, 25, 25, 0));

        stackPane.getChildren().addAll(textArea, importIcon, exportIcon, runButton);

        VBox vbResult = createResultContent();
        VBox.setVgrow(vbResult, Priority.ALWAYS);
        vbResult.setPadding(new Insets(10, 0, 0, 0));

        GridPane textGrid = new GridPane();
        textGrid.add(editorModule.createLineNumberArea(textArea), 0, 0);
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

        /* All the icon button */
//        VBox iconsBox = editorModule.createIconsBox(primaryStage, textArea, tabPane);

        hbox.getChildren().addAll(textGrid);

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

    public VBox createResultContent() {
        VBox vbox = new VBox();
        VBox.setVgrow(vbox, Priority.ALWAYS);

        SplitPane splitPane = new SplitPane();
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        TabPane tbResult = new TabPane();
        VBox.setVgrow(tbResult, Priority.ALWAYS);

        Tab text = new Tab("", createTextResult());
        FontIcon textIcon = new FontIcon(MaterialDesignF.FORMAT_TEXT);
        textIcon.setIconSize(34);
        text.setGraphic(textIcon);
        text.setClosable(false);
        text.getStyleClass().add("query-tab");

        Tab table = new Tab("", createTableResult());
        FontIcon tableIcon = new FontIcon(MaterialDesignT.TABLE);
        tableIcon.setIconSize(34);
        table.setGraphic(tableIcon);
        table.setClosable(false);
        table.getStyleClass().add("query-tab");

        Tab graph = new Tab("", createGraphResult());
        FontIcon graphIcon = new FontIcon(MaterialDesignG.GRAPH_OUTLINE);
        graphIcon.setIconSize(34);
        graph.setGraphic(graphIcon);
        graph.setClosable(false);
        graph.getStyleClass().add("query-tab");

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
            }
        });
    }

    private VBox createTextResult() {
        VBox vbox = new VBox();
        vbox.setPadding(new Insets(10));
        vbox.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(2))));
        vbox.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        HBox hBox = new HBox();

        ComboBox<String> cbTypeOfFile = new ComboBox<>();
        cbTypeOfFile.getItems().addAll("XML", "JSON", "CSV", "TSV");

        Pane spacer = new Pane();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        FontIcon exportIcon = new FontIcon(MaterialDesignA.APPLICATION_EXPORT);
        exportIcon.setIconSize(34);
        exportIcon.setOnMouseClicked(event -> {

        });
        exportIcon.getStyleClass().add("custom-icon");
        Tooltip exportTooltip = new Tooltip("Export file");
        exportTooltip.setFont(Font.font(14));
        Tooltip.install(exportIcon, exportTooltip);

        hBox.getChildren().addAll(cbTypeOfFile, spacer, exportIcon);

        Label l  = new Label("Text");
        vbox.getChildren().addAll(hBox, l);

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

        });
        exportIcon.getStyleClass().add("custom-icon");
        Tooltip exportTooltip = new Tooltip("Export file");
        exportTooltip.setFont(Font.font(14));
        Tooltip.install(exportIcon, exportTooltip);


        StackPane.setAlignment(exportIcon, Pos.TOP_RIGHT);

        // TABLE
        MFXTableView<MaterialFx.Person> mfxTableView = new MFXTableView<>();

        MFXTableColumn<MaterialFx.Person> firstNameColumn = new MFXTableColumn<>("First Name", true,
                Comparator.comparing(MaterialFx.Person::getFirstName));
        firstNameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(MaterialFx.Person::getFirstName));

        MFXTableColumn<MaterialFx.Person> lastNameColumn = new MFXTableColumn<>("Last Name", true,
                Comparator.comparing(MaterialFx.Person::getLastName));
        lastNameColumn.setRowCellFactory(person -> new MFXTableRowCell<>(MaterialFx.Person::getLastName));

        MFXTableColumn<MaterialFx.Person> ageColumn = new MFXTableColumn<>("Age", true, Comparator.comparingInt(MaterialFx.Person::getAge));
        ageColumn.setRowCellFactory(person -> new MFXTableRowCell<>(p -> String.valueOf(p.getAge())));

        mfxTableView.getTableColumns().addAll(firstNameColumn, lastNameColumn, ageColumn);

        ObservableList<MaterialFx.Person> data = FXCollections.observableArrayList(
                new MaterialFx.Person("Jérémy", "Moncada", 30),
                new MaterialFx.Person("Marty", "McFly", 25),
                new MaterialFx.Person("Paul", "Bond", 25),
                new MaterialFx.Person("Jacques", "Marty", 25),
                new MaterialFx.Person("Léa", "Totk", 25),
                new MaterialFx.Person("Pierre", "Teso", 25),
                new MaterialFx.Person("Pedro", "Peter", 25),
                new MaterialFx.Person("Nox", "Pitt", 25),
                new MaterialFx.Person("Dark", "Vador", 40));
        mfxTableView.setItems(data);

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
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        FontIcon exportIcon = new FontIcon(MaterialDesignA.APPLICATION_EXPORT);
        exportIcon.setIconSize(34);
        exportIcon.setOnMouseClicked(event -> {

        });
        exportIcon.getStyleClass().add("custom-icon");
        Tooltip exportTooltip = new Tooltip("Export file");
        exportTooltip.setFont(Font.font(14));
        Tooltip.install(exportIcon, exportTooltip);


        StackPane.setAlignment(exportIcon, Pos.TOP_RIGHT);

        // GRAPH
        Digraph<String, String> graph = new DigraphEdgeList<>();

        Vertex<String> vertexA = graph.insertVertex("ex:Alice");
        Vertex<String> vertexB = graph.insertVertex("ex:Person");
        Vertex<String> vertexC = graph.insertVertex("ex:KitKat");

        graph.insertEdge(vertexA, vertexB, "rdf:type");
        graph.insertEdge(vertexA, vertexC, "ex:worksFor");


        SmartPlacementStrategy strategy = new SmartCircularSortedPlacementStrategy();
        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(graph, strategy);

        graphView.getStylableVertex("ex:Alice").setStyleClass("myVertex");


        stackPane.getChildren().addAll(graphView, exportIcon);
        vbox.getChildren().add(stackPane);

        graphView.setAutomaticLayout(true);
        return vbox;
    }

}
