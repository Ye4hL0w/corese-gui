package fr.inria.corese.controller;

import com.brunomnsilva.smartgraph.graph.Digraph;
import fr.inria.corese.core.Graph;
import fr.inria.corese.core.query.QueryProcess;
import fr.inria.corese.kgram.api.core.Node;
import fr.inria.corese.kgram.core.Mapping;
import fr.inria.corese.kgram.core.Mappings;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.text.Font;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The {@link QueryController} class handles the execution of SPARQL queries against an RDF graph
 * and manages the display of query results.
 * <p>
 * It interacts with a {@link DataController} to access and manipulate the RDF graph and provides methods
 * for handling different types of SPARQL queries, including SELECT, ASK, CONSTRUCT, and UPDATE queries.
 * </p>
 */
public class QueryController {

    private DataController dataController;
    private Graph graph;
    private MFXTableView<Map<Node, Node>> mfxTableView;
    private Digraph<String, String> constructResultDigraph;
    private List<Mapping> currentMappings;

    /**
     * Constructs a new {@link QueryController} with the specified {@link DataController}.
     *
     * @param dataController the {@link DataController} used to access and manipulate the RDF graph.
     */
    public QueryController(DataController dataController) {
        this.dataController = dataController;
        this.graph = getGraph();
        this.mfxTableView = createTableView();
    }

    /**
     * Retrieves the {@link Graph} from the {@link DataController}.
     *
     * @return the current {@link Graph}.
     */
    private Graph getGraph() {
        return dataController.getGraph();
    }

    /**
     * Executes the given SPARQL query and handles the result based on the type of query.
     * <p>
     * The method handles SELECT, ASK, CONSTRUCT, and UPDATE queries differently and processes the results accordingly.
     * </p>
     *
     * @param query the SPARQL query to execute.
     */
    public void handleQueryExecution(String query) {
        QueryProcess exec = QueryProcess.create(graph);
        try {
            Mappings map = exec.query(query);

            if (query.toLowerCase().contains("select")) {
                handleSelectQuery(map);
            } else if (query.toLowerCase().contains("ask")) {
                handleAskQuery(map);
            } else if (query.toLowerCase().contains("construct")) {
                handleConstructQuery(map);
            } else if (query.toLowerCase().contains("insert") || query.toLowerCase().contains("delete")) {
                handleUpdateQuery(map);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error executing query: " + e.getMessage());
        }
    }

    /**
     * Handles the results of a SELECT query and updates the {@link MFXTableView} with the results.
     * <p>
     * This method processes the mappings from the query result and populates the table view with the retrieved data.
     * </p>
     *
     * @param map the {@link Mappings} object containing the results of the SELECT query.
     */
    private void handleSelectQuery(Mappings map) {
        currentMappings = map.getMappingList();

        if (map.isEmpty()) {
            System.out.println("No results found.");
            return;
        }

        List<Node> selectVars = map.getSelect();

        mfxTableView.getTableColumns().clear();

        MFXTableColumn<Map<Node, Node>> rowNumberColumn = new MFXTableColumn<>("Num", true);
        rowNumberColumn.setRowCellFactory(row -> new MFXTableRowCell<>(rowData -> {
            int index = mfxTableView.getItems().indexOf(rowData) + 1;
            return String.valueOf(index);
        }));
        rowNumberColumn.setPrefWidth(100);
        rowNumberColumn.setFont(new Font(16));
        rowNumberColumn.setStyle("-fx-border-color: #dcdcdc; -fx-border-width: 0 1px 0 0;");
        mfxTableView.getTableColumns().add(rowNumberColumn);

        for (Node var : selectVars) {
            MFXTableColumn<Map<Node, Node>> column = new MFXTableColumn<>(var.toString(), true);
            column.setRowCellFactory(row -> new MFXTableRowCell<>(rowData -> {
                Node node = rowData.get(var);
                return node != null ? node.toString() : "null";
            }));
            column.setPrefWidth(600);
            column.setFont(new Font(16));
            column.setStyle("-fx-border-color: #dcdcdc; -fx-border-width: 0 1px 0 0;");
            mfxTableView.getTableColumns().add(column);
        }

        ObservableList<Map<Node, Node>> data = FXCollections.observableArrayList();
        for (Mapping m : map) {
            Map<Node, Node> row = new HashMap<>();
            for (Node var : selectVars) {
                row.put(var, m.getValue(var));
            }
            data.add(row);
        }

        mfxTableView.setItems(data);
    }

    /**
     * Handles the result of an ASK query and prints the result to the console.
     * <p>
     * The method checks if the ASK query returned any results and prints a boolean indicating the presence of results.
     * </p>
     *
     * @param map the {@link Mappings} object containing the result of the ASK query.
     */
    private void handleAskQuery(Mappings map) {
        boolean result = !map.isEmpty();
        System.out.println("ASK query result: " + result);
    }

    /**
     * Handles the result of a CONSTRUCT query and prints the resulting graph as a string to the console.
     * <p>
     * The method converts the resulting graph to a string and prints it to the console.
     * </p>
     *
     * @param map the {@link Mappings} object containing the result of the CONSTRUCT query.
     */
    private void handleConstructQuery(Mappings map) {
        Graph resultGraph = (Graph) map.getGraph();
        String graphString = resultGraph.display();
        System.out.println("CONSTRUCT query result:\n" + graphString);
    }

    // This method commented below is used to construct the graphical result of the build query, it is not implemented correctly, so it does not work
    //    private void handleConstructQuery(Mappings map) {
//        Graph resultGraph = (Graph) map.getGraph();
//
//        Map<String, Set<String>> constructResultDigraph = new HashMap<>();
//
//        Iterator<Edge> edges = (Iterator<Edge>) resultGraph.getEdges();
//        while (edges.hasNext()) {
//            Edge edge = edges.next();
//
//            Node subject = edge.getSubjectNode();
//            Node predicate = edge.getPredicateValue();
//            Node object = edge.getObjectNode();
//
//            String subjectStr = subject.toString();
//            String predicateStr = predicate.toString();
//            String objectStr = object.toString();
//
//            constructResultDigraph.putIfAbsent(subjectStr, new HashSet<>());
//            constructResultDigraph.putIfAbsent(objectStr, new HashSet<>());
//
//            constructResultDigraph.get(subjectStr).add(objectStr + " (" + predicateStr + ")");
//        }
//
//        System.out.println("CONSTRUCT query result: Graph data updated.");
//        for (Map.Entry<String, Set<String>> entry : constructResultDigraph.entrySet()) {
//            String vertex = entry.getKey();
//            Set<String> edgesSet = entry.getValue();
//            System.out.println(vertex + " has edges: " + edgesSet);
//        }
//    }

    /**
     * Handles the result of an UPDATE query and prints a success message to the console.
     * <p>
     * The method indicates that the update query has been executed successfully.
     * </p>
     *
     * @param map the {@link Mappings} object containing the result of the UPDATE query.
     */
    private void handleUpdateQuery(Mappings map) {
        System.out.println("Update query executed successfully.");
    }

    /* Method for Text Result XML */

    /**
     * Converts the current query results to an XML format.
     * <p>
     * This method generates an XML representation of the query results based on the current mappings.
     * </p>
     *
     * @return an XML string representing the query results.
     */
    public String convertResultsToXML() {
        if (currentMappings == null || currentMappings.isEmpty()) {
            return "<results>No data available</results>";
        }

        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<results>\n");

        List<Node> selectVars = List.of(currentMappings.get(0).getSelect());

        for (Mapping mapping : currentMappings) {
            xmlBuilder.append("  <result>\n");
            for (Node var : selectVars) {
                xmlBuilder.append("    <").append(var.toString()).append(">")
                        .append(mapping.getValue(var).toString())
                        .append("</").append(var.toString()).append(">\n");
            }
            xmlBuilder.append("  </result>\n");
        }

        xmlBuilder.append("</results>");
        return xmlBuilder.toString();
    }

    /**
     * Returns the current XML representation of the query results.
     *
     * @return the XML string representing the query results.
     */
    public String getXMLResult() {
        return convertResultsToXML();
    }


    /* Method for TableView */

    /**
     * Creates and configures a new {@link MFXTableView} for displaying query results.
     *
     * @return a new {@link MFXTableView} configured for displaying query results.
     */
    private MFXTableView<Map<Node, Node>> createTableView() {
        MFXTableView<Map<Node, Node>> tableView = new MFXTableView<>();
        tableView.setPrefWidth(1050);
        tableView.setPrefHeight(600);
        tableView.setStyle("-fx-font-size: 14px;");
        return tableView;
    }

    /**
     * Returns the {@link MFXTableView} used for displaying query results.
     *
     * @return the {@link MFXTableView} instance.
     */
    public MFXTableView<Map<Node, Node>> getTableView() {
        return mfxTableView;
    }

    /* Method for Diagraph */

    /**
     * Returns the {@link Digraph} created from the results of a CONSTRUCT query.
     *
     * @return the {@link Digraph} representing the CONSTRUCT query result.
     */
    public Digraph<String, String> getDigraph() {
        return constructResultDigraph;
    }
}
